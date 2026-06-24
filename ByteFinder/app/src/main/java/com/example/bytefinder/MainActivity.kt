package com.example.bytefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bytefinder.data.ApiClient
import com.example.bytefinder.data.ApiService
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.ui.components.ClayBottomNav
import com.example.bytefinder.ui.components.NavTab
import com.example.bytefinder.ui.screens.ClayAccountScreen
import com.example.bytefinder.ui.screens.ClaySearchScreen
import com.example.bytefinder.ui.screens.ClayBusinessScreen
import com.example.bytefinder.ui.screens.ClayLoginScreen
import com.example.bytefinder.ui.screens.ClayMyReviewsScreen
import com.example.bytefinder.ui.screens.ClayPratoDetailScreen
import com.example.bytefinder.ui.screens.ClayPratoCompareScreen
import com.example.bytefinder.ui.screens.ClayRestaurantDetailScreen
import com.example.bytefinder.ui.screens.ClaySettingsScreen
import com.example.bytefinder.ui.screens.HomeScreen
import com.example.bytefinder.ui.screens.NearZoneScreen
import com.example.bytefinder.ui.screens.BiteOnboardingScreen
import com.example.bytefinder.ui.theme.BitefinderClayTheme
import com.example.bytefinder.ui.viewmodel.HomeViewModel
import com.example.bytefinder.ui.viewmodel.HomeViewModelFactory
import com.example.bytefinder.ui.viewmodel.AuthViewModel
import com.example.bytefinder.ui.viewmodel.AuthViewModelFactory

private enum class AppScreen {
    HOME,
    SEARCH,
    NEAR,
    ACCOUNT,
    BUSINESS,
    MY_REVIEWS,
    SETTINGS,
    DETAIL,
    RESTAURANT_DETAIL,
    PRATO_COMPARE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = ApiClient.retrofit.create(ApiService::class.java)
        val repository = DataRepository(api)

        setContent {
            BitefinderClayTheme {
                AppRoot(repository = repository, api = api)
            }
        }
    }
}

@Composable
private fun AppRoot(repository: DataRepository, api: com.example.bytefinder.data.ApiService) {
    val context = LocalContext.current
    val onboardingPrefs = remember {
        context.getSharedPreferences("bitefinder_prefs", android.content.Context.MODE_PRIVATE)
    }
    var token by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }
    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var currentUserRole by remember { mutableStateOf<String?>(null) }
    var currentUserRestaurants by remember { mutableStateOf<List<Int>>(emptyList()) }

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var detailReturnScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedPratoId by remember { mutableStateOf<Int?>(null) }
    var selectedRestauranteId by remember { mutableStateOf<Int?>(null) }
    var selectedTab by remember { mutableStateOf(NavTab.HOME) }
    var onboardingDone by rememberSaveable {
        mutableStateOf(onboardingPrefs.getBoolean("onboarding_done", false))
    }

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(api) // assuming DataRepository exposes api or factory accepts api
    )

    fun goHome() {
        homeViewModel.resetToDefault()
        selectedPratoId = null
        currentScreen = AppScreen.HOME
        selectedTab = NavTab.HOME
    }

    fun signOut() {
        ApiClient.setAuthToken(null)
        token = null
        userName = null
        currentUserId = null
        currentUserRole = null
        currentUserRestaurants = emptyList()
        selectedPratoId = null
        selectedRestauranteId = null
        currentScreen = AppScreen.HOME
        selectedTab = NavTab.HOME
    }

    val isRestaurantUser = currentUserRole == "restaurante"

    // Ecrãs que mostram a bottom nav
    val showBottomNav = token != null &&
        currentScreen !in listOf(AppScreen.DETAIL, AppScreen.MY_REVIEWS, AppScreen.SETTINGS, AppScreen.RESTAURANT_DETAIL, AppScreen.PRATO_COMPARE) &&
        !(currentScreen == AppScreen.BUSINESS && !isRestaurantUser)

    if (token == null && !onboardingDone) {
        BiteOnboardingScreen(onFinish = {
            onboardingDone = true
            onboardingPrefs.edit().putBoolean("onboarding_done", true).apply()
        })
    } else if (token == null) {
        ClayLoginScreen(
            repository = repository,
            authViewModel = authViewModel,
            onLoggedIn = { t, userId, nome, role, restaurantes ->
                ApiClient.setAuthToken(t)
                token = t
                currentUserId = userId
                userName = nome
                currentUserRole = role
                currentUserRestaurants = restaurantes
                if (role == "restaurante") {
                    currentScreen = AppScreen.BUSINESS
                    selectedTab = NavTab.NEAR
                } else {
                    currentScreen = AppScreen.HOME
                    selectedTab = NavTab.HOME
                }
            }
        )
    } else {
        Column(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            // ── Conteúdo principal (cresce, empurra a nav bar para baixo) ──
            Box(modifier = androidx.compose.ui.Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        val isTabSwitch = targetState in listOf(
                            AppScreen.HOME, AppScreen.SEARCH, AppScreen.NEAR, AppScreen.ACCOUNT, AppScreen.BUSINESS
                        ) && initialState in listOf(
                            AppScreen.HOME, AppScreen.SEARCH, AppScreen.NEAR, AppScreen.ACCOUNT, AppScreen.BUSINESS
                        )
                        if (isTabSwitch) {
                            fadeIn(tween(220)) togetherWith fadeOut(tween(180))
                        } else {
                            val direction = if (targetState.ordinal >= initialState.ordinal) 1 else -1
                            (slideInHorizontally(
                                animationSpec = tween(330, easing = FastOutSlowInEasing),
                                initialOffsetX = { direction * (it / 6) }
                            ) + fadeIn(tween(260))) togetherWith
                                (slideOutHorizontally(
                                    animationSpec = tween(230, easing = FastOutSlowInEasing),
                                    targetOffsetX = { -direction * (it / 8) }
                                ) + fadeOut(tween(190)))
                        }
                    },
                    label = "screen-motion"
                ) { screen ->
                    when (screen) {
                        AppScreen.HOME -> HomeScreen(
                            viewModel = homeViewModel,
                            repository = repository,
                            userName = userName ?: "Utilizador",
                            isRestaurantUser = (currentUserRole == "restaurante"),
                            nearModeActive = true,
                            onPratoClick = { pratoId ->
                                selectedPratoId = pratoId
                                detailReturnScreen = AppScreen.HOME
                                currentScreen = AppScreen.PRATO_COMPARE
                            },
                            onGoHome = { goHome() }
                        )

                        AppScreen.NEAR -> if (isRestaurantUser) {
                            ClayBusinessScreen(
                                repository = repository,
                                currentUserId = currentUserId ?: 0,
                                restauranteIds = currentUserRestaurants,
                                onBack = { goHome() },
                                onPratoClick = { pratoId ->
                                    selectedPratoId = pratoId
                                    detailReturnScreen = AppScreen.NEAR
                                    currentScreen = AppScreen.DETAIL
                                },
                                onGoHome = { goHome() }
                            )
                        } else {
                            NearZoneScreen(
                                viewModel = homeViewModel,
                                onPratoClick = { pratoId ->
                                    selectedPratoId = pratoId
                                    detailReturnScreen = AppScreen.NEAR
                                    currentScreen = AppScreen.PRATO_COMPARE
                                }
                            )
                        }

                        AppScreen.ACCOUNT -> ClayAccountScreen(
                            userName = userName ?: "Utilizador",
                            userRole = currentUserRole ?: "user",
                            isRestaurantUser = (currentUserRole == "restaurante"),
                            onOpenBusiness = {
                                currentScreen = AppScreen.BUSINESS
                            },
                            onOpenMyReviews = {
                                currentScreen = AppScreen.MY_REVIEWS
                            },
                            onOpenSettings = {
                                currentScreen = AppScreen.SETTINGS
                            },
                            onSignOut = { signOut() }
                        )

                        AppScreen.BUSINESS -> ClayBusinessScreen(
                            repository = repository,
                            currentUserId = currentUserId ?: 0,
                            restauranteIds = currentUserRestaurants,
                            onBack = {
                                if (isRestaurantUser) {
                                    currentScreen = AppScreen.NEAR
                                    selectedTab = NavTab.NEAR
                                } else {
                                    currentScreen = AppScreen.ACCOUNT
                                    selectedTab = NavTab.ACCOUNT
                                }
                            },
                            onPratoClick = { pratoId ->
                                selectedPratoId = pratoId
                                detailReturnScreen = AppScreen.BUSINESS
                                currentScreen = AppScreen.DETAIL
                            },
                            onGoHome = { goHome() }
                        )

                        AppScreen.MY_REVIEWS -> ClayMyReviewsScreen(
                            repository = repository,
                            userId = currentUserId ?: 0,
                            onBack = { currentScreen = AppScreen.ACCOUNT; selectedTab = NavTab.ACCOUNT },
                            onPratoClick = { pratoId ->
                                selectedPratoId = pratoId
                                detailReturnScreen = AppScreen.MY_REVIEWS
                                currentScreen = AppScreen.DETAIL
                            },
                            onGoHome = { goHome() }
                        )

                        AppScreen.SETTINGS -> ClaySettingsScreen(
                            onBack = { currentScreen = AppScreen.ACCOUNT; selectedTab = NavTab.ACCOUNT },
                            onGoHome = { goHome() }
                        )

                        AppScreen.SEARCH -> ClaySearchScreen(
                            viewModel = homeViewModel,
                            onPratoClick = { pratoId ->
                                selectedPratoId = pratoId
                                detailReturnScreen = AppScreen.SEARCH
                                currentScreen = AppScreen.PRATO_COMPARE
                            },
                            onCategorySelected = { category ->
                                homeViewModel.onCategorySelected(category)
                                selectedTab = NavTab.HOME
                                currentScreen = AppScreen.HOME
                            }
                        )

                        AppScreen.DETAIL -> ClayPratoDetailScreen(
                            repository = repository,
                            pratoId = selectedPratoId ?: 0,
                            currentUserId = currentUserId ?: 0,
                            restaurantIds = currentUserRestaurants,
                            onBack = { currentScreen = detailReturnScreen },
                            onGoHome = { goHome() },
                            onRestauranteClick = { restId ->
                                selectedRestauranteId = restId
                                currentScreen = AppScreen.RESTAURANT_DETAIL
                            }
                        )

                        AppScreen.RESTAURANT_DETAIL -> ClayRestaurantDetailScreen(
                            repository = repository,
                            restauranteId = selectedRestauranteId ?: 0,
                            onBack = { currentScreen = detailReturnScreen },
                            onGoHome = { goHome() },
                            onPratoClick = { pratoId ->
                                selectedPratoId = pratoId
                                detailReturnScreen = AppScreen.RESTAURANT_DETAIL
                                currentScreen = AppScreen.DETAIL
                            }
                        )

                        AppScreen.PRATO_COMPARE -> ClayPratoCompareScreen(
                            repository = repository,
                            pratoId = selectedPratoId ?: 0,
                            selectedCity = homeViewModel.state.value.selectedCity,
                            onBack = { currentScreen = detailReturnScreen },
                            onGoHome = { goHome() },
                            onPratoDetailClick = { pratoId ->
                                selectedPratoId = pratoId
                                detailReturnScreen = AppScreen.PRATO_COMPARE
                                currentScreen = AppScreen.DETAIL
                            },
                            onRestauranteClick = { restId ->
                                selectedRestauranteId = restId
                                detailReturnScreen = AppScreen.PRATO_COMPARE
                                currentScreen = AppScreen.RESTAURANT_DETAIL
                            }
                        )
                    }
                }
            }

            // ── Bottom Navigation Bar ──────────────────────────────────
            if (showBottomNav) {
                ClayBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        if (tab == NavTab.HOME) {
                            goHome()
                        } else {
                            selectedTab = tab
                            currentScreen = when (tab) {
                                NavTab.HOME    -> AppScreen.HOME
                                NavTab.SEARCH  -> AppScreen.SEARCH
                                NavTab.NEAR    -> if (isRestaurantUser) AppScreen.BUSINESS else AppScreen.NEAR
                                NavTab.ACCOUNT -> AppScreen.ACCOUNT
                            }
                        }
                    },
                    isRestaurantUser = isRestaurantUser
                )
            }
        }
    }
}


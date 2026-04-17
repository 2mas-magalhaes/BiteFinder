package com.example.bytefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bytefinder.data.ApiClient
import com.example.bytefinder.data.ApiService
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.ui.screens.ClayBusinessScreen
import com.example.bytefinder.ui.screens.ClayLoginScreen
import com.example.bytefinder.ui.screens.ClayMyReviewsScreen
import com.example.bytefinder.ui.screens.ClayPratoDetailScreen
import com.example.bytefinder.ui.screens.HomeScreen
import com.example.bytefinder.ui.theme.BitefinderClayTheme
import com.example.bytefinder.ui.viewmodel.HomeViewModel
import com.example.bytefinder.ui.viewmodel.HomeViewModelFactory

private enum class AppScreen {
    HOME,
    BUSINESS,
    MY_REVIEWS,
    DETAIL
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = ApiClient.retrofit.create(ApiService::class.java)
        val repository = DataRepository(api)

        setContent {
            BitefinderClayTheme {
                AppRoot(repository = repository)
            }
        }
    }
}

@Composable
private fun AppRoot(repository: DataRepository) {
    var token by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }
    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var currentUserRole by remember { mutableStateOf<String?>(null) }
    var currentUserRestaurants by remember { mutableStateOf<List<Int>>(emptyList()) }

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var detailReturnScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedPratoId by remember { mutableStateOf<Int?>(null) }

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )

    fun goHome() {
        selectedPratoId = null
        currentScreen = AppScreen.HOME
    }

    fun signOut() {
        token = null
        userName = null
        currentUserId = null
        currentUserRole = null
        currentUserRestaurants = emptyList()
        selectedPratoId = null
        currentScreen = AppScreen.HOME
    }

    if (token == null) {
        ClayLoginScreen(
            repository = repository,
            onLoggedIn = { t, userId, nome, role, restaurantes ->
                token = t
                currentUserId = userId
                userName = nome
                currentUserRole = role
                currentUserRestaurants = restaurantes
                currentScreen = if (role == "restaurante") AppScreen.BUSINESS else AppScreen.HOME
            }
        )
    } else {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                val direction = if (targetState.ordinal >= initialState.ordinal) 1 else -1
                (slideInHorizontally(
                    animationSpec = tween(durationMillis = 330, easing = FastOutSlowInEasing),
                    initialOffsetX = { direction * (it / 6) }
                ) + fadeIn(animationSpec = tween(260))) togetherWith
                    (slideOutHorizontally(
                        animationSpec = tween(durationMillis = 230, easing = FastOutSlowInEasing),
                        targetOffsetX = { -direction * (it / 8) }
                    ) + fadeOut(animationSpec = tween(190)))
            },
            label = "screen-motion"
        ) { screen ->
            when (screen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        viewModel = homeViewModel,
                        repository = repository,
                        userName = userName ?: "Utilizador",
                        isRestaurantUser = (currentUserRole == "restaurante"),
                        onPratoClick = { pratoId ->
                            selectedPratoId = pratoId
                            detailReturnScreen = AppScreen.HOME
                            currentScreen = AppScreen.DETAIL
                        },
                        onOpenBusiness = { currentScreen = AppScreen.BUSINESS },
                        onOpenMyReviews = { currentScreen = AppScreen.MY_REVIEWS },
                        onSignOut = { signOut() },
                        onGoHome = { goHome() }
                    )
                }

                AppScreen.BUSINESS -> {
                    ClayBusinessScreen(
                        repository = repository,
                        currentUserId = currentUserId ?: 0,
                        restauranteIds = currentUserRestaurants,
                        onBack = { currentScreen = AppScreen.HOME },
                        onPratoClick = { pratoId ->
                            selectedPratoId = pratoId
                            detailReturnScreen = AppScreen.BUSINESS
                            currentScreen = AppScreen.DETAIL
                        },
                        onGoHome = { goHome() }
                    )
                }

                AppScreen.MY_REVIEWS -> {
                    ClayMyReviewsScreen(
                        repository = repository,
                        userId = currentUserId ?: 0,
                        onBack = { currentScreen = AppScreen.HOME },
                        onPratoClick = { pratoId ->
                            selectedPratoId = pratoId
                            detailReturnScreen = AppScreen.MY_REVIEWS
                            currentScreen = AppScreen.DETAIL
                        },
                        onGoHome = { goHome() }
                    )
                }

                AppScreen.DETAIL -> {
                    ClayPratoDetailScreen(
                        repository = repository,
                        pratoId = selectedPratoId ?: 0,
                        currentUserId = currentUserId ?: 0,
                        restaurantIds = currentUserRestaurants,
                        onBack = { currentScreen = detailReturnScreen },
                        onGoHome = { goHome() }
                    )
                }
            }
        }
    }
}


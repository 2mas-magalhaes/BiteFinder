package com.example.bytefinder.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val street: String
)

class LocationService(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun getCurrentLocation(): UserLocation? {
        if (!hasLocationPermission()) return null

        val location = getCurrentFusedLocation() ?: return null

        return location.toUserLocation()
    }

    @Suppress("MissingPermission")
    private suspend fun getCurrentFusedLocation(): Location? {
        return suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation { cts.cancel() }

            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    cont.resume(loc)
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }

    @Suppress("MissingPermission")
    fun locationUpdates(): Flow<UserLocation> {
        return callbackFlow {
            if (!hasLocationPermission()) {
                close()
                return@callbackFlow
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000L)
                .setMinUpdateIntervalMillis(500L)
                .setMaxUpdateDelayMillis(0L)
                .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                .setWaitForAccurateLocation(false)
                .build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { trySend(it) }
                }
            }

            fusedClient.requestLocationUpdates(request, callback, context.mainLooper)
                .addOnFailureListener { close(it) }

            awaitClose {
                fusedClient.removeLocationUpdates(callback)
            }
        }.map { location ->
            location.toUserLocation()
        }
    }

    private fun Location.toUserLocation(): UserLocation {
        val geocoded = reverseGeocode(latitude, longitude)
        return UserLocation(
            latitude = latitude,
            longitude = longitude,
            city = geocoded.first,
            street = geocoded.second
        )
    }

    private fun reverseGeocode(lat: Double, lng: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale("pt", "PT"))
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "Portugal"
                val street = addr.thoroughfare?.let { name ->
                    addr.subThoroughfare?.let { "$name $it" } ?: name
                } ?: addr.getAddressLine(0)?.split(",")?.firstOrNull() ?: ""
                Pair(city, street)
            } else {
                Pair("Portugal", "")
            }
        } catch (_: Exception) {
            Pair("Portugal", "")
        }
    }

    /**
     * Maps the detected city name to one of the supported cities in MockDataProvider.
     * Returns the matched city name or "Todas" if no match.
     */
    fun matchCity(detectedCity: String): String {
        val normalized = detectedCity.lowercase().trim()
        return when {
            normalized.contains("lisboa") || normalized.contains("lisbon") -> "Lisboa"
            normalized.contains("amadora") -> "Lisboa" // Greater Lisbon
            normalized.contains("oeiras") -> "Lisboa"
            normalized.contains("cascais") -> "Lisboa"
            normalized.contains("sintra") -> "Lisboa"
            normalized.contains("loures") -> "Lisboa"
            normalized.contains("almada") -> "Lisboa"
            normalized.contains("porto") || normalized.contains("oporto") -> "Porto"
            normalized.contains("gaia") || normalized.contains("vila nova de gaia") -> "Porto"
            normalized.contains("matosinhos") -> "Porto"
            normalized.contains("maia") -> "Porto"
            normalized.contains("coimbra") -> "Coimbra"
            else -> "Todas"
        }
    }
}

package com.example.bytefinder.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
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

        val location = getLastOrCurrentLocation() ?: return null
        val geocoded = reverseGeocode(location.first, location.second)

        return UserLocation(
            latitude = location.first,
            longitude = location.second,
            city = geocoded.first,
            street = geocoded.second
        )
    }

    @Suppress("MissingPermission")
    private suspend fun getLastOrCurrentLocation(): Pair<Double, Double>? {
        // Try last known location first
        val lastLocation = suspendCancellableCoroutine<Pair<Double, Double>?> { cont ->
            fusedClient.lastLocation
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        cont.resume(Pair(loc.latitude, loc.longitude))
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
        if (lastLocation != null) return lastLocation

        // Request fresh location
        return suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation { cts.cancel() }

            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        cont.resume(Pair(loc.latitude, loc.longitude))
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
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

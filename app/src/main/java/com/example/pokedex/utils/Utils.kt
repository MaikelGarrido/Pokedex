package com.example.pokedex.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

fun Context.openAppOptions() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:$packageName")
    }.let(::startActivity)
}

fun Context.message(message: String) {
    return Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.checkLocationPermission(): Boolean {
    val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    return permission == PackageManager.PERMISSION_GRANTED
}

fun getDistanceCovered(steps: Int): Double {
    val feet = (steps * 2.5).toInt()
    val distance = feet / 3.281
    return String.format("%.2f", distance).toDouble()
    /*return "$finalDistance meter"*/
}

fun getWeightString(weight: Int): String = String.format("%.1f KG", weight.toFloat() / 10)

fun getHeightString(height: Int): String = String.format("%.1f M", height.toFloat() / 10)

fun Fragment.vibratePhone() {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}

/**
 * Calculate the distance between two [Location] objects.
 */
fun Location.distanceBetween(location: Location): Double {
    return distance(
        lat1 = this.latitude,
        lat2 = location.latitude,

        lng1 = this.longitude,
        lng2 = location.longitude,

        el1 = this.altitude,
        el2 = location.altitude
    )
}

/**
 * Calculate distance between two points in latitude and longitude taking
 * into account height difference. If you are not interested in height
 * difference pass 0.0. Uses Haversine method as its base.
 *
 * lat1, lng1 Start point lat2, lng2 End point el1 Start altitude in meters
 * el2 End altitude in meters
 * @returns Distance in Meters
 */
private fun distance(lat1: Double, lat2: Double, lng1: Double,
                     lng2: Double, el1: Double, el2: Double): Double {
    val r = 6371 // Radius of the earth

    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lng2 - lng1)
    val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2))
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    var distance = r.toDouble() * c * 1000.0 // convert to meters

    val height = el1 - el2

    distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)

    return Math.sqrt(distance)
}

fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, ): Double {
    val latA = Math.toRadians(lat1)
    val lonA = Math.toRadians(lon1)
    val latB = Math.toRadians(lat2)
    val lonB = Math.toRadians(lon2)
    val cosAng = cos(latA) * cos(latB) * cos(lonB - lonA) + sin(latA) * sin(latB)
    val ang = acos(cosAng)
    return ang * 6371
}
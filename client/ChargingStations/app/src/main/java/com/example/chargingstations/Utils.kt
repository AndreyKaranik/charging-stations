package com.example.chargingstations

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.chargingstations.presentation.viewmodel.MainActivityViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.Map.CameraCallback
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider


class MyCameraListener(
    private val context: Context,
    private val placemarkMapObjects: List<PlacemarkMapObject>,
    zoomValue: Float
) : CameraListener {

    val ZOOM_BOUNDARY = 12f
    var changed = zoomValue >= ZOOM_BOUNDARY

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        when {
            cameraPosition.zoom >= ZOOM_BOUNDARY && changed -> {
                placemarkMapObjects.forEach {
                    it.setIcon(
                        ImageProvider.fromBitmap(
                            createBitmapFromVector(
                                context,
                                R.drawable.baseline_location_on_24
                            )
                        )
                    )
                }
                changed = false
            }

            cameraPosition.zoom < ZOOM_BOUNDARY && !changed -> {
                placemarkMapObjects.forEach {
                    it.setIcon(
                        ImageProvider.fromBitmap(
                            createBitmapFromVector(
                                context,
                                R.drawable.baseline_circle_24
                            )
                        )
                    )
                }
                changed = true
            }
        }
    }

}

fun createBitmapFromVector(context: Context, art: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, art) ?: return null
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            return true
        }
    }
    return false
}

fun moveTo(mainActivityViewModel: MainActivityViewModel, mapView: MapView, cameraCallback: CameraCallback, chargingStationId: Int, point: Point) {
    mainActivityViewModel.showChargingStationDetailsSheet(chargingStationId)
    mapView.map.move(
        CameraPosition(
            Point(point.latitude, point.longitude),
            20.0f,
            mapView.map.cameraPosition.azimuth,
            mapView.map.cameraPosition.tilt
        ), Animation(Animation.Type.SMOOTH, 1f), cameraCallback
    )
}
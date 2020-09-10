package com.elanciers.bringszo.Common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*

object SingleShotLocationProvider {
    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
// contents of the else and if. Also be sure to check gps permission/settings are allowed.
// call usually takes <10ms
    interface LocationCallback {
        fun onNewLocationAvailable(location: Location?)
    }
    fun requestSingleUpdate(
        context: Context,
        callback: LocationCallback
    ) {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isNetworkEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        println("isNetworkEnabled : $isNetworkEnabled")
        val isGPSEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        println("isGPSEnabled : $isGPSEnabled")
        if (!isGPSEnabled && !isNetworkEnabled) {
            val googleApiClient = GoogleApiClient.Builder(context).addApi(
                LocationServices.API).build()
            googleApiClient.connect()
            val locationRequest = LocationRequest.create()
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.setInterval(5 * 1000)
            locationRequest.setFastestInterval(5 * 1000)
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            // **************************
            builder.setAlwaysShow(true) // this is the key ingredient
            // **************************
            val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback(object: ResultCallback<LocationSettingsResult>
            {
                override fun onResult(result: LocationSettingsResult)
                {
                    val status = result.getStatus()
                    val state = result.getLocationSettingsStates()
                    when (status.getStatusCode()) {
                        LocationSettingsStatusCodes.SUCCESS -> {

                        }
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{}

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                    }
                }
            })
        } else {
            if (isNetworkEnabled) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_COARSE
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) { // TODO: Consider calling
//    Activity#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for Activity#requestPermissions for more details.
                    return
                }
                //if (locationManager != null) {
                val loc = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (loc != null) {
                    callback.onNewLocationAvailable(loc) //(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                }
                //}
                locationManager.requestSingleUpdate(
                    criteria,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            callback.onNewLocationAvailable(location) //(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                        }

                        override fun onStatusChanged(
                            provider: String,
                            status: Int,
                            extras: Bundle
                        ) {
                        }

                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    },
                    null
                )
            } else {
                if (isGPSEnabled) {
                    val criteria = Criteria()
                    criteria.accuracy = Criteria.ACCURACY_FINE
                    //if (locationManager != null) {
                    val loc = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (loc != null) {
                        callback.onNewLocationAvailable(loc) //(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                    }
                    //}
                    locationManager.requestSingleUpdate(
                        criteria,
                        object : LocationListener {
                            override fun onLocationChanged(location: Location) {
                                callback.onNewLocationAvailable(location) //GPSCoordinates(location.getLatitude(), location.getLongitude())
                            }

                            override fun onStatusChanged(
                                provider: String,
                                status: Int,
                                extras: Bundle
                            ) {
                            }

                            override fun onProviderEnabled(provider: String) {}
                            override fun onProviderDisabled(provider: String) {}
                        },
                        null
                    )
                }
            }
        }
    }

    // consider returning Location instead of this dummy wrapper class
    class GPSCoordinates {
        var longitude = -1f
        var latitude = -1f

        constructor(theLatitude: Float, theLongitude: Float) {
            longitude = theLongitude
            latitude = theLatitude
        }

        constructor(theLatitude: Double, theLongitude: Double) {
            longitude = theLongitude.toFloat()
            latitude = theLatitude.toFloat()
        }
    }
}
package com.elanciers.bringszo

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import org.jetbrains.annotations.Nullable


class RouteService : Service(), ConnectionCallbacks,
    OnConnectionFailedListener, LocationListener {
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null
    private var latitudeValue = 0.0
    private var longitudeValue = 0.0
    private var customSharedPreference: Utils? = null
    private var query: DBController? = null
    private var startTimeInMilliSeconds = 0L
    private var isServiceRunning = false
    override fun onCreate() {
        super.onCreate()
        customSharedPreference = Utils(applicationContext)
        if (isRouteTrackingOn) {
            startTimeInMilliSeconds = System.currentTimeMillis()
            Log.d(TAG, "Current time $startTimeInMilliSeconds")
            Log.d(TAG, "Service is running")
        }
        query = DBController(applicationContext)
        mLocationRequest = createLocationRequest()
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
            mGoogleApiClient!!.connect()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isServiceRunning = true
        return START_STICKY
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onConnected(@Nullable bundle: Bundle?) {
        Log.d(TAG, "Connection method has been called")
        val builder =
            LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback(object : ResultCallback<LocationSettingsResult?> {
            override fun onResult(result: LocationSettingsResult) {
                val status: Status = result.status
                when (status.getStatusCode()) {
                    LocationSettingsStatusCodes.SUCCESS -> if (ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) === PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) === PackageManager.PERMISSION_GRANTED
                    ) {
                        mLastLocation =
                            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                        if (mLastLocation != null) {
                            latitudeValue = mLastLocation!!.getLatitude()
                            longitudeValue = mLastLocation!!.getLongitude()
                            Log.d(
                                TAG,
                                "Latitude 1: $latitudeValue Longitude 1: $longitudeValue"
                            )
                            LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient,
                                mLocationRequest,
                                this@RouteService
                            )
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        })
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    protected fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 5000
        mLocationRequest.fastestInterval = 3000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return mLocationRequest
    }

    override fun onLocationChanged(location: Location) {
        Log.d(
            TAG,
            "Latitude " + location.getLatitude().toString() + " Longitude " + location.getLongitude()
        )
        Log.d(TAG, "SERVICE RUNNING $isServiceRunning")
        if (isRouteTrackingOn && startTimeInMilliSeconds == 0L) {
            startTimeInMilliSeconds = System.currentTimeMillis()
        }
        if (isRouteTrackingOn && startTimeInMilliSeconds > 0) {
            latitudeValue = location.getLatitude()
            longitudeValue = location.getLongitude()
            Log.d(
                TAG,
                "Latitude $latitudeValue Longitude $longitudeValue"
            )
            // insert values to local sqlite database
            query!!.addNewLocationObject(
                System.currentTimeMillis(),
                latitudeValue,
                longitudeValue
            )
            // send local broadcast receiver to application components
            val localBroadcastIntent = Intent(ACTION)
            localBroadcastIntent.putExtra("RESULT_CODE", "LOCAL")
            LocalBroadcastManager.getInstance(applicationContext)
                .sendBroadcast(localBroadcastIntent)
            val timeoutTracking = 2 * 60 * 60 * 1000.toLong()
            if (System.currentTimeMillis() >= startTimeInMilliSeconds + timeoutTracking) { //turn of the tracking
                customSharedPreference!!.setServiceState(false)
                Log.d(TAG, "SERVICE HAS BEEN STOPPED")
                this.stopSelf()
            }
        }
        if (!isRouteTrackingOn) {
            Log.d(TAG, "SERVICE HAS BEEN STOPPED 1")
            isServiceRunning = false
            Log.d(TAG, "SERVICE STOPPED $isServiceRunning")
            /*val dialogIntent = Intent(this, RecordResultActivity::class.java)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(dialogIntent)*/
            this.stopSelf()
        }
    }

    private val isRouteTrackingOn: Boolean
        private get() {
            Log.d(
                TAG,
                "SERVICE STATE " + customSharedPreference!!.getServiceState()
            )
            return customSharedPreference!!.getServiceState()
        }

    override fun onDestroy() {
        mGoogleApiClient!!.disconnect()
        super.onDestroy()
    }

    companion object {
        private val TAG = RouteService::class.java.simpleName
        const val ACTION = "com.elanciers.pova.RouteService"
    }
}
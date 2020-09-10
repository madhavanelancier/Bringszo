package com.elanciers.bringszo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Connection
import com.elanciers.bringszo.Common.GpsUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_option_select_.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OptionSelect_Activity : AppCompatActivity() {
    var locationmanager: LocationManager? = null
    val activity = this
    var isGPS = false;
    var mContext: Context? = null;

    val pinarr=ArrayList<String>()
    val cityarr=ArrayList<String>()
    var latistr=""
    var longstr=""

    fun getLocation() {
        try {
            println("locat")
            locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

                return
            }
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            //locationmanager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, activity)
            locationmanager!!.requestSingleUpdate(
                criteria,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        println("singlelocation : " + location.latitude + " , " + location.longitude)
                        latistr = location.latitude.toString()
                        longstr = location.longitude.toString()

                        if (latistr.isNotEmpty() && longstr.isNotEmpty()) {
                            RewardpointsAsync_search().execute()
                        } else {
                           // val toast=Toast(applicationContext,"Unable to get location")
                        }
                        //location_shimmer.stopShimmerAnimation()
                        // location_shimmer.visibility = View.GONE
                        // location_layout.visibility = View.VISIBLE
                        //getCompleteAddressString(location)
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
            // Log.e(“Network”, “Network”);
            if (locationmanager != null) {
                val location = locationmanager!!
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    println("lastknown : " + location.latitude + " , " + location.longitude)

                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            println("error : " + e.printStackTrace())
        }


    }
    var locationManager: LocationManager? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_select_)
        mContext=this

       /* pinarr.add("Select Your Pincode")
        pinarr.add("625010")
        pinarr.add("625020")

        cityarr.add("Select Your City")
        cityarr.add("Chennai")
        cityarr.add("Madurai")

        val adap=ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,cityarr)
        cityspin.adapter=adap

        val adap1=ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,pinarr)
        pinspin.adapter=adap1*/



        pinspin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                if (pinspin.selectedItemPosition != 0&&cityspin.selectedItemPosition!=0) {

                    shop.visibility=View.VISIBLE
                }
                else {
                    shop.visibility=View.INVISIBLE

                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

        cityspin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                if (cityspin.selectedItemPosition != 0&&pinspin.selectedItemPosition!=0) {

                    shop.visibility=View.VISIBLE
                }
                else {
                    shop.visibility=View.INVISIBLE

                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }


        shop.setOnClickListener {
            enable_shop.visibility=View.VISIBLE
            shoperr.visibility=View.VISIBLE
            shop.visibility=View.INVISIBLE

            Handler().postDelayed(Runnable {
                startActivity(Intent(this@OptionSelect_Activity,HomeActivity::class.java))
            },2000)
        }




        if (CheckingPermissionIsEnabledOrNot()){
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )== PackageManager.PERMISSION_GRANTED) {
                // val mLocationManager =  getSystemService(LOCATION_SERVICE) as (LocationManager);
                // mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, this);
                locationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000,
                    10F,
                    locationListenerGPS
                )

            }


            statusCheck()
            //getLocation()
        }else{
            RequestMultiplePermission()
        }

        cardView.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))

        }
        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))

        }

        floatingActionButtons.setOnClickListener {
            val toast= Toast.makeText(applicationContext,"Coming Soon...",Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER,0,0)
            toast.show()

        }
    }
    fun statusCheck() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        println("net : " + manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        println("gps : " + manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)&&!manager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            ))
        {
            println("perform")
            GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
                override fun gpsStatus(isGPSEnable: Boolean) {
                    // turn on GPS
                    isGPS = isGPSEnable
                    println("network & gps else " + isGPSEnable)
                    getLocation()
                }
            })
            /*val request = LocationRequest()
                .setFastestInterval(1500)
                .setInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            val settingsRequest = LocationSettingsRequest.Builder()
            settingsRequest.addLocationRequest(request)
            val client = LocationServices.getSettingsClient(activity)
            val responseTask = client.checkLocationSettings(settingsRequest.build())
            responseTask.addOnSuccessListener(activity, { locationSettingsResponse-> locationScheduler() })
            responseTask.addOnFailureListener(activity, { e->
                val statusCode = (e as ApiException).getStatusCode()
                when (statusCode) {
                    CommonStatusCodes.RESOLUTION_REQUIRED -> try
                    {
                        val apiException = (e as ResolvableApiException)
                        apiException.startResolutionForResult(LocationSettingsStatusCodes, permissionCode)
                        Log.d(TAG, "Dialog displayed")
                    }
                    catch (sendIntentException:IntentSender.SendIntentException) {
                        Log.d(TAG, "Error displaying dialogBox", sendIntentException)
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.d(TAG, "Unable to turn on location service", e)
                } })*/
            //buildAlertMessageNoGps()
            //if (googleApiClient == null)
            //{
            /*val googleApiClient = GoogleApiClient.Builder(getApplicationContext()).addApi(
                LocationServices.API).build()
            googleApiClient.connect()
            val locationRequest = LocationRequest.create()
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.setInterval(5 * 1000)
            //locationRequest.setFastestInterval(5 * 1000)
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
                            getLocation()
                        }
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{}
                            *//*try
                                {
                                    status.startResolutionForResult(activity, 1000)
                                }
                                catch (e: IntentSender.SendIntentException)
                                {
                                }*//*
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                        }
                    }
                })*/
            //}
        }else{
            println("else : ")

            if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                getLocation()
                //buildAlertMessageNoGps()
                //if (googleApiClient == null)
                //{
                /*val googleApiClient = GoogleApiClient.Builder(getApplicationContext()).addApi(
                    LocationServices.API).build()
                googleApiClient.connect()
                val locationRequest = LocationRequest.create()
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                locationRequest.setInterval(5 * 1000)
                //locationRequest.setFastestInterval(5 * 1000)
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
                                getLocation()
                            }
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{}
                            *//*try
                            {
                                status.startResolutionForResult(activity, 1000)
                            }
                            catch (e: IntentSender.SendIntentException)
                            {
                            }*//*
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                        }
                    }
                })*/
                //}
            }else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                getLocation()
                //buildAlertMessageNoGps()
                //if (googleApiClient == null)
                //{
                /*val googleApiClient = GoogleApiClient.Builder(getApplicationContext()).addApi(
                    LocationServices.API).build()
                googleApiClient.connect()
                val locationRequest = LocationRequest.create()
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                locationRequest.setInterval(5 * 1000)
                //locationRequest.setFastestInterval(5 * 1000)
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
                                getLocation()
                            }
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{}
                            *//*try
                            {
                                status.startResolutionForResult(activity, 1000)
                            }
                            catch (e: IntentSender.SendIntentException)
                            {
                            }*//*
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                        }
                    }
                })*/
                //}
            }else {
                GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
                    override fun gpsStatus(isGPSEnable: Boolean) {
                        // turn on GPS
                        isGPS = isGPSEnable
                        println("network else " + isGPSEnable)
                        getLocation()
                    }
                })
            }
        }
    }
    var locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: android.location.Location) {
            val latitude = location.latitude
            val longitude = location.longitude
            val msg = "New Latitude: " + latitude + "New Longitude: " + longitude
            println("msglat_lang"+msg)
            latistr=location.latitude.toString()
            longstr=location.longitude.toString()
            //Toast.makeText(this@HomeActivity, msg, Toast.LENGTH_LONG).show()
            //location_shimmer.stopShimmerAnimation()
            //location_shimmer.visibility=View.GONE
           // location_layout.visibility=View.VISIBLE
           // getCompleteAddressString(location)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

            //getLocation()
        }

        override fun onProviderEnabled(provider: String) {
            getLocation()

        }

        override fun onProviderDisabled(provider: String) {
                statusCheck()
        }
    }
    fun CheckingPermissionIsEnabledOrNot(): Boolean {

        val INTERNET = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
        val ACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
        //val ACCESS_NOTIFICATION_POLICY = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        val ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        val ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val CALL_PHONE = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CALL_PHONE
        )

        return INTERNET == PackageManager.PERMISSION_GRANTED &&
                ACCESS_NETWORK_STATE == PackageManager.PERMISSION_GRANTED &&
                //ACCESS_NOTIFICATION_POLICY == PackageManager.PERMISSION_GRANTED &&
                ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED &&
                ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED //&&
        //CALL_PHONE == PackageManager.PERMISSION_GRANTED
    }
    fun RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(
            this, arrayOf<String>
                (
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION//,
                //android.Manifest.permission.CALL_PHONE
            ), Appconstands.RequestPermissionCode
        )


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {

            Appconstands.RequestPermissionCode ->

                if (grantResults.size > 0) {

                    val INTERNET = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val ACCESS_NETWORK_STATE = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    //val ACCESS_NOTIFICATION_POLICY = grantResults[2] == PackageManager.PERMISSION_GRANTED
                    val ACCESS_FINE_LOCATION = grantResults[2] == PackageManager.PERMISSION_GRANTED
                    val ACCESS_COARSE_LOCATION =
                        grantResults[3] == PackageManager.PERMISSION_GRANTED
                    //val CALL_PHONE = grantResults[4] == PackageManager.PERMISSION_GRANTED

                    if (INTERNET && ACCESS_NETWORK_STATE /*&& ACCESS_NOTIFICATION_POLICY*/ && ACCESS_FINE_LOCATION && ACCESS_COARSE_LOCATION /*&&CALL_PHONE*/) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            /*val mLocationManager =  getSystemService(LOCATION_SERVICE) as (LocationManager);
                            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this);*/
                            getLocation()
                        }
                        //Toast.makeText(this@MainFirstActivity,"Permission Granted", Toast.LENGTH_LONG).show()
                    } else {
                        //Toast.makeText(this@MainFirstActivity,"Permission Denied", Toast.LENGTH_LONG).show()

                    }
                }

        }
    }
    inner class RewardpointsAsync_search : AsyncTask<String?, String?, String?>() {
        override fun onPreExecute() {

        }

        protected override fun doInBackground(vararg strings: String?): String? {
            var result: String? = null
            val con = Connection()
            try {
                val jobj = JSONObject()
                jobj.put("s_latitude", latistr)
                jobj.put("s_longitude", longstr)
                Log.i("rewardinput", Appconstands.loc + "    " + jobj.toString())
                result = con.sendHttpPostjson2(Appconstands.loc, jobj, "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(resp: String?) {
//            swipeToRefresh!!.visibility = View.VISIBLE
          //  progress_lay!!.visibility = View.GONE
           // productItems1 = java.util.ArrayList()
            try {
                if (resp != null) {

                    val jobj = JSONObject(resp)
                    if (jobj.getString("Status") == "success") {

                        runOnUiThread {
                            enable_shop.visibility=View.INVISIBLE
                            enable_done.visibility=View.VISIBLE
                            shoperr.setText("Yay! Bringing service in your zone!!!")
                        }


                        Handler().postDelayed(Runnable {
                            startActivity(Intent(applicationContext,HomeActivity::class.java))
                        },2000)

                    } else {

                        enable_shop.visibility=View.GONE
                        shoperr.visibility=View.GONE
                        error_shop.visibility=View.VISIBLE
                        shopena.visibility=View.VISIBLE

                        Handler().postDelayed(Runnable {
                            finish()
                        },2000)

                    }
                }
            }catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}
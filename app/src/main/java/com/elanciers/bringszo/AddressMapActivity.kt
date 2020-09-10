package com.elanciers.bringszo

import android.Manifest
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.coordinate_lay_map.*
import kotlinx.android.synthetic.main.address_lay.*
import android.content.pm.PackageManager
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.Dialog
import androidx.core.content.ContextCompat
import android.content.Intent
import android.content.IntentSender
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.location.Address
import android.location.Geocoder
import android.os.*
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.elanciers.bringszo.Common.*
import com.elanciers.bringszo.DataClass.AddressData
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import com.google.android.gms.common.*
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.tabs.TabLayout
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class AddressMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener , ResultCallback<LocationSettingsResult> {
    override fun onResult(locationSettingsResult: LocationSettingsResult) {
        val status = locationSettingsResult.getStatus()
        when (status.getStatusCode()) {
            LocationSettingsStatusCodes.SUCCESS -> {
                Log.i(TAG, "All location settings are satisfied.")
                startLocationUpdates()
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                Log.i(TAG, ("Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings "))
                try
                {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this@AddressMapActivity, REQUEST_CHECK_SETTINGS)
                }
                catch (e: IntentSender.SendIntentException) {
                    Log.i(TAG, "PendingIntent unable to execute request.")
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(TAG, ("Location settings are inadequate, and cannot be fixed here. Dialog " + "not created."))
        }
    }
    val tag = "AdrsMap"
    val activity = this
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog
    var success=""

    /*val mFragmentTitleList1 = ArrayList<String>()
    val mFragmentIconList1 = ArrayList<Int>()*/
    val REQUEST_CHECK_SETTINGS = 0x1;
    private var mMap: GoogleMap? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    //internal lateinit var mLocationMarkerText: TextView
    private var mCenterLatLong: LatLng? = null


    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private var mResultReceiver: AddressResultReceiver? = null
    /**
     * The formatted location address.
     */
    protected var mAddressOutput: String? = null
    protected var mAreaOutput: String? = null
    protected var mCityOutput: String? = null
    protected var mStateOutput: String? = null
    /*internal lateinit var mLocationAddress: EditText
    internal lateinit var mLocationText: TextView
    internal lateinit var mToolbar: Toolbar*/

    var id = ""
    var v = ""
    var type = "Home"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appconstands.changeStatusBarColor(this, R.color.statusbar)
        setContentView(R.layout.activity_address_map)
        utils = Utils(this)
        db = DBController(this)
        pDialog = Dialog(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        selectType()

        work.setOnClickListener {
            if (intent.extras.getString("id")=="") {
                if (db.AddressType("Work")) {
                    type = "Work"
                    selectType()
                } else {
                    Toast.makeText(activity, "You Already have a Work Address", Toast.LENGTH_LONG).show()
                    if (db.AddressType("Home")) {
                        type = "Home"
                        selectType()
                    } else {
                        type = "Other"
                        selectType()
                    }

                }
            }else {
                type = "Work"
                selectType()
            }
        }
        home.setOnClickListener {
            if (intent.extras.getString("id")=="") {
                if (db.AddressType("Home")) {
                    type = "Home"
                    selectType()
                } else {
                    Toast.makeText(activity, "You Already have a Home Address", Toast.LENGTH_LONG).show()
                    if (db.AddressType("Work")) {
                        type = "Work"
                        selectType()
                    } else {
                        type = "Other"
                        selectType()
                    }
                }
            }else {
                type = "Home"
                selectType()
            }
        }
        other.setOnClickListener{
            type = "Other"
            selectType()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleReveal(R.id.other_layout, 1, true, true);
                cust_tab.setVisibility(View.INVISIBLE);
            }
            else {
                other_layout.setVisibility(View.VISIBLE);
                cust_tab.setVisibility(View.INVISIBLE);
            }
        }
       /* val llBottomSheet = findViewById(R.id.address_layout) as LinearLayout
        val bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet)
       *//* bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)*//*
        bottomSheetBehavior.setPeekHeight(340)
        bottomSheetBehavior.setHideable(false)
        bottomSheetBehavior.setBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet:View, newState:Int) {
            }
            override fun onSlide(@NonNull bottomSheet:View, slideOffset:Float) {
            }
        })*/

        back.setOnClickListener {
            finish()
        }
        /*mFragmentTitleList1.add("Work")
        mFragmentTitleList1.add("Home")
        mFragmentTitleList1.add("Other")
        mFragmentIconList1.add(R.drawable.ic_suitcase)
        mFragmentIconList1.add(R.drawable.ic_home)
        mFragmentIconList1.add(R.drawable.ic_location)*/


        enter_address.setText("enter HOUSE / FLAT / BLOCK NO")
        if (intent.extras.getString("id")!=""){
            id = intent.extras.get("id").toString()
            val data = db.Addressget(intent.extras.get("id").toString())
            /*val set = LatLng(data.adrs_latitude.toString().toDouble(), data.adrs_longtitude.toString().toDouble());
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(set, 20f));*/
            val adrs_id = data.adrs_id!!.toString()
            val adrs_type = data.adrs_type!!.toString()
            val adrs_title = data.adrs_title!!.toString()
            val address = data.address!!.toString()
            val adrs_house = data.adrs_house!!.toString()
            val adrs_landmark = data.adrs_landmark!!.toString()
            val adrs_latitude = data.adrs_latitude!!.toString()
            val adrs_longtitude = data.adrs_longtitude!!.toString()
            if (adrs_type == "Work"){
                type = "Work"
                selectType()
            }
            else if (adrs_type == "Home"){
                type = "Home"
                selectType()
            }
            else {
                type = "Other"
                selectType()
                other_title.setText(adrs_title)
            }

            house.setText(adrs_house)
            land.setText(adrs_landmark)
            chech()
        }else{
            if (db.AddressType("Work")) {
                type = "Work"
                selectType()
            }else{
                if (db.AddressType("Home")) {
                    type = "Home"
                    selectType()
                }else{
                    type = "Other"
                    selectType()
                }
            }
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                println("onTabSelected tab1 : "+tab.position)
                //tabselection(tab.position)
                /*if (tab.position==0) {
                    if (db.AddressType("Work")) {
                        selecttab1(tab.position)
                    } else {
                        Toast.makeText(activity, "You Already have a Work Address", Toast.LENGTH_LONG).show()
                        if (db.AddressType("Home")) {
                            tabs.getTabAt(1)!!.select()
                        } else {
                            tabs.getTabAt(2)!!.select()
                        }

                    }
                }
                if (tab.position==1) {
                    if (db.AddressType("Home")) {
                        selecttab1(tab.position)
                    } else {
                        Toast.makeText(activity, "You Already have a Home Address", Toast.LENGTH_LONG).show()
                        if (db.AddressType("Work")) {
                            tabs.getTabAt(0)!!.select()
                        } else {
                            tabs.getTabAt(2)!!.select()
                        }
                    }
                }

                if (tab.position==2){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(R.id.other_layout, 1, true, true);
                        tabs.setVisibility(View.INVISIBLE);
                    }
                    else {
                        other_layout.setVisibility(View.VISIBLE);
                        tabs.setVisibility(View.INVISIBLE);
                    }
                }*/
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                println("onTabUnselected tab1")

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                println("onTabReselected tab1")
            }
        })
        house.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                chech()
            }

        })
        land.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                chech()
            }
        })

        //enter_address.setBackgroundResource(R.drawable.add_address)
        //enter_address.setTextColor(resources.getColor(R.color.colorAccent))

       /* enter_address.setBackgroundResource(R.drawable.add_address)
        enter_address.setTextColor(resources.getColor(R.color.colorAccent))
        enter_address.setText("next")*/
        lay.setOnClickListener {
            enter_layout.visibility = View.VISIBLE
        }
        enter_address.setOnClickListener {
            enter_layout.visibility = View.VISIBLE
            if (enter_address.text.toString().toLowerCase()=="save") {
                if (chech()) {
                    val data = AddressData()
                    data.adrs_type = type//mFragmentTitleList1[tabs.selectedTabPosition]
                    if (tabs.selectedTabPosition==2) {
                        data.adrs_title = if (other_title.text.toString().trim().equals("",true)) "" else other_title.text.toString().trim()
                    }else{
                        data.adrs_title = type//mFragmentTitleList1[tabs.selectedTabPosition]
                    }
                    data.address = house.text.toString().trim()+", "+land.text.toString().trim()+", "+address.text.toString().trim()
                    data.adrs_house = house.text.toString().trim()
                    data.adrs_landmark = land.text.toString().trim()
                    data.adrs_latitude = mCenterLatLong!!.latitude.toString()
                    data.adrs_longtitude = mCenterLatLong!!.longitude.toString()
                    data.city = mCityOutput.toString()
                    if (intent.extras.get("id")!=""){
                        data.adrs_id = intent.extras.get("id").toString()
                        EditAddress(data)
                    }else {
                        SaveAddress(data)
                    }
                }
            }
        }
        cancel.setOnClickListener {
            //selecttab1(0)
            //tabs.getTabAt(0)!!.select()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleReveal(R.id.other_layout, 1, true, false);
                cust_tab.setVisibility(View.VISIBLE);
            }
            else {
                other_layout.setVisibility(View.GONE);
                cust_tab.setVisibility(View.VISIBLE);
            }
        }


        enter_layout.visibility = View.GONE
        address_layout.visibility = View.GONE

        //mLocationText.setOnClickListener { openAutocompleteActivity() }
        mapFragment!!.getMapAsync(this)
        mResultReceiver = AddressResultReceiver(Handler())

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            println("AppUtils.isLocationEnabled(activity) : "+AppUtils.isLocationEnabled(activity))
            if (!AppUtils.isLocationEnabled(activity)) {
                // notify user
                val dialog = AlertDialog.Builder(activity)
                dialog.setMessage("Location not enabled!")
                dialog.setPositiveButton("Open location settings") { paramDialogInterface, paramInt ->
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                }
                dialog.setNegativeButton("Cancel") { paramDialogInterface, paramInt ->

                }
                dialog.show()
            }
            if (intent.extras.get("id")!="") {
            }else {
                buildGoogleApiClient()
            }
        } else {
            Toast.makeText(activity, "Location not supported in this device", Toast.LENGTH_SHORT).show()
        }

        searchLocation("Kala")
    }
    fun selectType(){
        if (type=="Home"){
            //home
            home_txt.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            home_img.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
            home_vw.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))

            //work
            work_txt.setTextColor(ContextCompat.getColor(this, R.color.textcolor))
            work_vw.setBackgroundColor(ContextCompat.getColor(this, R.color.textcolor))
            work_img.setColorFilter(ContextCompat.getColor(this, R.color.textcolor), PorterDuff.Mode.SRC_ATOP)

            //other
            other_txt.setTextColor(ContextCompat.getColor(this, R.color.textcolor))
            other_vw.setBackgroundColor(ContextCompat.getColor(this, R.color.textcolor))
            other_img.setColorFilter(ContextCompat.getColor(this, R.color.textcolor), PorterDuff.Mode.SRC_ATOP)
        }else if (type=="Work"){
            //home
            home_txt.setTextColor(ContextCompat.getColor(this, R.color.textcolor))
            home_img.setColorFilter(ContextCompat.getColor(this, R.color.textcolor), PorterDuff.Mode.SRC_ATOP)
            home_vw.setBackgroundColor(ContextCompat.getColor(this, R.color.textcolor))

            //work
            work_txt.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            work_vw.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            work_img.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)

            //other
            other_txt.setTextColor(ContextCompat.getColor(this, R.color.textcolor))
            other_vw.setBackgroundColor(ContextCompat.getColor(this, R.color.textcolor))
            other_img.setColorFilter(ContextCompat.getColor(this, R.color.textcolor), PorterDuff.Mode.SRC_ATOP)

        }else if (type=="Other"){
            //home
            home_txt.setTextColor(ContextCompat.getColor(this, R.color.textcolor))
            home_img.setColorFilter(ContextCompat.getColor(this, R.color.textcolor), PorterDuff.Mode.SRC_ATOP)
            home_vw.setBackgroundColor(ContextCompat.getColor(this, R.color.textcolor))

            //work
            work_txt.setTextColor(ContextCompat.getColor(this, R.color.textcolor))
            work_vw.setBackgroundColor(ContextCompat.getColor(this, R.color.textcolor))
            work_img.setColorFilter(ContextCompat.getColor(this, R.color.textcolor), PorterDuff.Mode.SRC_ATOP)

            //other
            other_txt.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            other_vw.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            other_img.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
        }
    }
    private fun setTextViewDrawableColor(textView:TextView, color:Int) {
        for (drawable in textView.compoundDrawables)
        {
            if (drawable != null)
            {
                drawable.setColorFilter(PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP))
            }
        }
    }
    fun tabselection(position: Int){
        if (intent.extras.getString("id")=="") {
            if (position == 0) {
                if (db.AddressType("Work")) {
                    selecttab1(position)
                } else {
                    Toast.makeText(activity, "You Already have a Work Address", Toast.LENGTH_LONG).show()
                    if (db.AddressType("Home")) {
                        tabs.getTabAt(1)!!.select()
                        selecttab1(1)
                    } else {
                        tabs.getTabAt(2)!!.select()
                        selecttab1(2)
                    }

                }
            }
            if (position == 1) {
                if (db.AddressType("Home")) {
                    selecttab1(position)
                } else {
                    Toast.makeText(activity, "You Already have a Home Address", Toast.LENGTH_LONG).show()
                    if (db.AddressType("Work")) {
                        tabs.getTabAt(0)!!.select()
                        selecttab1(0)
                    } else {
                        tabs.getTabAt(2)!!.select()
                        selecttab1(2)
                    }
                }
            }
        }else{
            selecttab1(position)
        }

        if (position==2){
            selecttab1(position)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleReveal(R.id.other_layout, 1, true, true);
                tabs.setVisibility(View.INVISIBLE);
            }
            else {
                other_layout.setVisibility(View.VISIBLE);
                tabs.setVisibility(View.INVISIBLE);
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "OnMapReady")
        mMap = googleMap

        mMap!!.setOnCameraChangeListener { cameraPosition ->
            var cameraPos : CameraPosition? = null

            if (id!=""){
                val data = db.Addressget(intent.extras.get("id").toString())
                val set = LatLng(data.adrs_latitude.toString().toDouble(), data.adrs_longtitude.toString().toDouble());
                //val set = LatLng(-33.867, 151.206);
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(set, 20f));
                cameraPos =CameraPosition.Builder()
                        .target(set).zoom(20f)/*.tilt(70f)*/.build()
                id=""
            }else {
                cameraPos = cameraPosition
            }
            Log.d("Camera postion change" + "", cameraPosition.toString() + "")
            mCenterLatLong = cameraPos!!.target


            mMap!!.clear()

            try {

                val mLocation = Location("")
                mLocation.latitude = mCenterLatLong!!.latitude
                mLocation.longitude = mCenterLatLong!!.longitude


                RewardpointsAsync_search().execute()

                if(success=="success") {
                    startIntentService(mLocation)
                }


                ///mLocationMarkerText.text = "Lat : " + mCenterLatLong!!.latitude + "," + "Long : " + mCenterLatLong!!.longitude
                println("Lat : " + mCenterLatLong!!.latitude + "," + "Long : " + mCenterLatLong!!.longitude)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //        mMap.setMyLocationEnabled(true);
        //        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //
        //        // Add a marker in Sydney and move the camera
        //        LatLng sydney = new LatLng(-34, 151);
        //        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    inner class RewardpointsAsync_search : AsyncTask<String?, String?, String?>() {
        override fun onPreExecute() {
            success=""
        }

        protected override fun doInBackground(vararg strings: String?): String? {
            var result: String? = null
            val con = Connection()
            try {
                val jobj = JSONObject()
                jobj.put("s_latitude", mCenterLatLong!!.latitude)
                jobj.put("s_longitude",  mCenterLatLong!!.longitude)
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

            Log.e("Err",resp.toString())
            try {
                if (resp != null) {

                    val jobj = JSONObject(resp)
                    if (jobj.getString("Status") == "success") {
                        success="success"



                        runOnUiThread {
                            //enable_shop.visibility=View.INVISIBLE
                            //enable_done.visibility=View.VISIBLE
                            //shoperr.setText("Yay! Bringing service in your zone!!!")
                        }


                        /* Handler().postDelayed(Runnable {
                             startActivity(Intent(applicationContext,HomeActivity::class.java))
                         },2000)*/

                    } else {
                        success="failure"
                        val toast=Toast.makeText(applicationContext,"We are not bring service in this location",Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER,0,0)
                        toast.show()

                        //enable_shop.visibility=View.GONE
                        //shoperr.visibility=View.GONE
                        //error_shop.visibility=View.VISIBLE
                        //shopena.visibility=View.VISIBLE

                        /*Handler().postDelayed(Runnable {
                            finish()
                        },2000)*/

                    }
                }
            }catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient)
        if (mLastLocation != null) {
            changeMap(mLastLocation)
            Log.d(TAG, "ON connected")

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        try {
            val mLocationRequest = LocationRequest()
            mLocationRequest.interval = 10000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConnectionSuspended(i: Int) {
        Log.i(TAG, "Connection suspended")
        mGoogleApiClient!!.connect()
    }

    override fun onLocationChanged(location: Location?) {
        try {
            if (location != null)
                changeMap(location)
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    protected fun startLocationUpdates() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }


    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    override fun onStart() {
        super.onStart()
        try {
            mGoogleApiClient!!.connect()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onStop() {
        super.onStop()
        try {

        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }

    private fun checkPlayServices(): Boolean {
        val resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show()
            } else {
                //finish();
            }
            return false
        }
        return true
    }

    private fun changeMap(location: Location) {
        Log.d(TAG, "Reaching map" + mMap!!)


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap!!.uiSettings.isZoomControlsEnabled = false
            mMap!!.uiSettings.isCompassEnabled=false
            val latLong: LatLng

            var cameraPosition : CameraPosition? = null
            latLong = LatLng(location.latitude, location.longitude)

            if (id!=""){
                val data = db.Addressget(intent.extras.get("id").toString())
                val set = LatLng(data.adrs_latitude.toString().toDouble(), data.adrs_longtitude.toString().toDouble());
                //val set = LatLng(-33.867, 151.206);
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(set, 20f));
                cameraPosition =CameraPosition.Builder()
                        .target(set).zoom(20f)/*.tilt(70f)*/.build()
                id=""
            }else {
                cameraPosition = CameraPosition.Builder()
                        .target(latLong).zoom(20f)/*.tilt(70f)*/.build()
            }

            mMap!!.isMyLocationEnabled = true
            mMap!!.uiSettings.isMyLocationButtonEnabled = true
            mMap!!.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition))

            mCenterLatLong = cameraPosition.target
            //mLocationMarkerText.text = "Lat : " + location.latitude + "," + "Long : " + location.longitude
            println("Lat : " + location.latitude + "," + "Long : " + location.longitude)
            startIntentService(location)


        } else {
            Toast.makeText(applicationContext,
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show()
        }

    }

    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY)

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA)

            val state = resultData.getString("state")
            val countryName = resultData.getString("countryName")
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY)
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET)
            val full = mStateOutput.toString().split(",")

            println("FullAdrs : "+resultData.getString(AppUtils.LocationConstants.FullAdrs))
            println("mAddressOutput : "+mAddressOutput)
            println("mAreaOutput : "+mAreaOutput)
            println("state : "+state)
            println("countryName : "+countryName)
            println("mCityOutput : "+mCityOutput)
            println("mStateOutput : "+mStateOutput)

            displayAddressOutput()

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));
                /*try {
                    val ar = full.get(full.size - 4)
                    val last = full.get(full.size - 3) + full.get(full.size - 2) + full.get(full.size - 1)
                    println("ar : " + ar)
                    println("last : " + last)
                    val are = resultData.getString("area")
                    area.setText(ar)
                    address.setText(last)
                }catch (e : java.lang.Exception){*/
                    val are = resultData.getString("area")
                    area.setText(are)
                    address.setText(mCityOutput+","+state+", "+mAreaOutput+", "+countryName)
               /* }*/
                Handler().postDelayed({
                    address_layout.visibility = View.VISIBLE
                }, 1000)
            }
        }

    }

    /**
     * Updates the address in the UI.
     */
    protected fun displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null){}
            // mLocationText.setText(mAreaOutput+ "");

                //mLocationAddress.setText(mAddressOutput)
            //mLocationText.setText(mAreaOutput);
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected fun startIntentService(mLocation: Location) {




        enter_layout.visibility = View.GONE
        address_layout.visibility = View.GONE
        // Create an intent for passing to the intent service responsible for fetching the address.
        val intent = Intent(this, FetchAddressIntentService::class.java)

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver)

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation)

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent)

        println("Latitude:" + mLocation.getLatitude() + ", Longitude:" + mLocation.getLongitude())
        /*val geocoder = Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            val listAddresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            println("listAddresses : "+listAddresses)
            if(null!=listAddresses&&listAddresses.size>0){
                val area = listAddresses.get(0).getAddressLine(0).toString().split(",")[1]
                val city = listAddresses.get(0).locality
                println("adminArea : "+listAddresses.get(0).adminArea)
                println("subAdminArea : "+listAddresses.get(0).subAdminArea)
                println("subLocality : "+listAddresses.get(0).subLocality)
                println("area : "+area)
                println("city : "+city)
                *//*ab!!.title=city.trimStart()
                ab!!.subtitle =area.trimStart()*//*
            }
        } catch ( e: IOException) {
            e.printStackTrace();
        }*/
    }


    private fun openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        } catch (e: GooglePlayServicesRepairableException) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.connectionStatusCode,
                    0 /* requestCode */).show()
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            val message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode)

            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                val place = PlaceAutocomplete.getPlace(activity, data!!)

                // TODO call location based filter


                val latLong: LatLng


                latLong = place.latLng

                //mLocationText.setText(place.getName() + "");

                val cameraPosition = CameraPosition.Builder()
                        .target(latLong).zoom(19f).tilt(70f).build()

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mMap!!.isMyLocationEnabled = true
                mMap!!.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition))


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            val status = PlaceAutocomplete.getStatus(activity, data!!)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
        when (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.i(TAG, "User agreed to make required location settings changes.")
                    startLocationUpdates()
                }
                Activity.RESULT_CANCELED -> Log.i(TAG, "User chose not to make required location settings changes.")
            }
        }
    }

    companion object {
        private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        private val TAG = "MAP LOCATION"
        private val REQUEST_CODE_AUTOCOMPLETE = 1
    }

    fun selecttab1(position: Int){
        /*val tab3 = tabs.getTabAt(0)
        assert(tab3 != null)
        tab3!!.setCustomView(null)
        val view3 = LayoutInflater.from(this).inflate(R.layout.custom_tab_address, null)
        val tabTextView3 = view3.findViewById<TextView>(R.id.tabTextView)
        tabTextView3.setText(mFragmentTitleList1.get(0))
        val tabImageView3 = view3.findViewById<ImageView>(R.id.tabImageView)
        tabImageView3.setImageResource(mFragmentIconList1.get(0))
        tab3.setCustomView(view3)

        val tab4 = tabs.getTabAt(1)
        assert(tab4 != null)
        tab4!!.setCustomView(null)
        val view4 = LayoutInflater.from(this).inflate(R.layout.custom_tab_address, null)
        val tabTextView4 = view4.findViewById<TextView>(R.id.tabTextView)
        tabTextView4.setText(mFragmentTitleList1.get(1))
        val tabImageView4 = view4.findViewById<ImageView>(R.id.tabImageView)
        tabImageView4.setImageResource(mFragmentIconList1.get(1))
        tab4.setCustomView(view4)

        val tab2 = tabs.getTabAt(2)
        assert(tab2 != null)
        tab2!!.setCustomView(null)
        val view = LayoutInflater.from(this).inflate(R.layout.custom_tab_address, null)
        val tabTextView = view.findViewById<TextView>(R.id.tabTextView)
        tabTextView.setText(mFragmentTitleList1.get(2))
        val tabImageView = view.findViewById<ImageView>(R.id.tabImageView)
        tabImageView.setImageResource(mFragmentIconList1.get(2))
        tab2.setCustomView(view)


        val tab = tabs.getTabAt(position)
        assert(tab != null)
        tab!!.setCustomView(null)
        tab!!.setCustomView(getSelectedTabView1(position))*/
    }

    fun getSelectedTabView1(position: Int): View {
        val view = LayoutInflater.from(this).inflate(R.layout.custom_tab_address, null)
        /*val tabTextView = view.findViewById<TextView>(R.id.tabTextView)
        tabTextView.setText(mFragmentTitleList1.get(position))
        tabTextView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        val tabImageView = view.findViewById<ImageView>(R.id.tabImageView)
        tabImageView.setImageResource(mFragmentIconList1.get(position))
        tabImageView.setColorFilter(
                ContextCompat.getColor(this, R.color.colorAccent),
                PorterDuff.Mode.SRC_ATOP
        )*/
        return view
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun circleReveal(viewID:Int, posFromRight:Int, containsOverflow:Boolean, isShow:Boolean) {
        val myView = findViewById<View>(viewID)
        var width = myView.getWidth()
        if (posFromRight > 0)
            width = width - (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2)
        if (containsOverflow)
            width = width - getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)
        val cx = width
        val cy = myView.getHeight() / 2
        val anim: Animator
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, width.toFloat())
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, width.toFloat(), 0f)
        anim.setDuration(220.toLong())
        // make the view invisible when the animation is done
        anim.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation:Animator) {
                if (!isShow)
                {
                    super.onAnimationEnd(animation)
                    myView.setVisibility(View.INVISIBLE)
                }
            }
        })
        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE)
        // start the animation
        anim.start()
    }

    fun chech() : Boolean{
        if (house.text.toString().trim().isNotEmpty() && land.text.toString().trim().isNotEmpty()) {
            enter_address.setText("save")
            return true
        } else {
            //Toast.makeText(activity,"Fill All Fields",Toast.LENGTH_LONG).show()
            return false
        }
        /*if (tabs.selectedTabPosition==2){
            if (house.text.toString().trim().isNotEmpty() && land.text.toString().trim().isNotEmpty() && other_title.text.toString().trim().isNotEmpty()) {
                enter_address.setText("save")
                return true
            } else {
                Toast.makeText(activity,"Fill All Fields",Toast.LENGTH_LONG).show()
                return false
            }
        }else {
            if (house.text.toString().trim().isNotEmpty() && land.text.toString().trim().isNotEmpty()) {
                enter_address.setText("save")
                return true
            } else {
                Toast.makeText(activity,"Fill All Fields",Toast.LENGTH_LONG).show()
                return false
            }
        }*/
    }

    fun SaveAddress(data:AddressData){
        pDialog= Dialog(activity)
        Appconstands.loading_show(activity, pDialog).show()
        val call = ApproveUtils.Get.AddAddress(data.adrs_type.toString(),
                data.adrs_title.toString(),
                data.adrs_house.toString(),
                data.adrs_landmark.toString(),
                data.address.toString(),
                data.adrs_latitude.toString(),
                data.adrs_longtitude.toString(),
                data.city.toString(),
                utils.userid().toString())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag responce", response.toString())
                pDialog.dismiss()
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {

                        data.adrs_id = example.message!!.toString()
                        println("data_api"+ example.message!!.toString())
                        db.AddressInsert(data)

                        val its = Intent()
                        its.putExtra("adrs_id",data.adrs_id)
                        its.putExtra("adrs_type",data.adrs_type)
                        its.putExtra("adrs_title",data.adrs_title)
                        its.putExtra("address",data.address)
                        its.putExtra("adrs_house",data.adrs_house)
                        its.putExtra("adrs_landmark",data.adrs_landmark)
                        its.putExtra("adrs_latitude",data.adrs_latitude)
                        its.putExtra("adrs_longtitude",data.adrs_longtitude)
                        setResult(Activity.RESULT_OK,its)
                        finish()
                        //startActivity(Intent(activity, HomeActivity::class.java))
                    } else {
                        Toast.makeText(
                                activity,
                                example.message,
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }

                //loading_show(activity).dismiss()
            }

            override fun onFailure(call: Call<Resp>, t: Throwable) {
                Log.e("$tag Fail response", t.toString())
                if (t.toString().contains("time")) {
                    Toast.makeText(
                            activity,
                            "Poor network connection",
                            Toast.LENGTH_LONG
                    ).show()
                }
                pDialog.dismiss()
                //loading_show(activity).dismiss()
            }
        })
    }

    fun EditAddress(data:AddressData){
        pDialog= Dialog(activity)
        Appconstands.loading_show(activity, pDialog).show()
        println("adrs_type : "+data.adrs_type)
        println("adrs_title : "+data.adrs_title)
        println("adrs_house : "+data.adrs_house)
        println("adrs_landmark : "+data.adrs_landmark)
        println("address : "+data.address)
        println("adrs_latitude : "+data.adrs_latitude)
        println("adrs_longtitude : "+data.adrs_longtitude)
        println("userid : "+utils.userid())
        println("adrs_id : "+data.adrs_id)
        val call = ApproveUtils.Get.EditAddress(data.adrs_type.toString(),
                data.adrs_title.toString(),
                data.adrs_house.toString(),
                data.adrs_landmark.toString(),
                data.address.toString(),
                data.adrs_latitude.toString(),
                data.adrs_longtitude.toString(),
                utils.userid().toString(),
                data.city.toString(),
                data.adrs_id.toString())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag responce", response.toString())

                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        db.AddressInsert(data)
                        Toast.makeText(
                                activity,
                                example.message,
                                Toast.LENGTH_LONG
                        ).show()
                        val its = Intent()
                        its.putExtra("adrs_id",data.adrs_id)
                        its.putExtra("adrs_type",data.adrs_type)
                        its.putExtra("adrs_title",data.adrs_title)
                        its.putExtra("address",data.address)
                        its.putExtra("adrs_house",data.adrs_house)
                        its.putExtra("adrs_landmark",data.adrs_landmark)
                        its.putExtra("adrs_latitude",data.adrs_latitude)
                        its.putExtra("adrs_longtitude",data.adrs_longtitude)
                        setResult(Activity.RESULT_OK,its)
                        finish()
                        //startActivity(Intent(activity, HomeActivity::class.java))
                    } else {
                        Toast.makeText(
                                activity,
                                example.message,
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }
                pDialog.dismiss()
                //loading_show(activity).dismiss()
            }

            override fun onFailure(call: Call<Resp>, t: Throwable) {
                Log.e("$tag Fail response", t.toString())
                if (t.toString().contains("time")) {
                    Toast.makeText(
                            activity,
                            "Poor network connection",
                            Toast.LENGTH_LONG
                    ).show()
                }
                pDialog.dismiss()
                //loading_show(activity).dismiss()
            }
        })
    }

    fun searchLocation(str : String) {
        val locationSearch = findViewById(R.id.other_title) as EditText
        val location = str//locationSearch.getText().toString()
        var addressList:List<Address>? = null
        if (location != null || location != "")
        {
            val geocoder = Geocoder(this)
            try
            {
                addressList = geocoder.getFromLocationName(location,1)
                println("srch addressList : "+addressList)
            }
            catch (e:IOException) {
                e.printStackTrace()
            }
            //val address = addressList!!.get(0)

            //println("address : "+address)
            //val latLng = LatLng(address.getLatitude(), address.getLongitude())
            /*mMap.addMarker(MarkerOptions().position(latLng).title(location))
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            Toast.makeText(getApplicationContext(), address.getLatitude() + " " + address.getLongitude(), Toast.LENGTH_LONG).show()*/
        }
    }
}

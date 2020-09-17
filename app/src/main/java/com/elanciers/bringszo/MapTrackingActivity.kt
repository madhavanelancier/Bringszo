package com.elanciers.bringszo

import android.Manifest
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elanciers.bringszo.Common.Appconstands.rupees
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.GpsUtils
import com.elanciers.bringszo.Common.Shimmer
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.LocationObject
import com.elanciers.bringszo.DataClass.OrderDetail
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.track_content.*
import org.jetbrains.annotations.Nullable
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MapTrackingActivity : AppCompatActivity(), OnMapReadyCallback,
    ConnectionCallbacks {
    lateinit var pDialog : Dialog
    lateinit var ab : ActionBar
    val activity = this
    val tag = "Track"
    var orderid = ""
    var ven_lat=""
    var ven_lng=""
    var user_lat=""
    var user_lng=""
    var order_status=""
    var d_mobile=""
    lateinit var utils: Utils
    var current_lat = 0.0
    var current_lng = 0.0
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null
    private var latitudeValue = 0.0
    private var longitudeValue = 0.0
    private var mMap: GoogleMap? = null
    private var mQuery: DBController? = null
    private var autoUpdate: Timer? = null
    private var routeReceiver: RouteBroadCastReceiver? = null
    private var startToPresentLocations: List<LocationObject>? = null
    fun getLocation() {
        try {
            var  location : Location
            var locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                println("locat gps ")
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                //locationmanager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, activity)
                locationManager!!.requestSingleUpdate(
                    criteria,
                    object : LocationListener {
                        override fun onLocationChanged(locatio: Location) {
                            println("singlelocation : " + locatio.latitude + " , " + locatio.longitude)
                            location = locatio
                            current_lat = location.latitude
                            current_lng = location.longitude
                            dr()
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
                if (locationManager != null) {
                    val locatio = locationManager!!
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (locatio != null) {
                        println("lastknown : " + locatio.latitude + " , " + locatio.longitude)
                        location=locatio
                        current_lat = location.latitude
                        current_lng = location.longitude
                        dr()
                    }
                }
            }else{
                println("locat gps ")
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_HIGH
                //locationmanager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, activity)
                locationManager!!.requestSingleUpdate(
                    criteria,
                    object : LocationListener {
                        override fun onLocationChanged(locatio: Location) {
                            println("singlelocation : " + locatio.latitude + " , " + locatio.longitude)
                            location=locatio
                            current_lat = location.latitude
                            current_lng = location.longitude
                            dr()
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
                if (locationManager != null) {
                    val locatio = locationManager!!
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (locatio != null) {
                        println("lastknown : " + locatio.latitude + " , " + locatio.longitude)
                        location=locatio
                        current_lat = location.latitude
                        current_lng = location.longitude
                        dr()
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            println("error : "+e.printStackTrace())
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_tracking)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar);
        ab = supportActionBar!!
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px)
        orderid = intent.extras.getString("orderid")
        ab!!.title = "ORDER #$orderid"
        utils= Utils(this)
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build()
        }
        mQuery = DBController(this)
        startToPresentLocations = mQuery!!.getAllLocationObjects()
        mLocationRequest = createLocationRequest()
        routeReceiver = RouteBroadCastReceiver()


        println("orderid : "+orderid)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    fun gps(){
        GpsUtils(this).turnGPSOn(object: GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable:Boolean/*,Location:Location*/) {
                // turn on GPS
                if (isGPSEnable) {
                    println("network & gps else " + isGPSEnable)
                    /*println("lat : " + Location.latitude)
                    println("lng : " + Location.longitude)
                    current_lat = Location.latitude
                    current_lng = Location.longitude*/
                    getLocation()
                }else{
                    gps()
                }
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        gps()
    }

    private fun markStartingLocationOnMap(mapObject: GoogleMap?, location: LatLng) {
        mapObject!!.addMarker(MarkerOptions().position(location).title("Your location").icon(
            /*bitmapDescriptorFromVector(this,R.drawable.ic_single_motorbike)*/
            BitmapDescriptorFactory.fromResource(R.mipmap.placeholder)
        ))
        mapObject.moveCamera(CameraUpdateFactory.newLatLng(location))
    }
    private fun markEndingLocationOnMap(mapObject: GoogleMap?, location: LatLng) {

        if (order_status==("Pickup")) {
            mapObject!!.addMarker(
                MarkerOptions().position(location).title("").icon(
                    /* bitmapDescriptorFromVector(this,R.drawable.ic_shop)*/
                    BitmapDescriptorFactory.fromResource(R.mipmap.bicycle)
                )
            )
            //mapObject.moveCamera(CameraUpdateFactory.newLatLng(location))
        }else{
            mapObject!!.addMarker(
                MarkerOptions().position(location).title("").icon(
                    /*bitmapDescriptorFromVector(this,R.drawable.ic_customer)*/
                    BitmapDescriptorFactory.fromResource(R.mipmap.store)
                )
            )
            //mapObject.moveCamera(CameraUpdateFactory.newLatLng(location))
        }
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
                                "Latitude 4: $latitudeValue Longitude 4: $longitudeValue"
                            )
                            refreshMap(mMap)
                            markStartingLocationOnMap(mMap, LatLng(latitudeValue, longitudeValue))
                            startPolyline(mMap, LatLng(latitudeValue, longitudeValue))
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        })
    }

    override fun onConnectionSuspended(i: Int) {}
    private inner class RouteBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val local = intent.extras.getString("RESULT_CODE")!!
            if (local == "LOCAL") { //get all data from database
                startToPresentLocations = mQuery!!.getAllLocationObjects()
                if (startToPresentLocations!!.size > 0) { //prepare map drawing.
                    val locationPoints =
                        getPoints(startToPresentLocations)
                    refreshMap(mMap)
                    markStartingLocationOnMap(mMap, locationPoints[0])
                    drawRouteOnMap(mMap, locationPoints)
                }
            }
        }
    }

    fun dr (){
        val lst = ArrayList<LatLng>()
        var data = LatLng(current_lat,current_lng)//(9.927537,78.099764)
        lst.add(data)
        data = LatLng(9.927769,78.105708)
        //lst.add(data)
        data = LatLng(9.928398,78.108020)
        //lst.add(data)
        if (ven_lat!="") {
            data = LatLng(ven_lat.toDouble(), ven_lng.toDouble())//(9.930270,78.108218)
            lst.add(data)
        }
        /*}else{
            data = LatLng(user_lat.toDouble(), user_lng.toDouble())//(9.930270,78.108218)
            lst.add(data)
        }*/
        //9.927537, 78.099764
        //9.927769, 78.105708
        //9.928398, 78.108020
        //9.930270, 78.108218
        val locationPoints =lst
        //getPoints(startToPresentLocations)
        refreshMap(mMap)
        markStartingLocationOnMap(mMap, locationPoints.first())
        if (ven_lat!="") {
            markEndingLocationOnMap(mMap, locationPoints.last())
        }
        drawRouteOnMap(mMap, locationPoints)
    }

    private fun getPoints(mLocations: List<LocationObject>?): List<LatLng> {
        val points: MutableList<LatLng> = ArrayList()
        for (mLocation in mLocations!!) {
            points.add(LatLng(mLocation.Latitude!!, mLocation.Longitude!!))
        }
        return points
    }

    private fun startPolyline(map: GoogleMap?, location: LatLng) {
        if (map == null) {
            Log.d(TAG, "Map object is not null")
            return
        }
        val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        options.add(location)
        val polyline = map.addPolyline(options)
        val cameraPosition = CameraPosition.Builder()
            .target(location)
            .zoom(16f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun drawRouteOnMap(
        map: GoogleMap?,
        positions: List<LatLng>
    ) {
        /*val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        options.addAll(positions)
        val polyline = map!!.addPolyline(options)*/
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(positions[0].latitude, positions[0].longitude))
            .zoom(17f)
            .bearing(90f)
            .tilt(40f)
            .build()
        map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun refreshMap(mapInstance: GoogleMap?) {
        mapInstance!!.clear()
    }

    protected fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 5000
        mLocationRequest.fastestInterval = 3000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return mLocationRequest
    }

    override fun onResume() {
        super.onResume()
        if (routeReceiver == null) {
            routeReceiver = RouteBroadCastReceiver()
        }
        val filter = IntentFilter(RouteService.ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(routeReceiver!!, filter)
        autoUpdate = Timer()
        autoUpdate!!.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread { CheckSts(orderid) }
            }
        }, 0, 20000)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(routeReceiver!!)
        autoUpdate!!.cancel()
    }

    override fun onStart() {
        mGoogleApiClient!!.connect()
        super.onStart()
    }

    override fun onStop() {
        mGoogleApiClient!!.disconnect()
        super.onStop()
    }

    companion object {
        private val TAG = MapTrackingActivity::class.java.simpleName
    }

    fun CheckSts(sid:String){
        /*val pDialo = ProgressDialog(this);
        pDialo.setMessage("Loading....");
        pDialo.setIndeterminate(false);
        pDialo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialo.setCancelable(false);
        pDialo.show()*/
        //pDialog = Dialog(activity)
        //Appconstands.loading_show(activity, pDialog).show()
        val call = ApproveUtils.Get.getsts(sid,utils.zoneid())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag responce", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val ad = response.body() as Resp
                        val z = 0
                        val id = if (ad.response!![z].id.isNullOrEmpty()) "" else ad.response!![z].id.toString()
                        val vid = if (ad.response!![z].vid.isNullOrEmpty()) "" else ad.response!![z].vid.toString()
                        val location = if (ad.response!![z].location.isNullOrEmpty()) "" else ad.response!![z].location.toString()
                        val subtotal = if (ad.response!![z].subtotal.isNullOrEmpty()) "" else ad.response!![z].subtotal.toString()
                        val discount = if (ad.response!![z].discount.isNullOrEmpty()) "" else ad.response!![z].discount.toString()
                        val delivery = if (ad.response!![z].delivery.isNullOrEmpty()) "" else ad.response!![z].delivery.toString()
                        val ven_address = if (ad.response!![z].ven_address.isNullOrEmpty()) "" else ad.response!![z].ven_address.toString()
                        val user_address = if (ad.response!![z].user_address.isNullOrEmpty()) "" else ad.response!![z].user_address.toString()
                        order_status = if (ad.response!![z].order_status.isNullOrEmpty()) "" else ad.response!![z].order_status.toString()
                        val user_type = if (ad.response!![z].user_type.isNullOrEmpty()) "" else ad.response!![z].user_type.toString()
                        val datetime = if (ad.response!![z].datetime.isNullOrEmpty()) "" else ad.response!![z].datetime.toString()
                        val coupon_code = if (ad.response!![z].coupon_code.isNullOrEmpty()) "" else ad.response!![z].coupon_code.toString()
                        val total = if (ad.response!![z].total.isNullOrEmpty()) "" else ad.response!![z].total.toString()
                        ven_lat = if (ad.response!![z].ven_lat.isNullOrEmpty()) "" else ad.response!![z].ven_lat.toString()
                        ven_lng = if (ad.response!![z].ven_lng.isNullOrEmpty()) "" else ad.response!![z].ven_lng.toString()
                        user_lat = if (ad.response!![z].user_lat.isNullOrEmpty()) "" else ad.response!![z].user_lat.toString()
                        user_lng = if (ad.response!![z].user_lng.isNullOrEmpty()) "" else ad.response!![z].user_lng.toString()
                        val payment_mode = if (ad.response!![z].payment_mode.isNullOrEmpty()) "" else ad.response!![z].payment_mode.toString()
                        val delivery_boy = if (ad.response!![z].delivery_boy.isNullOrEmpty()) "" else ad.response!![z].delivery_boy.toString()
                        d_mobile = if (ad.response!![z].d_mobile.isNullOrEmpty()) "" else ad.response!![z].d_mobile.toString()
                        val d_lat = if (ad.response!![z].d_lat.isNullOrEmpty()) "" else ad.response!![z].d_lat.toString()
                        val d_lng = if (ad.response!![z].d_lng.isNullOrEmpty()) "" else ad.response!![z].d_lng.toString()
                        val order_details = ad.response!![z].order_details
                        var itm = ""
                        val srr = JSONArray()
                        for (ads in 0 until order_details!!.size) {
                            val obj = JSONObject()
                            val product = if (order_details[ads].product.isNullOrEmpty()) "" else order_details[ads].product.toString()
                            val qty = if (order_details[ads].qty.isNullOrEmpty()) "" else order_details[ads].qty.toString()
                            val mesure = if (order_details[ads].mesure.isNullOrEmpty()) "" else order_details[ads].mesure.toString()
                            val price = if (order_details[ads].price.isNullOrEmpty()) "" else order_details[ads].price.toString()
                            val tota = if (order_details[ads].total.isNullOrEmpty()) "" else order_details[ads].total.toString()
                            itm = itm + "" + product + " " + mesure + " x " + qty + ","
                            obj.put("product",product)
                            obj.put("qty",qty)
                            obj.put("mesure",mesure)
                            obj.put("price",price)
                            obj.put("total",tota)
                            srr.put(obj)
                        }
                        val tm = SimpleDateFormat("H:mm").format(Date())
                        ab!!.subtitle = "${order_details!!.size} items,$rupees$total | $tm"
                        println("srr : "+srr)
                        val data = OrderDetail()
                        data.id = id
                        data.name = vid
                        data.loc = location
                        data.price = total
                        data.subtotal = subtotal
                        data.delivery = delivery
                        data.deliver_sts = order_status
                        data.discount = discount
                        data.items = itm.removeSuffix(",")//"Thuvaram Paruppu 500 gms x 1, Coconut Oil 500 ml x 1"
                        data.dateandtime = datetime
                        data.details = srr
                        data.order_details = order_details
                        data.shopnm = vid
                        data.shoploc = ven_address
                        data.adrs_type =user_type
                        data.coupon_code =coupon_code
                        data.adrs = user_address
                        data.payment_mode = payment_mode
                        println("order_status : "+order_status)
                        val shimmer = Shimmer()
                        shimmer.start(rec)
                        shimmer.start(pre)
                        shimmer.start(pic)
                        shimmer.start(de)
                        if (order_status == "Pending") {
                            orderlive.setVisibility(View.INVISIBLE)
                            orderdone.setVisibility(View.VISIBLE)
                            cookinglive.setVisibility(View.INVISIBLE)
                            cookingdone.setVisibility(View.INVISIBLE)
                            picklive.setVisibility(View.INVISIBLE)
                            pickdone.setVisibility(View.INVISIBLE)
                            deliv.setVisibility(View.GONE)
                            deliverylive.setVisibility(View.INVISIBLE)
                            deliverydone.setVisibility(View.INVISIBLE)
                        } else if (order_status == "Processing") {
                            orderlive.setVisibility(View.INVISIBLE)
                            orderdone.setVisibility(View.INVISIBLE)
                            cookinglive.setVisibility(View.INVISIBLE)
                            cookingdone.setVisibility(View.VISIBLE)
                            picklive.setVisibility(View.INVISIBLE)
                            pickdone.setVisibility(View.INVISIBLE)
                            deliv.setVisibility(View.GONE)
                            deliverylive.setVisibility(View.INVISIBLE)
                            deliverydone.setVisibility(View.INVISIBLE)
                        } else if (order_status == "Pickup") {
                            orderlive.setVisibility(View.INVISIBLE)
                            orderdone.setVisibility(View.INVISIBLE)
                            cookinglive.setVisibility(View.INVISIBLE)
                            cookingdone.setVisibility(View.INVISIBLE)
                            picklive.setVisibility(View.INVISIBLE)
                            pickdone.setVisibility(View.VISIBLE)
                            deliv.setVisibility(View.VISIBLE)
                            deliverylive.setVisibility(View.INVISIBLE)
                            deliverydone.setVisibility(View.INVISIBLE)
                            d_name.setText(delivery_boy+","+d_mobile)
                            ven_lat = d_lat
                            ven_lng = d_lng
                        } else if (order_status == "Delivered") {
                            orderlive.setVisibility(View.INVISIBLE)
                            orderdone.setVisibility(View.INVISIBLE)
                            cookinglive.setVisibility(View.INVISIBLE)
                            cookingdone.setVisibility(View.INVISIBLE)
                            picklive.setVisibility(View.INVISIBLE)
                            pickdone.setVisibility(View.INVISIBLE)
                            deliv.setVisibility(View.GONE)
                            deliverylive.setVisibility(View.INVISIBLE)
                            deliverydone.setVisibility(View.VISIBLE)
                            val intent = Intent(activity, OrderDetailActivity::class.java)
                            intent.putExtra("id",id)
                            intent.putExtra("details",srr.toString())
                            intent.putExtra("delivery",delivery)
                            intent.putExtra("discount",discount)
                            intent.putExtra("total",total)
                            intent.putExtra("subtotal",subtotal)
                            intent.putExtra("deliver_sts",order_status)
                            intent.putExtra("dateandtime",(System.currentTimeMillis()/1000).toString())//datetime
                            intent.putExtra("order_details",order_details.toString())
                            intent.putExtra("shopnm",vid)
                            intent.putExtra("shoploc",ven_address)
                            intent.putExtra("adrs_type",user_type)
                            intent.putExtra("adrs",user_address)
                            intent.putExtra("coupon_code",coupon_code)
                            intent.putExtra("payment_mode",payment_mode)
                            startActivity(intent)
                            finish()
                        } else if (order_status == "Declined"){
                            Toast.makeText(activity,"Order Cancelled",Toast.LENGTH_LONG).show()
                            finish()
                        }
                        dr()
                    } else {
                        Toast.makeText(
                            activity,
                            example.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                //pDialog.dismiss()
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
                //pDialog.dismiss()
                //loading_show(activity).dismiss()
            }
        })
    }
    fun Open_call(view: View){
        val  intent = Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "$d_mobile"));
        startActivity(intent);
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.profile_page, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()
        when(id){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

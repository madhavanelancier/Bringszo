package com.elanciers.bringszo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.elanciers.bringszo.Common.Appconstands.GOOGLE_API_KEY
import com.elanciers.bringszo.Common.Appconstands.RADIUS_1000
import com.elanciers.bringszo.Common.Appconstands.TYPE_BAR
import com.elanciers.bringszo.Common.MapsController
import com.elanciers.bringszo.DataClass.Route
import com.elanciers.bringszo.DataClass.Spot
import com.elanciers.bringszo.directions.Directions
import com.elanciers.bringszo.nearbySearch.NearbySearch
import com.elanciers.bringszo.retrofit.RetrofitClient2
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mMapsController: MapsController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        mMapsController = MapsController(this, mGoogleMap)
        mMapsController.setCustomMarker()

        zoomToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mMapsController.animateZoomInCamera()
            } else {
                mMapsController.animateZoomOutCamera()
            }
        }

        placesToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            progressLayout.visibility = View.VISIBLE

            if (isChecked) {
                val position = mGoogleMap.cameraPosition.target.latitude.toString() + "," + mGoogleMap.cameraPosition.target.longitude.toString()
                val placesCall = RetrofitClient2.googleMethods().getNearbySearch(position, RADIUS_1000, TYPE_BAR, GOOGLE_API_KEY)
                placesCall.enqueue(object : Callback<NearbySearch> {
                    override fun onResponse(call: Call<NearbySearch>, response: Response<NearbySearch>) {
                        val nearbySearch = response.body()!!

                        if (nearbySearch.status.equals("OK")) {
                            val spotList = ArrayList<Spot>()

                            for (resultItem in nearbySearch.results!!) {
                                val spot = Spot(resultItem.name, resultItem.geometry.location?.lat, resultItem.geometry.location?.lng)
                                spotList.add(spot)
                            }

                            mMapsController.setMarkersAndZoom(spotList)
                        } else {
                            toast(nearbySearch.status)
                            placesToggleButton.isChecked = false
                        }

                        progressLayout.visibility = View.GONE
                    }

                    override fun onFailure(call: Call<NearbySearch>, t: Throwable) {
                        toast(t.toString())
                        progressLayout.visibility = View.GONE
                        placesToggleButton.isChecked = false
                    }
                })
            } else {
                progressLayout.visibility = View.GONE
                mMapsController.clearMarkers()
            }
        }

        directionsToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            progressLayout.visibility = View.VISIBLE

            if (isChecked) {
                val directionsCall = RetrofitClient2.googleMethods().getDirections(getString(R.string.time_square), getString(R.string.chelsea_market), GOOGLE_API_KEY)
                directionsCall.enqueue(object : Callback<Directions> {
                    override fun onResponse(call: Call<Directions>, response: Response<Directions>) {
                        Log.e("maps response", response.toString())
                        val directions = response.body()!!

                        if (directions.status.equals("OK")) {
                            val legs = directions.routes[0].legs[0]
                            val route = Route(getString(R.string.time_square), getString(R.string.chelsea_market), legs.startLocation.lat, legs.startLocation.lng, legs.endLocation.lat, legs.endLocation.lng, directions.routes[0].overviewPolyline.points)

                            mMapsController.setMarkersAndRoute(route)
                        } else {
                            toast(directions.status)
                            directionsToggleButton.isChecked = false
                        }

                        progressLayout.visibility = View.GONE
                    }

                    override fun onFailure(call: Call<Directions>, t: Throwable) {
                        Log.e("maps fail res", t.toString())
                        toast(t.toString())
                        progressLayout.visibility = View.GONE
                        directionsToggleButton.isChecked = false
                    }
                })
            } else {
                progressLayout.visibility = View.GONE
                mMapsController.clearMarkersAndRoute()
            }
        }
    }

    private fun toast(status: String) {
        Toast.makeText(this,status,Toast.LENGTH_LONG).show()
    }
}
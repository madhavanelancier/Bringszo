package com.elanciers.bringszo

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.elanciers.bringszo.Adapters.StoreListAdapter
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.StoreItem
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_stores.*
import kotlinx.android.synthetic.main.content_store.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoresActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, StoreListAdapter.OnBottomReachedListener,StoreListAdapter.OnItemClickListener {
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val maxScroll = appBarLayout!!.getTotalScrollRange()
        val percentage = Math.abs(verticalOffset).toFloat() / maxScroll.toFloat()

        if (percentage == 1f && isHideToolbarView) {
            //toolbarHeaderView.setVisibility(View.VISIBLE)
            isHideToolbarView = !isHideToolbarView
            invalidateOptionsMenu()

        } else if (percentage < 1f && !isHideToolbarView) {
            //toolbarHeaderView.setVisibility(View.GONE)
            isHideToolbarView = !isHideToolbarView
            invalidateOptionsMenu()
        }

    }
    private var isHideToolbarView = false
    override fun OnItemClick(view: View, position: Int, viewType: Int) {
        println("type : "+stores[position].typeid)
        println("vendor_id : "+stores[position].id)
        val it = Intent(this, ProductActivity::class.java)
        it.putExtra("typeid",stores[position].typeid)
        it.putExtra("vendor_id",stores[position].id)
        it.putExtra("name",stores[position].name)
        it.putExtra("dur", stores[position].tim)
        it.putExtra("loc",stores[position].loc)
        utils.setVendor(stores[position].typeid.toString(),stores[position].id.toString(),stores[position].name.toString(),stores[position].img.toString())
        startActivity(it)
    }

    override fun onBottomReached(position: Int) {
        //getStores(start)
    }
    val tag = "Store"
    val activity = this
    lateinit var utils : Utils
    lateinit var pDialog: Dialog

    var stores = ArrayList<StoreItem>()
    lateinit var storesadp : StoreListAdapter
    var start =0
    var typenm =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stores)
        appbar.addOnOffsetChangedListener(activity)
        Appconstands.changeStatusBarColor(this, R.color.statusbar)
        utils = Utils(activity)
        val tool = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tool)
        val ab = supportActionBar
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.back_arrow)
        ab!!.setTitle(intent.extras.getString("name"))
        typenm =(intent.extras.getString("name"))

        searchedit.setOnClickListener {
            val extra = intent.extras
            val id  = extra.getString("id")
            val lat  = extra.getDouble("lat")
            val lng  = extra.getDouble("lng")
            val its = Intent(activity, SearchActivity::class.java)
            its.putExtra("id", id)
            its.putExtra("typenm",typenm)
            its.putExtra("lat", lat)
            its.putExtra("lng", lng)
            startActivity(its)
        }

        var data = StoreItem()
        data.id = "1"
        data.name = "Departmental Store"
        data.img = ""
        data.loc = "5.3 km Anna Nagar"
        data.tim = "15 mins"
        data.offer = "50% upto RS.100, Min order Rs.50"
        //stores.add(data)
        data = StoreItem()
        data.id = "2"
        data.name = "Grocery Shop"
        data.img = ""
        data.loc = "7.2 km Anupanadi"
        data.tim = "15 mins"
        data.offer = "50% upto RS.100, Min order Rs.50"
        //stores.add(data)

        storesadp = StoreListAdapter(activity,stores,activity,activity)
        recyclervendorlist.adapter = storesadp

        if (Appconstands.net_status(activity)){
            store_layout.visibility = View.GONE
            store_shimmer.visibility = View.VISIBLE
            store_shimmer.startShimmerAnimation()
            getStores(0)
        }
    }

    fun getStores(star:Int){
        val extra = intent.extras
        val id  = extra.getString("id")
        val lat  = extra.getDouble("lat")
        val lng  = extra.getDouble("lng")
        //loading_show(activity, pDialog).show()
        println("lat : "+lat)
        println("lng : "+lng)
        println("id : "+id)
        println("start : "+star)
        val call = ApproveUtils.Get.getVendors(lat.toString(),lng.toString(),id.toString(),star.toString())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val slid = response.body() as Resp
                        val res = slid.response!!
                        noof.setText(slid.message.toString()+" Shops Available")
                        for (h in 0 until res.size){
                            val data = StoreItem()
                            data.id =res[h].id
                            data.typeid =res[h].type_id
                            data.img = res[h].image
                            data.name = res[h].name
                            data.distance = res[h].distance
                            data.loc = res[h].location
                            data.tim = res[h].duration
                            data.offer = if (res[h].offer.isNullOrEmpty()) "" else res[h].offer.toString()//""//50% upto RS.100, Min order Rs.50
                            stores.add(data)
                            storesadp.notifyDataSetChanged()
                        }
                        start = start + 15
                    }
                }
                //pDialog.dismiss()
                store_layout.visibility = View.VISIBLE
                store_shimmer.visibility = View.GONE
                store_shimmer.stopShimmerAnimation()
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
                store_layout.visibility = View.VISIBLE
                store_shimmer.visibility = View.GONE
                store_shimmer.stopShimmerAnimation()
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; activity adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search_menu, menu)
        if (isHideToolbarView){
            menu.findItem(R.id.search).setVisible(false)
        }else{
            menu.findItem(R.id.search).setVisible(true)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()
        when(id){
            android.R.id.home -> {
                //onBackPressed()
                finish()
                return true
            }
            R.id.search -> {
                val extra = intent.extras
                val id  = extra.getString("id")
                val lat  = extra.getDouble("lat")
                val lng  = extra.getDouble("lng")
                val its = Intent(activity, ProductSearchActivity::class.java)
                its.putExtra("id", id)
                its.putExtra("typenm",typenm)
                its.putExtra("lat", lat)
                its.putExtra("lng", lng)
                startActivity(its)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
/*{
    "Status": "Success",
    "Message": 8,
    "Response": [
        {
            "id": 84,
            "name": "RR Super Market",
            "distance": "4.5 KM",
            "location": "Mattuthavani",
            "duration": "16 mins"
        },
        {
            "id": 83,
            "name": "A@1",
            "distance": "4.5 KM",
            "location": "Dsdc",
            "duration": "16 mins"
        },
        {
            "id": 82,
            "name": "Malikai stores",
            "distance": "4.5 KM",
            "location": "Othakadai",
            "duration": "16 mins"
        },
        {
            "id": 81,
            "name": "K . Pudur",
            "distance": "4.5 KM",
            "location": "K pudur",
            "duration": "16 mins"
        },
        {
            "id": 80,
            "name": "ANNA NAGAR",
            "distance": "4.5 KM",
            "location": "Anna nagar",
            "duration": "16 mins"
        },
        {
            "id": 78,
            "name": "Big bazzar",
            "distance": "4.5 KM",
            "location": "Arasaradi",
            "duration": "16 mins"
        },
        {
            "id": 77,
            "name": "A to Z",
            "distance": "4.5 KM",
            "location": "Anna nagar",
            "duration": "16 mins"
        },
        {
            "id": 75,
            "name": "Kannan Store",
            "distance": "4.5 KM",
            "location": "Tvs Nagar",
            "duration": "16 mins"
        }
    ]
}*/
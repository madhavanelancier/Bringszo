package com.elanciers.bringszo

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.elanciers.booking.DataClass.SpinnerPojo
import com.elanciers.bringszo.Adapters.OfferListAdapter
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.OfferItem
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.connection_error.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OffersActivity : AppCompatActivity() , OfferListAdapter.OnBottomReachedListener,
    OfferListAdapter.OnItemClickListener {
    override fun OnItemClick(view: View, position: Int, viewType: Int) {
        println("type : " + stores[position].typeid)
        println("vendor_id : " + stores[position].id)
        val it = Intent(activity, ProductActivity::class.java)
        it.putExtra("typeid", stores[position].typeid)
        it.putExtra("vendor_id", stores[position].id)
        it.putExtra("name", stores[position].name)
        it.putExtra("dur", stores[position].tim)
        it.putExtra("loc", stores[position].loc)
        utils.setVendor(
            stores[position].typeid.toString(),
            stores[position].id.toString(),
            stores[position].name.toString(),
            stores[position].img.toString()
        )
        startActivity(it)


    }

    override fun onBottomReached(position: Int) {
        if (stores.size>14){
            if (start==0) {
                stores.clear()
                storesadp.notifyDataSetChanged()
                store_shimmer.visibility = View.VISIBLE
                store_shimmer.startShimmerAnimation()
                getStores(start, src)
            }else {
                start = start + stores.size
                getStores(start, src)
            }
        }
    }

    val tag = "MainSrch"
    val activity = this
    lateinit var utils: Utils
    lateinit var pDialog: Dialog
    lateinit var db: DBController

    var stores = ArrayList<OfferItem>()
    lateinit var storesadp: OfferListAdapter
    var start = 0
    var src = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appconstands.changeStatusBarColor(this, R.color.statusbar)
        setContentView(R.layout.activity_offers)
        back.setOnClickListener {
            onBackPressed()
        }
        utils = Utils(activity)
        pDialog = Dialog(activity)
        db = DBController(activity)
        storesadp = OfferListAdapter(this, stores, this, this)
        vendorlist.adapter = storesadp

        searchedit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                src = searchedit.text.trim().toString()

                if (Appconstands.net_status(activity)) {
                    //vendorlist.visibility = View.GONE
                    //store_shimmer.visibility = View.VISIBLE
                    //store_shimmer.startShimmerAnimation()
                    println("p0!!.trim().toString() : " + src)
                    if (src.length > 2) {
                        stores.clear()
                        storesadp.notifyDataSetChanged()
                        store_shimmer.visibility = View.VISIBLE
                        store_shimmer.startShimmerAnimation()
                        getStores(0, searchedit.text.trim().toString())
                    }
                } else {
                    connection.visibility = View.VISIBLE
                }
            }

        })
        view_cart_lay()

        if (Appconstands.net_status(activity)) {
            if (start == 0) {
                stores.clear()
                storesadp.notifyDataSetChanged()
                store_shimmer.visibility = View.VISIBLE
                store_shimmer.startShimmerAnimation()
                getStores(start, src)
            } else {
                getStores(start, src)
            }
        }
        conbtn.setOnClickListener {
            if (Appconstands.net_status(activity)) {
                if (start == 0) {
                    stores.clear()
                    storesadp.notifyDataSetChanged()
                    store_shimmer.visibility = View.VISIBLE
                    store_shimmer.startShimmerAnimation()
                    getStores(start, src)
                } else {
                    getStores(start, src)
                }
            }
        }
    }

    fun getStores(star: Int, search: String) {
        connection.visibility = View.GONE
        val extra = intent.extras
        val lat = extra.getDouble("lat")
        val lng = extra.getDouble("lng")
        //loading_show(activity, pDialog).show()
        println("lat : " + lat)
        println("lng : " + lng)
        println("start : " + star)
        val call = ApproveUtils.Get.Offer(
            //lat.toString(),
            //lng.toString(),
            search.toString(),
            star.toString()
        )
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val slid = response.body() as Resp
                        val res = slid.response!!
                        //noof.setText(slid.message.toString()+" Shops Available")
                        //val res = res1[0].Vendor!!
                        for (h in 0 until res.size) {
                            val data = OfferItem()
                            data.id = if (res[h].vendor_id.isNullOrEmpty())"" else res[h].vendor_id.toString()
                            data.typeid = if (res[h].type_id.isNullOrEmpty())"" else res[h].type_id.toString()
                            data.img = if (res[h].vendor_image.isNullOrEmpty())"" else res[h].vendor_image.toString()
                            data.name = if (res[h].name.isNullOrEmpty())"" else res[h].name.toString()
                            data.distance = if (res[h].distance.isNullOrEmpty())"" else res[h].distance.toString()
                            data.loc = if (res[h].location.isNullOrEmpty())"" else res[h].location.toString()
                            data.tim = if (res[h].duration.isNullOrEmpty())"" else res[h].duration.toString()
                            data.offer = ""//50% upto RS.100, Min order Rs.50

                            //product
                            val pid = if (res[h].pid.isNullOrEmpty())"" else res[h].pid.toString()
                            val pname = if (res[h].pname.isNullOrEmpty())"" else res[h].pname.toString()
                            val image = if (res[h].image.isNullOrEmpty())"" else res[h].image.toString()
                            val option = res[h].option
                            val options = ArrayList<SpinnerPojo>()
                            for (o in 0 until option!!.size){
                                val sdata = SpinnerPojo()
                                val price = option[o].price
                                val opname = option[o].name
                                sdata.id = o.toString()
                                sdata.name = opname
                                sdata.price = price
                                options.add(sdata)
                            }
                            data.title = ""
                            data.sid = ""
                            data.pid = pid
                            data.pname = pname
                            data.qty = "0"
                            data.nm = options[0].name
                            data.price = options[0].price
                            data.selpos = 0
                            data.pimg =image
                            data.options = options
                            stores.add(data)
                        }

                    }
                }
                //pDialog.dismiss()
                //store_layout.visibility = View.VISIBLE
                store_shimmer.visibility = View.GONE
                store_shimmer.stopShimmerAnimation()
                adp()
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
                adp()
                //pDialog.dismiss()
                //loading_show(activity).dismiss()
                //store_layout.visibility = View.VISIBLE
                store_shimmer.visibility = View.GONE
                store_shimmer.stopShimmerAnimation()
                //connection.visibility = View.VISIBLE
            }
        })
    }

    fun adp() {
        storesadp.notifyDataSetChanged()
        if (stores.isEmpty()) {

        }
    }

    fun view_cart_lay(){
        val tot = db.cartTotal.toString()
        println("db.cartTotal : "+db.cartTotal)
        if (db.cartTotal!=0.toDouble()/*&&(db.vendor_id==utils.getvendor_id())*/) {
            val vendor_nm = db.vendor_nm
            val items = db.cartItem.toString()
            cart_layout.visibility = View.VISIBLE
            tot_items.setText(items+" items - ")
            tot_price.setText("₹ "+tot)
            shop.setText("From : "+vendor_nm)
            continue_cart.setOnClickListener {
                startActivity(Intent(activity, CartActivity::class.java))
            }
        }else{
            cart_layout.visibility = View.GONE
        }

    }
}

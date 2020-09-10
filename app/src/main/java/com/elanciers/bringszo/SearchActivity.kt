package com.elanciers.bringszo

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.elanciers.bringszo.Adapters.StoreListAdapter
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.StoreItem
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.connection_error.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(){
/*override fun OnItemClick(view: View, position: Int, viewType: Int) {
        println("type : "+stores[position].typeid)
        println("vendor_id : "+stores[position].id)
        val it = Intent(this, ProductActivity::class.java)
        it.putExtra("typeid",stores[position].typeid)
        it.putExtra("vendor_id",stores[position].id)
        it.putExtra("name",stores[position].name)
        it.putExtra("loc",stores[position].loc)
        utils.setVendor(stores[position].typeid.toString(),stores[position].id.toString(),stores[position].name.toString(),stores[position].img.toString())
        startActivity(it)
    }*/



    val tag = "Search"
    val activity = this
    lateinit var utils : Utils
    lateinit var pDialog: Dialog


    lateinit var storesadp : StoreListAdapter
    var start =0
    var src = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appconstands.changeStatusBarColor(this, R.color.statusbar)
        setContentView(R.layout.activity_search)
        utils = Utils(activity)
        val t = intent.extras.getString("typenm")
        searchedit.setHint("Search for product")

        back.setOnClickListener{
            onBackPressed()
        }



        //searcheditreferal

        if (Appconstands.net_status(activity)){

        }
        conbtn.setOnClickListener {
            if (Appconstands.net_status(activity)){
                if (start==0) {
                    //stores.clear()
                    //storesadp.notifyDataSetChanged()
                    store_shimmer.visibility = View.VISIBLE
                    store_shimmer.startShimmerAnimation()
                    getStores(start, src)
                }else {
                    getStores(start, src)
                }
            }
        }
    }

    fun getStores(star:Int,search:String){
        connection.visibility=View.GONE
        val stores = ArrayList<StoreItem>()
        val extra = intent.extras
        val id  =  extra.getString("id")
        val lat  = extra.getDouble("lat")
        val lng  = extra.getDouble("lng")
        //loading_show(activity, pDialog).show()
        println("lat : "+lat)
        println("lng : "+lng)
        println("id : "+id)
        println("start : "+star)
        val call = ApproveUtils.Get.Search(lat.toString(),lng.toString(),id.toString(),search.toString(),star.toString())
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
                        for (h in 0 until res.size){
                            val data = StoreItem()
                            data.id =res[h].id
                            data.typeid =res[h].type_id
                            data.img = res[h].image
                            data.name = res[h].name
                            data.distance = res[h].distance
                            data.loc = res[h].location
                            data.tim = res[h].duration
                            data.offer = ""//50% upto RS.100, Min order Rs.50
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
                connection.visibility=View.VISIBLE
            }
        })
        val storesadp = StoreListAdapter(this, stores,
            object : StoreListAdapter.OnItemClickListener {
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

            },object : StoreListAdapter.OnBottomReachedListener{
                override fun onBottomReached(position: Int) {

                }
            })
        vendorlist.adapter = storesadp
    }
    fun adp(){
        ///storesadp.notifyDataSetChanged()

    }
}

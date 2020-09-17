package com.elanciers.bringszo

import android.app.Activity
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
import com.elanciers.bringszo.Adapters.CouponsAdapter
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.Coupons
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_coupon.*
import kotlinx.android.synthetic.main.connection_error.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouponActivity : AppCompatActivity(), CouponsAdapter.OnBottomReachedListener,CouponsAdapter.OnItemClickListener {
    override fun OnItemClick(view: View, position: Int, viewType: Int) {
        val its = Intent()
        its.putExtra("id",Couponslist[position].id)
        its.putExtra("dis",Couponslist[position].dis)
        its.putExtra("use",Couponslist[position].use)
        its.putExtra("small_dis",Couponslist[position].small_dis)
        its.putExtra("minimum_order",Couponslist[position].minimum_order)
        setResult(Activity.RESULT_OK,its)
        finish()
    }

    override fun onBottomReached(position: Int) {

    }
    val tag = "Coupon"
    val activity = this
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog

    val Couponslist = ArrayList<Coupons>()
    lateinit var adp : CouponsAdapter
    lateinit var ab : androidx.appcompat.app.ActionBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appconstands.changeStatusBarColor(this, R.color.statusbar)
        setContentView(R.layout.activity_coupon)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar);
        ab = supportActionBar!!
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px)
        ab!!.title = "APPLY COUPONS"
        utils = Utils(this)
        db = DBController(this)
        pDialog = Dialog(this)

        var data = Coupons()
        data.id = "1"
        data.dis = "50%"
        data.use = "use Mastercard"
        data.small_dis = "Use code 100MASTERCARD to get 50% discount up to Rs.100 on orders Rs.199 and above..."
        data.brief = arrayListOf("")
        //Couponslist.add(data)
        data = Coupons()
        data.id = "1"
        data.dis = "20%"
        data.use = "use Rupay"
        data.small_dis = "Use code 100RUPAY to get 20% discount up to Rs.80 on orders Rs.199 and above..."
        data.brief = arrayListOf("")
        //Couponslist.add(data)
        adp = CouponsAdapter(this,Couponslist,this,this)
        coupon_list.adapter = adp
        if (Appconstands.net_status(activity)) {
            getCoupon()
        }else{
            connection.visibility=View.VISIBLE
        }

        conbtn.setOnClickListener {
            if (Appconstands.net_status(activity)) {
                connection.visibility=View.GONE
                getCoupon()
            }else{
                connection.visibility=View.VISIBLE
            }
        }

        apply.setOnClickListener {
            if (Appconstands.net_status(activity)) {
                if (entercode.text.toString().trim().isNotEmpty()){
                    Apply(entercode.text.toString().trim())
                }
            }else{
                //connection.visibility=View.VISIBLE
                Toast.makeText(activity,"No Internet",Toast.LENGTH_LONG).show()
            }
        }
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

    fun getCoupon(){
        connection.visibility=View.GONE
        store_shimmer.visibility = View.VISIBLE
        store_shimmer.startShimmerAnimation()
        Couponslist.clear()
        //Appconstands.loading_show(activity, pDialog).show()
        val call = ApproveUtils.Get.Coupon("1",utils.zoneid())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag responce", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val ad = response.body() as Resp
                        for (i in 0 until ad.response!!.size) {
                            val id = if (ad.response!![i].id.isNullOrEmpty()) "" else ad.response!![i].id.toString()
                            println("cid : "+id)
                            val name = if (ad.response!![i].name.isNullOrEmpty()) "" else ad.response!![i].name.toString()
                            val code = if (ad.response!![i].code.isNullOrEmpty()) "" else ad.response!![i].code.toString()
                            val type = if (ad.response!![i].type.isNullOrEmpty()) "" else ad.response!![i].type.toString()
                            val discount = if (ad.response!![i].discount.isNullOrEmpty()) "" else ad.response!![i].discount.toString()
                            val minimum_order = if (ad.response!![i].minimum_order.isNullOrEmpty()) "" else ad.response!![i].minimum_order.toString()
                            val data = Coupons()
                            data.id = id
                            data.dis = discount
                            data.use = name
                            data.small_dis = code
                            data.minimum_order = minimum_order
                            data.brief = arrayListOf("$minimum_order")
                            Couponslist.add(data)
                            adp.notifyDataSetChanged()
                        }
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
                }else if(t.toString().contains("Unable to resolve host")){
                    connection.visibility=View.VISIBLE
                }
                //pDialog.dismiss()
                //loading_show(activity).dismiss()
                store_shimmer.visibility = View.GONE
                store_shimmer.stopShimmerAnimation()
            }
        })
    }

    fun Apply(src:String){
        pDialog = Dialog(activity)
        Appconstands.loading_show(activity,pDialog).show()
        val call = ApproveUtils.Get.Coupon_check(src,utils.zoneid())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag responce", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val ad = response.body() as Resp
                        //for (i in 0 until ad.response!!.size) {
                        val i =0
                            val id = if (ad.response!![i].id.isNullOrEmpty()) "" else ad.response!![i].id.toString()
                            println("cid : "+id)
                            val name = if (ad.response!![i].name.isNullOrEmpty()) "" else ad.response!![i].name.toString()
                            val code = if (ad.response!![i].code.isNullOrEmpty()) "" else ad.response!![i].code.toString()
                            val type = if (ad.response!![i].type.isNullOrEmpty()) "" else ad.response!![i].type.toString()
                            val discount = if (ad.response!![i].discount.isNullOrEmpty()) "" else ad.response!![i].discount.toString()
                            val minimum_order = if (ad.response!![i].minimum_order.isNullOrEmpty()) "" else ad.response!![i].minimum_order.toString()
                            val data = Coupons()
                            data.id = id
                            data.dis = discount
                            data.use = name
                            data.small_dis = code
                            data.minimum_order = minimum_order
                            data.brief = arrayListOf("$minimum_order")
                            //Couponslist.add(data)
                            //adp.notifyDataSetChanged()
                        val its = Intent()
                        its.putExtra("id",id)
                        its.putExtra("dis",discount)
                        its.putExtra("use",name)
                        its.putExtra("small_dis",code)
                        its.putExtra("minimum_order",minimum_order)
                        setResult(Activity.RESULT_OK,its)
                        finish()
                        //}
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
                //store_shimmer.visibility = View.GONE
                //store_shimmer.stopShimmerAnimation()
            }

            override fun onFailure(call: Call<Resp>, t: Throwable) {
                Log.e("$tag Fail response", t.toString())
                if (t.toString().contains("time")) {
                    Toast.makeText(
                        activity,
                        "Poor network connection",
                        Toast.LENGTH_LONG
                    ).show()
                }else if(t.toString().contains("Unable to resolve host")){
                    connection.visibility=View.VISIBLE
                }
                pDialog.dismiss()
                //loading_show(activity).dismiss()
                //store_shimmer.visibility = View.GONE
                //store_shimmer.stopShimmerAnimation()
            }
        })
    }
}
/*{
                "id": 1,
                "name": "OPEN Discount",
                "code": "FIRST101",
                "type": "Discount",
                "discount": "20",
                "minimum_order": "100"
            }
*/

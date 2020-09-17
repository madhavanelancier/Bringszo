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
import com.elanciers.bringszo.Adapters.OrderListAdapter
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.OrderDetail
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.connection_error.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderActivity : AppCompatActivity(),OrderListAdapter.OnItemClickListener,OrderListAdapter.OnBottomReachedListener {
    override fun OnItemClick(view: View, position: Int, viewType: Int) {
        if (Oders[position].deliver_sts!="Declined") {
            val its = Intent(this, OrderDetailActivity::class.java)
            its.putExtra("id", Oders[position].id.toString())
            its.putExtra("dateandtime", Oders[position].dateandtime.toString())
            its.putExtra("deliver_sts", Oders[position].deliver_sts.toString())
            its.putExtra("total", Oders[position].price.toString())
            its.putExtra("subtotal", Oders[position].subtotal.toString())
            its.putExtra("delivery", Oders[position].delivery.toString())
            its.putExtra("discount", Oders[position].discount.toString())
            its.putExtra("details", Oders[position].details.toString())
            its.putExtra("order_details", Oders[position].order_details.toString())
            its.putExtra("shopnm", Oders[position].shopnm)
            its.putExtra("shoploc", Oders[position].shoploc)
            its.putExtra("adrs_type", Oders[position].adrs_type)
            its.putExtra("coupon_code", Oders[position].coupon_code)
            its.putExtra("adrs", Oders[position].adrs)
            its.putExtra("payment_mode", Oders[position].payment_mode)
            startActivity(its)
        }
    }

    override fun onBottomReached(position: Int) {
        if (Appconstands.net_status(activity)) {
            connection.visibility=View.GONE
            if (Oders.size>14) {
                start = start + Oders.size
                getOrders()
            }
        }else{
            connection.visibility=View.VISIBLE
        }
    }
    val tag = "Orders"
    val activity = this
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog

    var start = 0
    var Oders = ArrayList<OrderDetail>()
    lateinit var adp :OrderListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appconstands.changeStatusBarColor(this, R.color.statusbar)
        setContentView(R.layout.activity_order)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar);
        val ab = supportActionBar
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px)
        ab!!.title = "My Orders"
        utils = Utils(activity)
        db = DBController(activity)
        pDialog = Dialog(activity)

        var data = OrderDetail()
        data.name="Departmental Store"
        data.loc="Anna Nagar"
        data.price="180"
        data.items="Thuvaram Paruppu 500 gms x 1, Coconut Oil 500 ml x 1"
        data.dateandtime="Oct 04, 12.45 PM"
        //Oders.add(data)
        data = OrderDetail()
        data.name="Departmental Store"
        data.loc="Anna Nagar"
        data.price="180"
        data.items="Thuvaram Paruppu 500 gms x 1, Coconut Oil 500 ml x 1"
        data.dateandtime="Oct 04, 12.45 PM"
        //Oders.add(data)

        adp = OrderListAdapter(activity,Oders,activity,activity)
        orderlist.adapter = adp



        conbtn.setOnClickListener {
            if (Appconstands.net_status(activity)) {
                connection.visibility=View.GONE
                //start = start + Oders.size
                getOrders()
            }else{
                connection.visibility=View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Appconstands.net_status(activity)) {
            connection.visibility=View.GONE
            //start = start + Oders.size
            getOrders()
        }else{
            connection.visibility=View.VISIBLE
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

    fun getOrders(){
        /*val pDialo = ProgressDialog(this);
        pDialo.setMessage("Loading....");
        pDialo.setIndeterminate(false);
        pDialo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialo.setCancelable(false);
        pDialo.show()*/
        println("utils.userid() : "+utils.userid())
        if (start==0) {
            pDialog= Dialog(activity)
            Appconstands.loading_show(activity, pDialog).show()
        }
        val call = ApproveUtils.Get.Orders(utils.userid(),start.toString(),utils.zoneid())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag responce", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val ad = response.body() as Resp
                        for (z in 0 until ad.response!!.size) {
                            val id = if (ad.response!![z].id.isNullOrEmpty()) "" else ad.response!![z].id.toString()
                            val vid = if (ad.response!![z].vid.isNullOrEmpty()) "" else ad.response!![z].vid.toString()
                            val location = if (ad.response!![z].location.isNullOrEmpty()) "" else ad.response!![z].location.toString()
                            val subtotal = if (ad.response!![z].subtotal.isNullOrEmpty()) "" else ad.response!![z].subtotal.toString()
                            val discount = if (ad.response!![z].discount.isNullOrEmpty()) "" else ad.response!![z].discount.toString()
                            val delivery = if (ad.response!![z].delivery.isNullOrEmpty()) "" else ad.response!![z].delivery.toString()
                            val ven_address = if (ad.response!![z].ven_address.isNullOrEmpty()) "" else ad.response!![z].ven_address.toString()
                            val user_address = if (ad.response!![z].user_address.isNullOrEmpty()) "" else ad.response!![z].user_address.toString()
                            val order_status = if (ad.response!![z].order_status.isNullOrEmpty()) "" else ad.response!![z].order_status.toString()
                            val user_type = if (ad.response!![z].user_type.isNullOrEmpty()) "" else ad.response!![z].user_type.toString()
                            val datetime = if (ad.response!![z].datetime.isNullOrEmpty()) "" else ad.response!![z].datetime.toString()
                            val coupon_code = if (ad.response!![z].coupon_code.isNullOrEmpty()) "" else ad.response!![z].coupon_code.toString()
                            val total = if (ad.response!![z].total.isNullOrEmpty()) "" else ad.response!![z].total.toString()
                            val payment_mode = if (ad.response!![z].payment_mode.isNullOrEmpty()) "" else ad.response!![z].payment_mode.toString()
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
                            Oders.add(data)
                            adp.notifyDataSetChanged()
                            adps()
                        }
                        if(Oders.isEmpty()){
                            imageView31.visibility=View.VISIBLE
                            textView36.visibility=View.VISIBLE
                        }
                        else{
                            imageView31.visibility=View.GONE
                            textView36.visibility=View.GONE
                        }
                        //startActivity(Intent(activity, HomeActivity::class.java))
                    } else {
                        if(Oders.isEmpty()){
                            imageView31.visibility=View.VISIBLE
                            textView36.visibility=View.VISIBLE
                        }
                        else{
                            imageView31.visibility=View.GONE
                            textView36.visibility=View.GONE
                        }
                        Toast.makeText(
                                activity,
                                example.message,
                                Toast.LENGTH_LONG
                        ).show()
                    }

                }
                if (start==0) {
                    pDialog.dismiss()
                }
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
                adps()
                if (start==0) {
                    pDialog.dismiss()
                }
                //loading_show(activity).dismiss()
            }
        })
    }
    fun adps(){
        /*if (Oders.isEmpty()){
            empdes.setText("You don't have any Oders . so kindly add Oders that hepls you checkout faster.")
            empbtn.setText("")
            empty_layout.visibility=View.VISIBLE
        }else{
            empty_layout.visibility=View.GONE
        }*/
    }
}
/*{
    "Status": "Success",
    "Message": "",
    "Response": [
        {
            "id": 3,
            "vid": "Reliance",
            "subtotal": "90",
            "discount": null,
            "delivery": "10",
            "total": "100",
            "order_details": [
                {
                    "product": "Oil",
                    "qty": "1",
                    "mesure": "1 Kg",
                    "price": "60",
                    "total": "60"
                },
                {
                    "product": "Peas",
                    "qty": "1",
                    "mesure": "2 Kg",
                    "price": "100",
                    "total": "100"
                }
*/

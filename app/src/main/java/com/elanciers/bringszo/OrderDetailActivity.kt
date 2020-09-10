package com.elanciers.bringszo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Appconstands.rupees
import kotlinx.android.synthetic.main.activity_order_detail.*
import org.json.JSONArray
import java.text.SimpleDateFormat

class OrderDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         Appconstands.changeStatusBarColor(this, R.color.statusbar)
        setContentView(R.layout.activity_order_detail)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar);
        val ab = supportActionBar
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px)
        val id = intent.extras.get("id").toString()
        ab!!.title = "ORDER #$id"

        val details = intent.extras.get("details").toString()
        val delivery = intent.extras.get("delivery").toString()
        val discount = intent.extras.get("discount").toString()
        val total = intent.extras.get("total").toString()
        val subtotal = intent.extras.get("subtotal").toString()
        val deli_sts = intent.extras.get("deliver_sts").toString()
        val dateandtime = intent.extras.get("dateandtime").toString()
        val order_details = intent.extras.get("order_details")
        val shopn = intent.extras.get("shopnm").toString()
        val shoplo = intent.extras.get("shoploc").toString()
        val adrs_typ = intent.extras.get("adrs_type").toString()
        val adr = intent.extras.get("adrs").toString()
        val coupon_code = intent.extras.get("coupon_code").toString()
        val payment_mode = intent.extras.get("payment_mode").toString()
        if (deli_sts=="Delivered"){
            deliver_sts.visibility= View.VISIBLE
            track.visibility= View.GONE
        }else{
           /* deliver_sts.visibility= View.GONE
            track.visibility= View.VISIBLE*/
        }
        if (discount!="0.0"){
            dis_lay.visibility=View.VISIBLE
        }else{
            dis_lay.visibility=View.GONE
        }

        cp_code.setText("Discount Applied ($coupon_code)")
        if (dateandtime.isNotEmpty()){
            val form = SimpleDateFormat("MMM dd, h.mm a").format(dateandtime.toLong()*1000)
            deliver_sts.setText("Order "+deli_sts+" "+form)
        }
        total_tot.setText(rupees+subtotal)
        total_amnt.setText(rupees+total)
        del.setText(rupees+delivery)
        dis.setText(rupees+discount)
        paid_sts.setText("Paid via $payment_mode")


            if (adrs_typ=="Work"){
                imageView12.setImageResource(R.drawable.ic_suitcase)
            }else  if (adrs_typ=="Home"){
                imageView12.setImageResource(R.drawable.ic_home)
            }else{
                imageView12.setImageResource(R.drawable.ic_location)
            }

        shopnm.setText("Delivery Address : ")
        shoploc.setText(adrs_typ+" - "+adr)
        adrs_type.setText(adrs_typ)
        adrs.setText(adr)

        val srr = ArrayList<String>()
        val j = JSONArray(details)
        for (h in 0 until j.length())
        {
            val obj = j.getJSONObject(h)
            val product = if (obj.getString("product").isNullOrEmpty()) "" else obj.getString("product").toString()
            val qty = if (obj.getString("qty").isNullOrEmpty()) "" else obj.getString("qty").toString()
            val mesure = if (obj.getString("mesure").isNullOrEmpty()) "" else obj.getString("mesure").toString()
            val price = if (obj.getString("price").isNullOrEmpty()) "" else obj.getString("price").toString()
            val tota = if (obj.getString("total").isNullOrEmpty()) "" else obj.getString("total").toString()
            val itm = "" + product + " " + mesure + " x " + qty+" = "+tota
            srr.add(itm)
        }
        items.adapter = ArrayAdapter<String>(this,R.layout.order_item,R.id.itm,srr)
        items.isExpanded=true
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

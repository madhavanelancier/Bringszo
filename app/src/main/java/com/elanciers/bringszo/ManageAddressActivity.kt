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
import com.elanciers.bringszo.Adapters.ManageAddressAdapter
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Appconstands.AddAddressCode
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.AddressData
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_manage_address.*
import kotlinx.android.synthetic.main.activity_manage_address.store_shimmer
import kotlinx.android.synthetic.main.common_empty_list.*
import kotlinx.android.synthetic.main.connection_error.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageAddressActivity : AppCompatActivity(),ManageAddressAdapter.OnBottomReachedListener,ManageAddressAdapter.OnItemClickListener {
    override fun OnItemClick(view: View, position: Int, viewType: Int) {

    }

    override fun onBottomReached(position: Int) {

    }
    val tag = "MngAdrs"
    val activity = this
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog

    var adrs = ArrayList<AddressData>()
    lateinit var adp : ManageAddressAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appconstands.changeStatusBarColor(this, R.color.statusbar)
        setContentView(R.layout.activity_manage_address)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar);
        val ab = supportActionBar
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px)
        ab!!.title = "Manage Addresses"
        utils = Utils(activity)
        db = DBController(activity)
        pDialog = Dialog(activity)
        adp = ManageAddressAdapter(activity,adrs,activity,activity)
        address_list.adapter = adp
        /*var data = Addresses()
        data.id = "1"
        data.title = "Work"
        data.adrs = "Company Name goes here, 2nd street, Chinna Chokkikulam, Madurai, Tamil Nadu 625002, India"
        data.dis = ""
        data.type = "Work"
        adrs.add(data)
        data = Addresses()
        data.id = "1"
        data.title = "Home"
        data.adrs = "Company Name goes here, 2nd street, Chinna Chokkikulam, Madurai, Tamil Nadu 625002, India"
        data.dis = ""
        data.type = "Home"
        adrs.add(data)*/
        adrs = db.AddressList()
        adp = ManageAddressAdapter(activity,adrs,activity,activity)
        address_list.adapter = adp
        adp.notifyDataSetChanged()
        println("size : "+adrs.size)
        if (adrs.isEmpty()){
            //getAddress()
            empty_layout.visibility = View.VISIBLE
        }else {
            empty_layout.visibility = View.GONE
        }



        add_new_adrs.setOnClickListener {
            val its = Intent(this, AddressMapActivity::class.java)
            its.putExtra("id","")
            startActivityForResult(its,AddAddressCode)
        }

        empbtn.setOnClickListener {
            val its = Intent(this, AddressMapActivity::class.java)
            its.putExtra("id","")
            startActivityForResult(its,AddAddressCode)
        }
        //getAddress()
        //CheckSigninOtp("9894940560","123456","123456")
        conbtn.setOnClickListener {
            adrs = db.AddressList()
            address_list.adapter = adp
            if (Appconstands.net_status(activity)){
                connection.visibility=View.GONE
                if (adrs.isEmpty()){
                    //getAddress()
                    empty_layout.visibility = View.VISIBLE
                }else {
                    empty_layout.visibility = View.GONE
                }
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

    override fun onResume() {
        super.onResume()
        //adrs.clear()
        //adp.notifyDataSetChanged()
    }

    fun getAddress(){
        store_shimmer.visibility = View.VISIBLE
        store_shimmer.startShimmerAnimation()
        /*val pDialo = ProgressDialog(this);
        pDialo.setMessage("Loading....");
        pDialo.setIndeterminate(false);
        pDialo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialo.setCancelable(false);
        pDialo.show()*/
        db.dropAddress()
        adrs.clear()
        //Appconstands.loading_show(activity, pDialog).show()
        val call = ApproveUtils.Get.Otp("1",utils.mobile().toString(),"123","123","")
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag responce", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        empty_layout.visibility = View.GONE
                        val ad = response.body() as Resp
                        val userid = if (ad.response!![0].id.isNullOrEmpty())"" else ad.response!![0].id.toString()
                        val name = if (ad.response!![0].name.isNullOrEmpty())"" else ad.response!![0].name.toString()
                        val email = if (ad.response!![0].email.isNullOrEmpty())"" else ad.response!![0].email.toString()
                        val mobile = if (ad.response!![0].mobile.isNullOrEmpty())"" else ad.response!![0].mobile.toString()
                        val address = ad.response!![0].address
                        /*utils.setUser(userid,name, mobile, email)
                        utils.setLogin(true)*/
                        println("userid : "+userid)
                        for (ads in 0 until address!!.size){
                            val adrs_id = if (address[ads].id.isNullOrEmpty())"" else address[ads].id.toString()
                            println("adrs_id : "+adrs_id)
                            val type = if (address[ads].type.isNullOrEmpty())"" else address[ads].type.toString()
                            val title = if (address[ads].title.isNullOrEmpty())"" else address[ads].title.toString()
                            val flat_no = if (address[ads].flat_no.isNullOrEmpty())"" else address[ads].flat_no.toString()
                            val area = if (address[ads].area.isNullOrEmpty())"" else address[ads].area.toString()
                            val location = if (address[ads].location.isNullOrEmpty())"" else address[ads].location.toString()
                            val lat = if (address[ads].lat.isNullOrEmpty())"" else address[ads].lat.toString()
                            val lng = if (address[ads].lng.isNullOrEmpty())"" else address[ads].lng.toString()
                            val rs = AddressData()
                            rs.adrs_id=adrs_id
                            rs.adrs_type=type
                            rs.adrs_title=title
                            rs.address=location
                            rs.adrs_house=flat_no
                            rs.adrs_landmark=area
                            rs.adrs_latitude=lat
                            rs.adrs_longtitude=lng
                            db.AddressInsert(rs)
                            adrs.add(rs)
                            adp = ManageAddressAdapter(activity,adrs,activity,activity)
                            address_list.adapter = adp
                            adp.notifyDataSetChanged()
                        }

                        //startActivity(Intent(activity, HomeActivity::class.java))
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
                }
                store_shimmer.visibility = View.GONE
                store_shimmer.stopShimmerAnimation()
                //pDialog.dismiss()
                //loading_show(activity).dismiss()
            }
        })
    }

    fun adps(){
        adrs = db.AddressList()
        adp = ManageAddressAdapter(activity,adrs,activity,activity)
        address_list.adapter = adp
        adp.notifyDataSetChanged()
        if (Appconstands.net_status(activity)) {
            if (adrs.isEmpty()) {
                empty_layout.visibility = View.VISIBLE
            } else {
                empty_layout.visibility = View.GONE
            }
        }else{
            connection.visibility=View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            Activity.RESULT_OK ->{
                when(requestCode){
                    AddAddressCode ->{
                        println("Address Added")
                        getAddress()
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                when(requestCode){
                    AddAddressCode ->{
                        println("Address Canceled")
                    }
                }
            }
        }
    }
}

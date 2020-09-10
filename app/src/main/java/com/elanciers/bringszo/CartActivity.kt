package com.elanciers.bringszo

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.elanciers.booking.DataClass.SpinnerPojo
import com.elanciers.bringszo.Adapters.AddressAdapter
import com.elanciers.bringszo.Adapters.CartAdapter
import com.elanciers.bringszo.Common.*
import com.elanciers.bringszo.Common.Appconstands.AddAddressCode
import com.elanciers.bringszo.Common.Appconstands.AddCoupon
import com.elanciers.bringszo.Common.Appconstands.changeStatusBarColor
import com.elanciers.bringszo.Common.Appconstands.net_status
import com.elanciers.bringszo.Common.Appconstands.rupees
import com.elanciers.bringszo.DataClass.*
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.connection_error.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CartActivity : AppCompatActivity() , PaymentStatusListener {
    val tag = "Cart"
    val activity = this
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog
    lateinit var deli_add : AddressData

    var order_id =""
    var catidlist=ArrayList<String>()

    var vendor_id =""
    var vendor_nm =""
    var vendor_loc =""
    var vendor_img =""
    var vendor_mail =""
    var vendor_mobile =""

    var cid = ""
    var cdis = ""
    var cuse = ""
    var csmall_dis = ""
    var cminimum_order = ""
    var discount = 0.toDouble()
    var delivery = 0.00

    var profail = false

    var cartlist = ArrayList<CartProduct>()
    lateinit var adp : CartAdapter

    var seladdress=AddressData()
    override fun onTransactionSubmitted() {
        toast("Transaction processing...")

    }

    override fun onTransactionCancelled() {
        toast("Transaction cancelled")

    }
    var tid="";

    override fun onTransactionSuccess() {

        /*val update = Dialog(this@CartActivity)
        update.requestWindowFeature(Window.FEATURE_NO_TITLE)
        update.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val v = layoutInflater.inflate(R.layout.success_popup1, null)
        val updatebut = v.findViewById<View>(R.id.yes) as TextView
        update.setContentView(v)
        update.setCancelable(false)
        update.show()*/


        //titlename.text = "New Version 1.$Number"

        /*updatebut.setOnClickListener {
            //clickupdate = "true"
            update.dismiss()

        }*/

        Handler().postDelayed(Runnable {
            val payment = OrderSend()
            payment.execute(tid,"upi", "success")
        },2000)





    }

    override fun onAppNotFound() {
        toast("UPI App Not Found.")
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails?) {
        //toast("Transaction completed...")
        // toast(transactionDetails!!.status!!)

    }

    override fun onTransactionFailed() {
        toast("Transaction failed...")

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_cart)
        changeStatusBarColor(this,android.R.color.transparent)
        utils = Utils(activity)
        db = DBController(activity)
        pDialog = Dialog(activity)

        val options = ArrayList<SpinnerPojo>()

        var sdata = SpinnerPojo()
        sdata.name = "500 Gms"
        sdata.price = "70"
        options.add(sdata)
        sdata = SpinnerPojo()
        sdata.name = "1 Kg"
        sdata.price = "140"
        options.add(sdata)

        var data = CartItems()
        data.id = ""
        data.name = "Thuvaram Paruppu"
        data.qty = "1"
        data.price = "70"
        data.img = ""
        data.options = options
        //cartlist.add(data)
        data = CartItems()
        data.id = ""
        data.name = "Coconut Oil - 500 ml"
        data.qty = "1"
        data.price = "80"
        data.img = ""
        data.options = ArrayList<SpinnerPojo>()
        //cartlist.add(data)

        cart_list.isExpanded=true
        adp = CartAdapter(this,R.layout.cart_item_adapter,cartlist)
        cart_list.adapter=adp
        cart_list.isExpanded=true
        cartscroll.isNestedScrollingEnabled=false

        apply_coupon.setOnClickListener {
            val its = Intent(this, CouponActivity::class.java)
            startActivityForResult(its,AddCoupon)
        }

        couponcan.setOnClickListener {
            cid = ""
            cdis = ""
            cuse = ""
            csmall_dis = ""
            cminimum_order = ""
            apl.visibility=View.GONE
            discount =0.toDouble()
            dis_lay.visibility = View.GONE
            discnt.setText("")
            applied.setText("Apply Coupon")
            couponcan.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_forward_ios_24px))
            tot()
        }

        vendor_id = utils.getvendor_id()
        addres()


        topaydel.setOnClickListener {
            try {
                val update = Dialog(this@CartActivity)
                update.requestWindowFeature(Window.FEATURE_NO_TITLE)
                update.window.setBackgroundDrawable(
                    ColorDrawable(Color.WHITE)
                )
                val vs =
                    layoutInflater.inflate(R.layout.app_image_dialog, null)
                val updatebut =
                    vs.findViewById<View>(R.id.update) as TextView
                val laterbut = vs.findViewById<View>(R.id.img) as ImageView
                val titlename =
                    vs.findViewById<View>(R.id.titlename) as TextView
                //download = vs.findViewById<View>(R.id.download) as TextView
                update.setContentView(vs)
                val window = update.window
                window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                update.setCancelable(true)
                update.show()

                Glide.with(this).load("https://www.bringszo.com/bring/public/images/delivery.png").placeholder(R.mipmap.bringssplash).into(laterbut)


                updatebut.setOnClickListener { // clickupdate="true";
                    update.dismiss()
                }
                /*laterbut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            update.dismiss();
                        }
                    });*/
            } catch (e: java.lang.Exception) {
                //logger.info("PerformVersionTask error" + e.getMessage());
            }
        }

        conbtn.setOnClickListener {
            if (Appconstands.net_status(activity)) {
                connection.visibility=View.GONE
                cartlist.clear()
                adp = CartAdapter(this, R.layout.cart_item_adapter, cartlist)
                cart_list.adapter = adp
                cartlist = db.CartList()
                pDialog = Dialog(activity)
                Appconstands.loading_show(activity,pDialog).show()
                for (h in 0 until cartlist.size) {
                    vendor_id = cartlist[h].vendor_id.toString()
                    vendor_nm = cartlist[h].vendor_nm.toString()
                    vendor_img = cartlist[h].img.toString()
                    if (!profail) {
                        getProducts(
                            cartlist[h].pid.toString(),
                            cartlist[h].opid.toString().toInt(),
                            h,
                            cartlist[h].qty.toString()
                        )
                    }else{
                        break
                    }
                }
                productFail()
                tot()
            }else{
                connection.visibility=View.VISIBLE
            }
        }
        set_cng.setOnClickListener {
            selectAddress(it)
        }
    }

    fun CloseAct(view: View){
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (Appconstands.net_status(activity)) {
            connection.visibility=View.GONE
            cartlist.clear()
            adp = CartAdapter(this, R.layout.cart_item_adapter, cartlist)
            cart_list.adapter = adp
            cartlist = db.CartList()
           /* pDialog = Dialog(activity)
            Appconstands.loading_show(activity,pDialog).show()*/
            for (h in 0 until cartlist.size) {
                vendor_id = cartlist[h].vendor_id.toString()
                vendor_nm = cartlist[h].vendor_nm.toString()
                vendor_img = cartlist[h].img.toString()
                if (!profail) {
                    getProducts(cartlist[h].pid.toString(), cartlist[h].opid.toString().toInt(), h, cartlist[h].qty.toString())
                }else{
                    break
                }
            }
            productFail()
            tot()
        }else{
            connection.visibility=View.VISIBLE
        }
    }
    fun productFail(){
        if (profail){
            val alert = AlertDialog.Builder(activity)
            alert.setTitle("UnAvailable")
            alert.setCancelable(false)
            alert.setMessage("Your cart contains items from ${db.vendor_nm}. but it's currently UnAvailable, try to add item after sometime")
            alert.setPositiveButton("ok", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                    db.dropHoleCart()
                    finish()
                }

            })
            alert.show()
        }
    }

    fun getProducts(pid:String,opid : Int,posi:Int,qty:String){
        println("pid : "+pid)
        profail = false
        val call = ApproveUtils.Get.Product(pid)
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val slid = response.body() as Resp
                        val pro = slid.response!!
                        val pname = pro[0].pname
                        val cid = pro[0].cid
                        val image = pro[0].image
                        val ven_id = pro[0].vendor_id!!.toString()
                        val vimage = pro[0].vimage!!.toString()
                        val mail = if (pro[0].mail.isNullOrEmpty()) "" else pro[0].mail!!.toString()
                        val mobile = if (pro[0].mobile.isNullOrEmpty()) "" else pro[0].mobile!!.toString()
                        val name = pro[0].name!!.toString()
                        val location = pro[0].location!!.toString()
                        vendor_id = ven_id
                        vendor_nm = name
                        vendor_loc = location
                        vendor_img = vimage
                        vendor_mail = mail
                        vendor_mobile = mobile
                        store()
                        val option = pro[0].option
                        val options = ArrayList<SpinnerPojo>()
                        val dts = TitledProduct()
                        val cartpro = CartProduct()
                        for (o in 0 until option!!.size){
                            val sdata = SpinnerPojo()
                            val price = option[o].price
                            val opname = option[o].name
                            sdata.id = o.toString()
                            sdata.name = opname
                            sdata.price = price
                            options.add(sdata)
                            println("opid : "+opid +" o : "+o)
                            if (opid == o){
                                dts.price = price
                                dts.selpos =opid
                                dts.nm = opname
                                cartpro.opid = opid.toString()
                                cartpro.opnm = opname
                                cartpro.price = price

                                cartlist[posi].opid = opid.toString()
                                cartlist[posi].opnm = opname
                                cartlist[posi].price = price
                            }
                        }
                        /*dts.pid = pid
                        dts.name = pname
                        dts.qty = qty
                        dts.img =image
                        dts.options = options
                        cartlist.add(dts)*/
                        println("options : "+options)
                        cartlist[posi].pid = pid
                        cartlist[posi].pnm = pname
                        cartlist[posi].qty = qty
                        cartlist[posi].options = options
                        catidlist.add(cid.toString())
                        db.CartUp(cartlist[posi])
                        adp = CartAdapter(activity,R.layout.cart_item_adapter,cartlist)
                        cart_list.adapter=adp
                    }else{
                        profail = true
                        pDialog.dismiss()
                    }
                }

                if (posi==cartlist.size-1){
                    pDialog.dismiss()
                }
                pDialog.dismiss()
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
                //product_layout.visibility = View.VISIBLE
                //product_shimmer.visibility = View.GONE
                //product_shimmer.stopShimmerAnimation()
                pDialog.dismiss()
            }
        })

    }

    fun tot(){
        val tot = db.cartTotal.toString()
        println("db.cartTotal : "+db.cartTotal)
        store()
        //cartlist = db.CartList()
        /*adp = CartAdapter(this,R.layout.cart_item_adapter,cartlist)
        cart_list.adapter=adp*/
        if (cartlist.isEmpty())
        {
            finish()
        }
        if (db.cartTotal!=0.toDouble()) {
            itmtot.setText(rupees+tot.toString())
            val items = db.cartItem.toString()
            val tp =  (db.cartTotal-discount) + delivery
            topay.setText(rupees+tp.toString())
            view_tot.setText(rupees+tp.toString())
            /*cart_layout.visibility = View.VISIBLE
            tot_items.setText(items+" items - ")
            tot_price.setText("₹ "+tot)
            shop.setText("From : "+vendor_nm)
            continue_cart.setOnClickListener {
                startActivity(Intent(activity, CartActivity::class.java))
            }*/
        }else{
            //cart_layout.visibility = View.GONE
        }
        pDialog.dismiss()
        /*val res = (db.cartTotal / 100.0f) * 10;
        println("res : "+res)*/

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println()
        when(resultCode){
            Activity.RESULT_OK ->{
                when(requestCode){
                    AddCoupon ->{
                        println("AddCoupon Added")
                        cid = data!!.extras.getString("id").toString()
                        cdis = data!!.extras.getString("dis").toString()
                        cuse = data!!.extras.getString("use").toString()
                        csmall_dis = data!!.extras.getString("small_dis").toString()
                        cminimum_order = data!!.extras.getString("minimum_order").toString()
                        if (cminimum_order.isNotEmpty()) {
                            if (db.cartTotal > cminimum_order.toString().toDouble()) {
                                val alert = AlertDialog.Builder(activity)
                                val v = layoutInflater.inflate(R.layout.discount_popup,null)
                                alert.setView(v)
                                alert.setCancelable(true)
                                val po = alert.create()
                                val yay = v.findViewById<TextView>(R.id.yay) as TextView
                                val code = v.findViewById<TextView>(R.id.code) as TextView
                                val discn = v.findViewById<TextView>(R.id.discnt) as TextView
                                val dis_disc = v.findViewById<TextView>(R.id.dis_disc) as TextView
                                code.setText("'"+csmall_dis+"' applied")
                                apl.visibility=View.VISIBLE
                                applied.setText("$csmall_dis")
                                dis_lay.visibility = View.VISIBLE
                                val t = (db.cartTotal / 100.0f) * cdis.toString().toInt();
                                //discnt.setText(""+discount)
                                val disc =String.format("%.2f",t)
                                discount = disc.toDouble()
                                dis_disc.setText("You have availed a total discount of Rs "+disc)
                                discn.setText(rupees+discount.toString())
                                discnt.setText(rupees+discount.toString())
                                couponcan.setImageDrawable(resources.getDrawable(R.drawable.ic_dialog_close_light))
                                yay.setOnClickListener {
                                    po.dismiss()
                                }
                                po.show()
                            }else{
                                cid = ""
                                cdis = ""
                                cuse = ""
                                csmall_dis = ""
                                cminimum_order = ""
                                apl.visibility=View.GONE
                                discount =0.toDouble()
                                dis_lay.visibility = View.GONE
                                discnt.setText("")
                                applied.setText("Apply Coupon")
                                couponcan.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_forward_ios_24px))
                                //Toast.makeText(activity,"")
                            }
                        }else{
                            cid = ""
                            cdis = ""
                            cuse = ""
                            csmall_dis = ""
                            cminimum_order = ""
                            apl.visibility=View.GONE
                            discount =0.toDouble()
                            dis_lay.visibility = View.GONE
                            discnt.setText("")
                            applied.setText("Apply Coupon")
                            couponcan.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_forward_ios_24px))
                        }
                        tot()
                    }

                    AddAddressCode -> {
                        println("AddAddress Added")
                        println("adrs_id"+data!!.extras.getString("adrs_id"))
                        val adrs = AddressData()
                        adrs.adrs_id=data!!.extras.getString("adrs_id")
                        adrs.adrs_type=data.extras.getString("adrs_type")
                        adrs.adrs_title=data.extras.getString("adrs_title")
                        adrs.address=data.extras.getString("address")
                        adrs.adrs_house=data.extras.getString("adrs_house")
                        adrs.adrs_landmark=data.extras.getString("adrs_landmark")
                        adrs.adrs_latitude=data.extras.getString("adrs_latitude")
                        adrs.adrs_longtitude=data.extras.getString("adrs_longtitude")
                        pDialog= Dialog(activity)
                        Appconstands.loading_show(activity,pDialog).show()
                        setAddress(adrs)
                        seladdress=adrs

                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                when(requestCode){
                    AddCoupon ->{
                        println("AddCoupon Canceled")
                    }
                    AddAddressCode -> {
                        println("AddAddress Canceled")
                    }
                }
            }
        }
    }

    fun dis(){
        if (cminimum_order.isNotEmpty()) {
            if (db.cartTotal > cminimum_order.toString().toDouble()) {
                val alert = AlertDialog.Builder(activity)
                val v = layoutInflater.inflate(R.layout.discount_popup,null)
                alert.setView(v)
                alert.setCancelable(true)
                val po = alert.create()
                val yay = v.findViewById<TextView>(R.id.yay) as TextView
                val code = v.findViewById<TextView>(R.id.code) as TextView
                val discn = v.findViewById<TextView>(R.id.discnt) as TextView
                val dis_disc = v.findViewById<TextView>(R.id.dis_disc) as TextView
                code.setText("'"+csmall_dis+"' applied")
                apl.visibility=View.VISIBLE
                applied.setText("$csmall_dis")
                dis_lay.visibility = View.VISIBLE
                discount = (db.cartTotal / 100.0f) * cdis.toString().toInt();
                //discnt.setText(""+discount)
                dis_disc.setText("You have availed a total discount of Rs "+discount)
                discn.setText(rupees+discount.toString())
                discnt.setText(rupees+discount.toString())
                couponcan.setImageDrawable(resources.getDrawable(R.drawable.ic_dialog_close_light))
                yay.setOnClickListener {
                    po.dismiss()
                }
                po.show()
                /*apl.visibility=View.VISIBLE
                applied.setText("$cuse")
                dis_lay.visibility = View.VISIBLE
                discount = (db.cartTotal / 100.0f) * cdis.toString().toInt();
                discnt.setText(rupees+discount.toString())
                couponcan.setImageDrawable(resources.getDrawable(R.drawable.ic_dialog_close_light))*/
            }else{
                cid = ""
                cdis = ""
                cuse = ""
                csmall_dis = ""
                cminimum_order = ""
                apl.visibility=View.GONE
                discount =0.toDouble()
                dis_lay.visibility = View.GONE
                discnt.setText("")
                applied.setText("Apply Coupon")
                couponcan.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_forward_ios_24px))
            }
        }else{
            cid = ""
            cdis = ""
            cuse = ""
            csmall_dis = ""
            cminimum_order = ""
            apl.visibility=View.GONE
            applied.setText("Apply Coupon")
            couponcan.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_forward_ios_24px))
        }
    }
    fun selectAddress(view: View){
        val openwith = BottomSheetDialog(activity)
        openwith.setOnDismissListener {
            println("dismiss")
        }
        openwith.requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        val popUpView = activity.layoutInflater.inflate(R.layout.select_address_popup, null)
        val addresslist = popUpView.findViewById(R.id.addresslist) as ExpandableHeightGridView
        val add_new_adrs = popUpView.findViewById(R.id.add_new_adrs) as TextView
        val srd = db.AddressList()
        /*val animMoveToTop = AnimationUtils.loadAnimation(activity, R.anim.bottom_top)
        popUpView.animation =animMoveToTop*/
        addresslist.adapter = AddressAdapter(activity,R.layout.address_item_adapter,srd)
        addresslist.isExpanded=true
        addresslist.setOnItemClickListener { adapterView, vi, i, l ->
            openwith.dismiss()
            val adrs = srd[i]
            seladdress=adrs
            pDialog= Dialog(activity)
            Appconstands.loading_show(activity,pDialog).show()
            setAddress(adrs)
        }
        add_new_adrs.setOnClickListener {
            openwith.dismiss()
            AddAddress(it)
        }

        openwith.setContentView(popUpView);
        openwith.setCancelable(true)
        openwith.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        openwith.window!!.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        openwith.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val displaymetrics = DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        val width =  (displaymetrics.widthPixels * 1);
        val height =  (displaymetrics.heightPixels * 1);
        openwith.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        openwith.show()
    }

    fun AddAddress(view: View){
        val its = Intent(activity,AddressMapActivity::class.java)
        its.putExtra("id","")
        startActivityForResult(its, AddAddressCode)
    }

    //set address
    fun setAddress(adr : AddressData){
        //"delivery_charge"
        println("adresss_id_value"+adr.adrs_id)
        getDelivery_charge(adr)

        //addres()
    }
    //delivery charge
    fun getDelivery_charge(adr : AddressData){

        println("utils.userid() : "+utils.userid())
        println("adr.adrs_id.toString() : "+adr.adrs_id.toString())
        println("vendor_id : "+vendor_id)
        deli_add = adr
        val call = ApproveUtils.Get.getDeliverycharge(
            db.cartTotal.toString())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        deliveryfee.setText(rupees+example.message!!.toDouble())
                        delivery = example.message!!.toDouble()
                        deli_add = adr
                        tot()
                        val set_img = findViewById<ImageView>(R.id.img) as ImageView
                        if (adr.adrs_type=="Work") {
                            set_img.setImageDrawable(resources.getDrawable(R.drawable.ic_suitcase))
                        }else if (adr.adrs_type == "Home"){
                            set_img.setImageDrawable(resources.getDrawable(R.drawable.ic_home))
                        }else{
                            set_img.setImageDrawable(resources.getDrawable(R.drawable.ic_location))
                        }
                        deliver.setText("Deliver to "+adr.adrs_type)
                        set_address.setText(adr.adrs_landmark)

                        set_address_layout.visibility=View.VISIBLE
                        multi_layout.visibility=View.GONE
                        add_address_layout.visibility=View.GONE
                    }else{
                        pDialog.dismiss()

                    }
                }
                pDialog.dismiss()
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
            }
        })
    }

    fun getDelivery_charge_dup(adr : AddressData){
        pDialog = Dialog(activity)
        Appconstands.loading_show(activity,pDialog).show()
        println("utils.userid() : "+utils.userid())
        println("adr.adrs_id.toString() : "+adr.adrs_id.toString())
        println("vendor_id : "+vendor_id)
        deli_add = adr

        val call = ApproveUtils.Get.getDeliverycharge(
            db.cartTotal.toString())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        deliveryfee.setText(rupees+example.message!!.toDouble())
                        delivery = example.message!!.toDouble()
                        deli_add = adr
                        tot()
                        val set_img = findViewById<ImageView>(R.id.img) as ImageView
                        if (adr.adrs_type=="Work") {
                            set_img.setImageDrawable(resources.getDrawable(R.drawable.ic_suitcase))
                        }else if (adr.adrs_type == "Home"){
                            set_img.setImageDrawable(resources.getDrawable(R.drawable.ic_home))
                        }else{
                            set_img.setImageDrawable(resources.getDrawable(R.drawable.ic_location))
                        }
                        deliver.setText("Deliver to "+adr.adrs_type)
                        set_address.setText(adr.adrs_landmark)

                        set_address_layout.visibility=View.VISIBLE
                        multi_layout.visibility=View.GONE
                        add_address_layout.visibility=View.GONE


                        val total = (db.cartTotal - discount) + delivery
                        val list = JSONArray()
                        val p = db.CartList()
                        val lst = ArrayList<PovaData>()
                        for (i in 0 until p.size) {
                            val obj = JSONObject()
                            val pid = p[i].pid
                            val qty = p[i].qty
                            val mesure = p[i].opnm
                            val price = p[i].price
                            val tot = p[i].qty.toString().toInt() * p[i].price.toString().toDouble()
                            obj.put("pid", pid.toString())
                            obj.put("qty", qty.toString())
                            obj.put("mesure", mesure.toString())
                            obj.put("price", price.toString())
                            obj.put("ptotal", tot.toString())
                            list.put(obj)
                            /* val li = JsonObject()
                    li.add("pid",pid.toString())
                    li.add("qty",qty.toString())
                    li.add("mesure",mesure.toString())
                    li.add("price",price.toString())
                    li.add("ptotal",tot.toString())*/
                            val pova = PovaData()
                            pova.pid = pid.toString()
                            pova.qty = qty.toString()
                            pova.mesure = mesure.toString()
                            pova.price = price.toString()
                            pova.ptotal = tot.toString()
                            lst.add(pova)
                        }



                        println("vid : " + vendor_id)
                        println("user : " + utils.userid())
                        println("subtotal : " + db.cartTotal)
                        println("coupon_id : " + cid)
                        println("discount : " + discount)
                        println("delivery : " + delivery)
                        println("total : " + total)
                        println("details : " + list)
                        println("adrs_id : " + deli_add.adrs_id)
                        /*val jsonObject = JsonObject();
                jsonObject.addProperty("vid",vendor_id)
                jsonObject.addProperty("user",utils.userid())
                jsonObject.addProperty("subtotal",db.cartTotal)
                jsonObject.addProperty("coupon_id",cid)
                jsonObject.addProperty("discount",discount)
                jsonObject.addProperty("delivery",delivery)
                jsonObject.addProperty("total",total)
                jsonObject.addProperty("adrs_id",deli_add.adrs_id)
                val citiesArray = JsonArray();
                citiesArray.add("Dhaka");
                citiesArray.add("Örebro");
                jsonObject.add("details", list);*/
                        val jsonObject = JSONObject();
                        jsonObject.put("vid", vendor_id)
                        jsonObject.put("user", utils.userid())
                        jsonObject.put("subtotal", db.cartTotal)
                        jsonObject.put("coupon_id", cid)
                        jsonObject.put("discount", discount)
                        jsonObject.put("delivery", delivery)
                        jsonObject.put("total", total)
                        jsonObject.put("adrs_id", deli_add.adrs_id)
                        jsonObject.put("details", list)
                        //pDialog.dismiss()
                        println("jsonObject : " + jsonObject)///postRawJSON(jsonObject)//
                        /*val call = ApproveUtils.Get.Order(
                    vendor_id,
                    utils.userid(),
                    db.cartTotal.toString(),
                    cid,
                    discount.toString(),
                    delivery.toString(),
                    total.toString(),
                    deli_add.adrs_id.toString(),
                    list)
                call.enqueue(object : Callback<Resp> {
                    override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                        Log.e("$tag response", response.toString())
                        if (response.isSuccessful()) {
                            val example = response.body() as Resp
                            println(example)
                            if (example.status == "Success") {
                                db.dropHoleCart()
                                finish()
                            }
                        }
                        pDialog.dismiss()
                        //product_layout.visibility = View.VISIBLE
                        //product_shimmer.visibility = View.GONE
                        //product_shimmer.stopShimmerAnimation()
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
                        //product_layout.visibility = View.VISIBLE
                        //product_shimmer.visibility = View.GONE
                        //product_shimmer.stopShimmerAnimation()
                    }
                })*/

                        //OrderSend().execute()
                        val alert = AlertDialog.Builder(activity)
                        val v = layoutInflater.inflate(R.layout.payment_popup,null)
                        alert.setView(v)
                        alert.setCancelable(true)
                        val po = alert.create()
                        val cash = v.findViewById<TextView>(R.id.cash) as TextView
                        val razor = v.findViewById<TextView>(R.id.razor) as TextView

                        cash.setOnClickListener{
                            po.dismiss()
                            val payment = OrderSend()
                            payment.execute("","Cash", "success")
                        }
                        razor.setOnClickListener{
                            po.dismiss()
                            startpayment()
                        }
                        po.show()

                    }else{
                        pDialog.dismiss()

                    }
                }
                pDialog.dismiss()
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
            }
        })
    }

    fun addres(){
        if (db.AddressList().size==0){
            add_address_layout.visibility=View.VISIBLE
            multi_layout.visibility=View.GONE
            set_address_layout.visibility=View.GONE
        }else if (db.AddressList().size==1){
            pDialog= Dialog(activity)
            Appconstands.loading_show(activity,pDialog).show()
            setAddress(db.AddressList()[0])
            seladdress=db.AddressList()[0]

            set_address_layout.visibility=View.VISIBLE
            multi_layout.visibility=View.GONE
            add_address_layout.visibility=View.GONE
        }else{
            add_address_layout.visibility=View.GONE
            var hh=""
            for (t in db.AddressList()){
                hh=hh+t.adrs_type+","
                titles.setText(hh.removeSuffix(","))
            }
            multi_layout.visibility=View.VISIBLE
            set_address_layout.visibility=View.GONE
        }
    }

    fun detailed_bill(view: View){
        //cartscroll.smoothScrollTo(0,0)
        cartscroll.fullScroll(View.FOCUS_DOWN)
    }

    fun ProceedToPay(view: View){
        if(utils.Maintenance().isNotEmpty()){

            val alert11 = AlertDialog.Builder(activity)
            alert11.setCancelable(false)
            //alert11.setTitle("Maintenance")
            alert11.setMessage(utils.Maintenance())
            alert11.setPositiveButton(
                "OK",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()
                    }
                })
            alert11.show()
        }else {
            if (net_status(activity)) {
                getDelivery_charge_dup(seladdress)


            } else {
                Toast.makeText(activity, "No Internet", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun store(){
        storenm.setText("Total items: ")
        storeloc.setText(cartlist.size.toString()+" item(s)")
        vendor_img=utils.getvendor_img()
        if (vendor_img.isNotEmpty()){

            //Glide.with(activity).load(Appconstands.ImageDomain+vendor_img).error(R.mipmap.banner).into(imageView9)

        }else{
        }
    }

    private inner class OrderSend : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            pDialog = Dialog(activity)
            Appconstands.loading_show(activity, pDialog).show()
        }

        override fun doInBackground(vararg param: String): String? {
            var result: String = ""

            println("param[0] : "+param[0])
            println("param[1] : "+param[1])
            println("param[2] : "+param[2])

            val total = (db.cartTotal-discount)+delivery
            val list = JSONArray()
            val p =db.CartList()
            val lst = ArrayList<PovaData>()
            for (i in 0 until p.size){
                val obj = JSONObject()
                val pid = p[i].pid
                val qty = p[i].qty
                val mesure = p[i].opnm
                val price = p[i].price
                val cid=catidlist[i]
                val tot = p[i].qty.toString().toInt() * p[i].price.toString().toDouble()
                obj.put("pid",pid.toString())
                obj.put("cid",cid.toString())
                obj.put("qty",qty.toString())
                obj.put("mesure",mesure.toString())
                obj.put("price",price.toString())
                obj.put("ptotal",tot.toString())
                list.put(obj)
            }

            val con = Connection()

            try {
                val jobj = JSONObject()
                jobj.put("vid",vendor_id)
                jobj.put("user",utils.userid())
                jobj.put("subtotal",db.cartTotal.toString())
                jobj.put("coupon_id",cid)
                jobj.put("discount",discount.toString())
                jobj.put("delivery",delivery.toString())
                jobj.put("total",total.toString())
                jobj.put("adrs_id",deli_add.adrs_id.toString())
                jobj.put("order_details",list)
                jobj.put("paymentid", param[0].toString())
                jobj.put("payment_mode", param[1].toString())//Razorpay
                jobj.put("payment_status", param[2].toString())

                Log.i("payment Input", Appconstands.order + "    " + jobj.toString())
                result = con.sendHttpPostjson2(Appconstands.order, jobj, "")


            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result
        }

        override fun onPostExecute(resp: String?) {
            try {
                if (!resp.isNullOrEmpty()) {
                    Log.e("newresp",resp)
                    val obj1 = JSONObject(resp)
                    if (obj1.getString("Status") == "Success") {
                        pDialog.dismiss()
                        //Toast.makeText(activity, obj1.getString("Message"), Toast.LENGTH_LONG).show()
                        order_id = obj1.getString("Message")
                        /*db.dropHoleCart()
                        finish()*/
                        db.dropHoleCart()

                        val currentapiVersion = Build.VERSION.SDK_INT
                        if (currentapiVersion <= 21) {
                            showthankspopup()
                        }
                        else{
                            val its = Intent(activity, PlaceOrder::class.java)
                           // its.putExtra("orderid",order_id)
                            startActivity(its)
                            finish()
                        }
                    } else {
                        pDialog.dismiss()
                        Toast.makeText(activity, obj1.getString("Message"), Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    pDialog.dismiss()
                }
            }catch (e : Exception){
                pDialog.dismiss()
            }

        }
    }

    fun startpayment(){
        val rand = Random();
        val rand_int1 = rand.nextInt(1000000000);
        println("rand_int1 : " + rand_int1)
        tid=rand_int1.toString()
        /*     idval = dollar
             tid = rand_int1.toString()
             plan = dollar*/
     /*   var amounttotal=""
        if(editText.text.toString().contains(".")){
            amounttotal=editText.text.toString()
        }
        else{
            amounttotal=editText.text.toString()+".00"
            println("amounttotal"+amounttotal)
        }*/

        val total = (db.cartTotal-discount)+delivery

        val easyUpiPayment = EasyUpiPayment.Builder()
            .with(this)
            .setPayeeVpa("vinu1112@kotak")
            .setPayeeName("Bringszo Payment")
            .setTransactionId("$rand_int1")
            .setTransactionRefId("$rand_int1")
            .setDescription("Shopping")
            .setAmount(total.toString())//"1.00
            .build();
        easyUpiPayment.startPayment();
        easyUpiPayment.setPaymentStatusListener(this@CartActivity)
    }

    /*fun startpayment() {
        val checkout = Checkout()
        try {
            val total = (db.cartTotal-discount)+delivery
            val options = JSONObject()
            options.put("name", "Pova")
            options.put("description", "Purchase Amount")
            options.put("image", "https://pova.co.in/pova/partner-with-us/images/logo.png")
            options.put("currency", "INR")
            val st =
                StringTokenizer(total.toString().trim({ it <= ' ' }), ".")
            val value1 = st.nextToken()
            val value2 = st.nextToken()
            println("st : "+st)
            println("value2 : "+value2)
            val paymentamount = Integer.valueOf(value2)
            println("paymentamount : "+(paymentamount*100) )
            options.put("amount", total*100)//paymentamount * 100
            val preFill = JSONObject()
            preFill.put("email", vendor_mail)
            preFill.put("contact", vendor_mobile)
            options.put("prefill", preFill)
            checkout.open(activity, options)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        try {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
            Log.e("razorpayPaymentID", razorpayPaymentID)
           *//*val payment = PaymentStatus()
            payment.execute(razorpayPaymentID, "success")*//*


            *//*if (dbCon.getOverAll() === 0) {
            } else {
            }*//*
        } catch (e: java.lang.Exception) {
            Log.e(
                tag,
                "Exception in onPaymentSuccess",
                e
            )
        }
    }

    override fun onPaymentError(code: Int, response: String) {
        try {
            Toast.makeText(this, "Payment failed: $code $response", Toast.LENGTH_LONG).show()
            *//*val payment = PaymentStatus()
            payment.execute("", response)*//*
            *//*if (dbCon.getOverAll() === 0) {
                //startActivity(Intent(this@CartActivity, JewelHome::class.java))
            } else {
            }*//*
        } catch (e: java.lang.Exception) {
            Log.e(
                tag,
                "Exception in onPaymentError",
                e
            )
        }
    }*/
    fun toast(msg:String){
        val toast=Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }
    inner class PaymentStatus :
        AsyncTask<String?, String?, String?>() {
        override fun onPreExecute() {
            /*previewbody.setVisibility(View.GONE)
            proceed.setVisibility(View.GONE)
            progress_lay.setVisibility(View.VISIBLE)*/
            pDialog = Dialog(this@CartActivity)
            Appconstands.loading_show(this@CartActivity,pDialog).show()
        }

        protected override fun doInBackground(vararg param: String?): String? {
            var result: String? = null
            val con = Connection()
            try {
                val jobj = JSONObject()
                jobj.put("vendor_id", vendor_id)
                jobj.put("customer_id", utils.userid())
                //jobj.put("order_date", datestr)
                jobj.put("order_id", order_id)
                //jobj.put("received_cash", paymentamount)
                jobj.put("paymentid", param[0])
                //jobj.put("type_id", typeidjson)
                jobj.put("payment_mode", "Razorpay")
                jobj.put("payment_status", param[1])
                Log.i(
                    "Paymentinput",
                    Appconstands.POVA_PAYMENTS.toString() + "  save inputtt      " + jobj.toString()
                )
                result = con.sendHttpPostjson(Appconstands.POVA_PAYMENTS, jobj).toString()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(resp: String?) {
            Log.e("Paymentresp", resp)
            /*previewbody.setVisibility(View.VISIBLE)
            proceed.setVisibility(View.VISIBLE)
            progress_lay.setVisibility(View.GONE)*/
            pDialog.dismiss()
            try {
                val jarr = JSONArray(resp)
                for (i in 0 until jarr.length()) {
                    val jobj = jarr.getJSONObject(i)
                    if (jobj.getString("Status") == "Success") {
                        val successmsg = jobj.getString("Response")
                        Log.e("successmsg", successmsg)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun showthankspopup() { //open = new Dialog(LoginActivity.this,R.style.MyCustomTheme);
        val open = Dialog(activity)
        open.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val popup = layoutInflater.inflate(R.layout.success_popup1, null)
        val ok = popup.findViewById<View>(R.id.yes) as TextView
        //open.getWindow().getAttributes().windowAnimations = R.style.MyCustomTheme;
        open.setContentView(popup)
        open.setCancelable(false)
        open.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
        open.window.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val lparam = WindowManager.LayoutParams()
        lparam.copyFrom(open.window.attributes)
        open.setCancelable(true)
        open.window
            .setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        open.show()
        ok.setOnClickListener {
            open.dismiss()
            /*val intent = Intent(activity, MapTrackingActivity::class.java)
            intent.putExtra("order_id",order_id)
            startActivity(intent)*/
            val its = Intent(activity, OrderActivity::class.java)
            its.putExtra("orderid",order_id)
            startActivity(its)
            finish()
        }
    }

}

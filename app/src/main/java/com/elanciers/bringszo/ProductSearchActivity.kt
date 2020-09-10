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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.elanciers.booking.DataClass.SpinnerPojo
import com.elanciers.bringszo.Adapters.TitledRecyclerAdapter
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.AllCat
import com.elanciers.bringszo.DataClass.TitledProduct
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_product_search.*
import kotlinx.android.synthetic.main.connection_error.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductSearchActivity : AppCompatActivity(),TitledRecyclerAdapter.OnItemClickListener,TitledRecyclerAdapter.OnBottomReachedListener {
    override fun OnItemClick(view: View, position: Int, viewType: Int) {

    }

    override fun onBottomReached(position: Int) {
        /*start=start+Products.size
        getProducts(src)*/
        if (Appconstands.net_status(activity)) {
            connection.visibility=View.GONE
            if (Products.size>14) {
                start=start+Products.size
                //getProducts(src)
            }
        }else{
            connection.visibility=View.VISIBLE
        }
    }
    val tag = "Product"
    val activity = this
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog
    var froms="";
    val Products = ArrayList<AllCat>()
    lateinit var adp : TitledRecyclerAdapter
    var start =0
    var src = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_search)
        Appconstands.changeStatusBarColor(activity, R.color.statusbar)
        utils = Utils(activity)
        db = DBController(activity)

        try{
            val frm=intent.extras
            froms=frm.getString("frm")
        }
        catch (e:Exception){

        }


        val mLayoutManager: LinearLayoutManager
        mLayoutManager = LinearLayoutManager(this)
        prolist!!.setLayoutManager(mLayoutManager)
        //recyclerView.adapter = ExtendedRecyclerAdapter(mActivity!!,pro_data,this,this)//SimpleStringRecyclerViewAdapter(recyclerView, pro_data)
        /*adp = TitledRecyclerAdapter(activity!!,pro_data,title,id,this,this)//SimpleStringRecyclerViewAdapter(recyclerView, pro_data)
        prolist.adapter = adp*/

        searchedit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, star: Int, before: Int, count: Int) {
                if (Appconstands.net_status(activity)) {
                    connection.visibility=View.GONE
                    if (searchedit.text.trim().length>3){
                        src = searchedit.text.trim().toString()
                        start=0
                        Products.clear()
                        if(froms.isEmpty()) {
                            getProducts(searchedit.text.trim().toString())
                        }
                        else if(froms=="home") {
                            getProducts_global(searchedit.text.trim().toString())
                        }
                    }
                }

                else{
                    connection.visibility=View.VISIBLE
                }

            }
        })

        back.setOnClickListener {
            finish()
        }
        view_cart_lay()

        conbtn.setOnClickListener {
            if (Appconstands.net_status(activity)) {
                connection.visibility=View.GONE
                if (searchedit.text.trim().length>3){
                    src = searchedit.text.trim().toString()
                    start=0
                    Products.clear()
                    getProducts(searchedit.text.trim().toString())
                }
            }else{
                connection.visibility=View.VISIBLE
            }
        }
    }
    fun getProducts(src:String){
        val typeid  = utils.gettypeid()
        val vendor_id  = utils.getvendor_id()
        //loading_show(activity, pDialog).show()
        println("type : "+typeid)
        println("vendor_id : "+vendor_id)
        if (start==0){
            product_shimmer.visibility = View.VISIBLE
            product_shimmer.startShimmerAnimation()
        }
        val call = ApproveUtils.Get.getSearchProducts(vendor_id.toString(),src,start.toString())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val slid = response.body() as Resp
                        val res = slid.response!!
                        for (h in 0 until res.size){
                            val fr = AllCat()
                            fr.cid = res[h].id
                            fr.title = res[h].category
                            val details = res[h].details
                            val AllPro = ArrayList<TitledProduct>()
                            for (d in 0 until details!!.size){
                                val pro = details[d].pro
                                val sid = details[d].sid
                                val name = details[d].name
                                for (p in 0 until pro!!.size){
                                    val pid = pro[p].pid
                                    val pname = pro[p].pname
                                    val image = pro[p].image
                                    val option = pro[p].option
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
                                    val dts = TitledProduct()
                                    dts.title = name
                                    dts.sid = sid
                                    dts.pid = pid
                                    dts.name = pname
                                    dts.qty = "0"
                                    dts.nm = options[0].name
                                    dts.price = options[0].price
                                    dts.selpos = 0
                                    dts.img =image
                                    dts.options = options
                                    AllPro.add(dts)
                                }
                            }
                            fr.pros = AllPro
                            Products.add(fr)
                            adp = TitledRecyclerAdapter("ProductSearchActivity",activity!!,AllPro,res[h].category.toString(),res[h].id.toString(),activity,activity)//SimpleStringRecyclerViewAdapter(recyclerView, pro_data)
                            prolist.adapter = adp
                        }
                    }
                }
                //pDialog.dismiss()
                //product_layout.visibility = View.VISIBLE
                if (start==0) {
                    product_shimmer.visibility = View.GONE
                    product_shimmer.stopShimmerAnimation()
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
                //pDialog.dismiss()
                //loading_show(activity).dismiss()
                //product_layout.visibility = View.VISIBLE
                if (start==0) {
                    product_shimmer.visibility = View.GONE
                    product_shimmer.stopShimmerAnimation()
                }
            }
        })
    }

    fun getProducts_global(src:String){
        val typeid  = utils.gettypeid()
        val vendor_id  = utils.getvendor_id()
        //loading_show(activity, pDialog).show()
        println("type : "+typeid)
        println("vendor_id : "+vendor_id)
        if (start==0){
            product_shimmer.visibility = View.VISIBLE
            product_shimmer.startShimmerAnimation()
        }
        val call = ApproveUtils.Get.getSearchProducts("",src,start.toString())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val slid = response.body() as Resp
                        val res = slid.response!!
                        for (h in 0 until res.size){
                            val fr = AllCat()
                            fr.cid = res[h].id
                            fr.title = res[h].category
                            val details = res[h].details
                            val AllPro = ArrayList<TitledProduct>()
                            for (d in 0 until details!!.size){
                                val pro = details[d].pro
                                val sid = details[d].sid
                                val name = details[d].name
                                for (p in 0 until pro!!.size){
                                    val pid = pro[p].pid
                                    val pname = pro[p].pname
                                    val image = pro[p].image
                                    val option = pro[p].option
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
                                    val dts = TitledProduct()
                                    dts.title = name
                                    dts.sid = sid
                                    dts.pid = pid
                                    dts.name = pname
                                    dts.qty = "0"
                                    dts.nm = options[0].name
                                    dts.price = options[0].price
                                    dts.selpos = 0
                                    dts.img =image
                                    dts.options = options
                                    AllPro.add(dts)
                                }
                            }
                            fr.pros = AllPro
                            Products.add(fr)
                            adp = TitledRecyclerAdapter("ProductSearchActivity",activity!!,AllPro,res[h].category.toString(),res[h].id.toString(),activity,activity)//SimpleStringRecyclerViewAdapter(recyclerView, pro_data)
                            prolist.adapter = adp
                        }
                    }
                }
                //pDialog.dismiss()
                //product_layout.visibility = View.VISIBLE
                if (start==0) {
                    product_shimmer.visibility = View.GONE
                    product_shimmer.stopShimmerAnimation()
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
                //pDialog.dismiss()
                //loading_show(activity).dismiss()
                //product_layout.visibility = View.VISIBLE
                if (start==0) {
                    product_shimmer.visibility = View.GONE
                    product_shimmer.stopShimmerAnimation()
                }
            }
        })
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

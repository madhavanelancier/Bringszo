package com.elanciers.bringszo

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.elanciers.adport.Adapter.CategoriesAdapter
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.ImageScroll
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import kotlinx.android.synthetic.main.activity_categories.category_layout
import kotlinx.android.synthetic.main.activity_categories.category_shimmer
import kotlinx.android.synthetic.main.activity_categories.cates
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesActivity : AppCompatActivity() {
    val tag = "Home"
    val activity = this
    lateinit var utils : Utils
    lateinit var pDialog: Dialog
    lateinit var db: DBController
    val catgs = ArrayList<ImageScroll>()
    lateinit var CategAdp : CategoriesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        Appconstands.changeStatusBarColor(this, R.color.statusbar)
        utils = Utils(activity)
        val tool = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tool)
        val ab = supportActionBar
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.back_arrow)
        ab!!.setTitle("All Categories")

        val extra = intent.extras
        val lat  = extra.getDouble("lat")
        val lng  = extra.getDouble("lng")
        CategAdp = CategoriesAdapter(activity,R.layout.category_list_adapter,catgs)
        cates.adapter = CategAdp
        cates.isExpanded = true

        if (Appconstands.net_status(activity)){
            getCategory()
        }

        cates.setOnItemClickListener { parent, view, position, id ->
            println("cat_time"+catgs[position].timing)
            if(catgs[position].timing!!.isEmpty()) {
                val it = Intent(activity, ProductActivity::class.java)
                it.putExtra("name", catgs[position].content)
                it.putExtra("typeid", catgs[position].id)
                utils.setVendor(
                    "1",
                    catgs[position].id.toString(),
                    "",
                    catgs[position].img.toString()
                )
                startActivity(it)
            }
            else if(catgs[position].timing!!.isNotEmpty()&&catgs[position].time=="false"){
                val alert=AlertDialog.Builder(this)
                alert.setTitle("Closed")
                alert.setMessage("Available time for this category "+catgs[position].timing)
                alert.setPositiveButton("ok", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()

                    }

                })
                val pop=alert.create()
                pop.show()
            }
            //finish()
        }

    }

    fun getCategory(){
        //loading_show(activity, pDialog).show()
        category_layout.visibility = View.GONE
        category_shimmer.visibility = View.VISIBLE
        category_shimmer.startShimmerAnimation()
        val call = ApproveUtils.Get.getCategory("1",utils.zoneid())
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
                            val data = ImageScroll()
                            data.id =res[h].id
                            data.img = res[h].image
                            data.content = res[h].name
                            data.time = res[h].time

                            if(data.time=="true"){
                                data.timing=""
                            }
                            else if(data.time=="false"){
                                data.timing=res[h].start_time+" - "+res[h].end_time

                            }

                            data.product =R.mipmap.all_store
                            catgs.add(data)
                            CategAdp.notifyDataSetChanged()
                        }
                    }
                }
                //pDialog.dismiss()
                category_layout.visibility = View.VISIBLE
                category_shimmer.visibility = View.GONE
                category_shimmer.stopShimmerAnimation()
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
                category_layout.visibility = View.VISIBLE
                category_shimmer.visibility = View.GONE
                category_shimmer.stopShimmerAnimation()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; activity adds items to the action bar if it is present.
       /* menuInflater.inflate(R.menu.search_menu, menu)
        if (isHideToolbarView){
            menu.findItem(R.id.search).setVisible(false)
        }else{
            menu.findItem(R.id.search).setVisible(true)
        }*/

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
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

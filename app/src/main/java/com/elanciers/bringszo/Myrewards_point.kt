package com.elanciers.bringszo

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.elanciers.bringszo.Adapters.Paymentadap
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.Connection
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.Rewardpointsbo
import kotlinx.android.synthetic.main.activity_myrewards_point.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Myrewards_point : AppCompatActivity() {
    val arraystr=ArrayList<String>()
    internal lateinit var itemsAdapter: Paymentadap
    private val mRecyclerListitems = java.util.ArrayList<Any>()
    private var productItems: MutableList<Rewardpointsbo>? = null
    internal lateinit var mLayoutManager: LinearLayoutManager
        var utils:Utils?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myrewards_point)
        mLayoutManager = LinearLayoutManager(this)
        recyclerlist.setLayoutManager(mLayoutManager)
        productItems = java.util.ArrayList()
        utils= Utils(this)
        itemsAdapter = Paymentadap(mRecyclerListitems, this, object :
            Paymentadap.OnItemClickListener {
            override fun OnItemClick(view: View, position: Int, viewType: Int) {
                val item = mRecyclerListitems[position] as Rewardpointsbo
                // Log.e("clickresp", "value")

                //clikffed();
            }
        })
        recyclerlist.adapter = itemsAdapter

        RewardpointsAsync().execute()

        back.setOnClickListener {
            finish()
        }

    }
    inner class RewardpointsAsync : AsyncTask<String, String, String>() {
        override fun onPreExecute() {

            progressBar.setVisibility(View.VISIBLE)
            recyclerlist.visibility = View.GONE

        }

        override fun doInBackground(vararg strings: String): String? {
            var result: String? = null
            val con = Connection()

            try {
                val jobj = JSONObject()
                jobj.put("uid",utils!!.mobile())
                Log.i(
                    "check Input",
                    Appconstands.userReward + "    " + jobj.toString()
                )
                result = con.sendHttpPostjson2(Appconstands.userReward, jobj, "")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result
        }

        override fun onPostExecute(resp: String?) {
            try {
                  Log.e("rewardresp", resp)
            } catch (e: Exception) {
                ///Log.e("rewardrespcatch", e.toString())

            }

            //prog.setVisibility(View.GONE)
            recyclerlist.visibility = View.VISIBLE
            progressBar.setVisibility(View.GONE)


            var feed: JSONArray? = null
            val feed1: String
            try {
                if (resp != null) {
                    val obj = JSONObject(resp)
                    //val objs = obj.getJSONObject(0)
                    if (obj.getString("Status").equals("Success")) {
                        val arrayLanguage = obj.getJSONArray("Response")
                        val reward = obj.getString("Total Reward")
                        textView3d.setText("₹ " + reward)
                        for (i in 0 until arrayLanguage.length()) {
                            val JO = arrayLanguage.get(i) as JSONObject
                            val username = JO.getString("name")
                            val todate = JO.getString("net")
                            val dtime = JO.getString("dtime")

                            val fullname = username

                            try {
                                productItems!!.add(
                                    Rewardpointsbo(
                                        i.toString(),
                                        fullname,
                                        dtime,
                                        todate,
                                        "",
                                        dtime,
                                        "",
                                        "",
                                        "",
                                        ""
                                    )
                                )

                            } catch (e: Exception) {
                                //Log.e("rewardrespnw", e.toString())
                                productItems!!.add(
                                    Rewardpointsbo(
                                        i.toString(),
                                        fullname,
                                        dtime,
                                        todate,
                                        "",
                                        dtime,
                                        "",
                                        "",
                                        "",
                                        ""
                                    )
                                )


                            }


                        }
                        mRecyclerListitems.clear()
                        mRecyclerListitems.addAll(productItems!!)
                        itemsAdapter.notifyDataSetChanged()
                        recyclerlist.setNestedScrollingEnabled(false);
                        // utils.savePreferences("noti","seen")
                    }
                    else{

                    }
                }else {

                    val reward = RewardpointsAsync()
                    reward.execute()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.e("E VALUE", e.toString())
                mRecyclerListitems.addAll(productItems!!)
                itemsAdapter.notifyDataSetChanged()
            }

        }
    }

}
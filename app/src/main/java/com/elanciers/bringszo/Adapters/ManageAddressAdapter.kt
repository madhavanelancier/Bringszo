package com.elanciers.bringszo.Adapters

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.elanciers.bringszo.AddressMapActivity
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.AddressData
import com.elanciers.bringszo.R
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageAddressAdapter(public val mActivity: Activity, public val mValues: ArrayList<AddressData>, private val listener: OnItemClickListener, private var onBottomReachedListener : OnBottomReachedListener ) : RecyclerView.Adapter<ManageAddressAdapter.DataObjectHolder>()
{
    val tag = "MAdrsAdp"
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog
    interface OnItemClickListener {
        fun OnItemClick(view: View, position: Int, viewType: Int)
    }

    interface OnBottomReachedListener {

        fun onBottomReached(position: Int)

    }

    fun OnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener) {

        this.onBottomReachedListener = onBottomReachedListener
    }
    internal var resource: Int = 0
    inner class DataObjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        ///internal var card : CardView
        var img : ImageView? = null
        var title : TextView? = null
        var adrs : TextView? = null
        var edit : TextView? = null
        var delete : TextView? = null

        init {

            img = itemView.findViewById(R.id.img) as ImageView
            title = itemView.findViewById(R.id.title) as TextView
            adrs = itemView.findViewById(R.id.adrs) as TextView
            edit = itemView.findViewById(R.id.edit) as TextView
            delete = itemView.findViewById(R.id.delete) as TextView


            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View) {
            try {
                listener.OnItemClick(v, adapterPosition, ITEM_CONTENT_VIEW_TYPE)

            } catch (e: Exception) {

            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DataObjectHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.manage_address_adapter, parent, false)

        return DataObjectHolder(view)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {
        holder.title!!.setText(mValues[position].adrs_title)
        holder.adrs!!.setText(mValues[position].address)
        db = DBController(mActivity)
        pDialog = Dialog(mActivity)
        utils = Utils(mActivity)
        if (mValues[position].adrs_type=="Work"){
            holder.img!!.setImageResource(R.drawable.ic_suitcase)
        }else  if (mValues[position].adrs_type=="Home"){
            holder.img!!.setImageResource(R.drawable.ic_home)
        }else{
            holder.img!!.setImageResource(R.drawable.ic_location)
        }

        holder.edit!!.setOnClickListener {
            val its = Intent(mActivity, AddressMapActivity::class.java)
            its.putExtra("id",mValues[position].adrs_id)
            mActivity.startActivityForResult(its, Appconstands.AddAddressCode)
        }
        holder.delete!!.setOnClickListener {
            val alert = AlertDialog.Builder(mActivity)
            alert.setTitle("Delete Address")
            alert.setMessage("Are you sure you want delete this address?")
            alert.setPositiveButton("yes",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    Appconstands.loading_show(mActivity, pDialog).show()
                    val call = ApproveUtils.Get.DeleteAddress(utils.userid(),mValues[position].adrs_id.toString())
                    call.enqueue(object : Callback<Resp> {
                        override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                            Log.e("$tag responce", response.toString())
                            pDialog.dismiss()
                            if (response.isSuccessful()) {
                                val example = response.body() as Resp
                                println(example)
                                if (example.status == "Success") {
                                    db.delAddress(mValues[position].adrs_id.toString())
                                    mValues.removeAt(position)
                                    notifyDataSetChanged()
                                    Toast.makeText(
                                            mActivity,
                                            example.message,
                                            Toast.LENGTH_LONG
                                    ).show()
                                    //startActivity(Intent(activity, HomeActivity::class.java))
                                } else {
                                    Toast.makeText(
                                            mActivity,
                                            example.message,
                                            Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            p0!!.dismiss()
                            //loading_show(activity).dismiss()
                        }

                        override fun onFailure(call: Call<Resp>, t: Throwable) {
                            Log.e("$tag Fail response", t.toString())
                            if (t.toString().contains("time")) {
                                Toast.makeText(
                                        mActivity,
                                        "Poor network connection",
                                        Toast.LENGTH_LONG
                                ).show()
                            }
                            p0!!.dismiss()
                            pDialog.dismiss()
                            //loading_show(activity).dismiss()
                        }
                    })
                }

            })
            alert.setNegativeButton("no",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0!!.dismiss()
                }

            })
            alert.show()

        }



        if (position == mValues.size - 1){
            onBottomReachedListener.onBottomReached(position);
        }
    }


    override fun getItemCount(): Int {
        return mValues.size
    }

    companion object {
        internal val ITEM_CONTENT_VIEW_TYPE = 1
    }

}
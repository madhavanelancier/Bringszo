package com.elanciers.bringszo.Adapters

import android.app.Activity
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.AddressData
import com.elanciers.bringszo.R

/*class AddressAdapter(public val mActivity: Activity, public val mValues: ArrayList<Addresses>, private val listener: OnItemClickListener, private var onBottomReachedListener : OnBottomReachedListener ) : RecyclerView.Adapter<AddressAdapter.DataObjectHolder>()
{

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
        var tim : TextView? = null

        init {

            img = itemView.findViewById(R.id.img) as ImageView
            title = itemView.findViewById(R.id.title) as TextView
            adrs = itemView.findViewById(R.id.adrs) as TextView
            tim = itemView.findViewById(R.id.tim) as TextView


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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item_adapter, parent, false)

        return DataObjectHolder(view)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {
        holder.title!!.setText(mValues[position].title)
        holder.adrs!!.setText(mValues[position].adrs)
        holder.tim!!.setText(mValues[position].dis)

        if (mValues[position].type=="Work"){
            holder.img!!.setImageResource(R.drawable.ic_suitcase)
        }else  if (mValues[position].type=="Home"){
            holder.img!!.setImageResource(R.drawable.ic_home)
        }else{
            holder.img!!.setImageResource(R.drawable.ic_location)
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

}*/
class AddressAdapter(internal var mActivity: Activity, internal var resource: Int, internal var mValues: ArrayList<AddressData>) :
        ArrayAdapter<AddressData>(mActivity, resource, mValues) {

    internal var inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
        val db = DBController(mActivity)
        val utils = Utils(mActivity)
        var itemView = itemView
        val holder = ViewHolder()
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.address_item_adapter, null)
            itemView!!.tag = holder
        }
        holder.img = itemView.findViewById(R.id.img) as ImageView
        holder.title = itemView.findViewById(R.id.title) as TextView
        holder.adrs = itemView.findViewById(R.id.adrs) as TextView
        holder.tim = itemView.findViewById(R.id.tim) as TextView


        holder.title!!.setText(mValues[position].adrs_title)
        holder.adrs!!.setText(mValues[position].address)
        holder.tim!!.visibility=View.GONE

        if (mValues[position].adrs_type=="Work"){
            holder.img!!.setImageResource(R.drawable.ic_suitcase)
        }else  if (mValues[position].adrs_type=="Home"){
            holder.img!!.setImageResource(R.drawable.ic_home)
        }else{
            holder.img!!.setImageResource(R.drawable.ic_location)
        }

        return itemView
    }

    internal inner class ViewHolder {
        //var customize : TextView? = null
        //var image1 : ImageView? = null
        var img : ImageView? = null
        var title : TextView? = null
        var adrs : TextView? = null
        var tim : TextView? = null

    }
}
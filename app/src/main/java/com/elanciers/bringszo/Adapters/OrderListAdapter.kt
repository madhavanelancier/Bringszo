package com.elanciers.bringszo.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elanciers.bringszo.Common.Appconstands.rupees
import com.elanciers.bringszo.DataClass.OrderDetail
import com.elanciers.bringszo.MapTrackingActivity
import com.elanciers.bringszo.OrderDetailActivity
import com.elanciers.bringszo.R
import java.text.SimpleDateFormat

class OrderListAdapter(public val context: Context, private val items: ArrayList<OrderDetail>, private val listener: OnItemClickListener, private var onBottomReachedListener : OnBottomReachedListener ) : RecyclerView.Adapter<OrderListAdapter.DataObjectHolder>()
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
        internal var nm : TextView
        internal var loc : TextView
        internal var price : TextView
        internal var items : TextView
        internal var dt : TextView
        internal var reorder : TextView
        internal var rate : TextView

        init {

            //card = itemView.findViewById(R.id.card) as CardView
            nm = itemView.findViewById(R.id.nm) as TextView
            loc = itemView.findViewById(R.id.loc) as TextView
            price = itemView.findViewById(R.id.price) as TextView
            items = itemView.findViewById(R.id.items) as TextView
            dt = itemView.findViewById(R.id.dt) as TextView
            reorder = itemView.findViewById(R.id.reorder) as TextView
            rate = itemView.findViewById(R.id.rate) as TextView


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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_list_adapter, parent, false)

        return DataObjectHolder(view)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {

        /*if (items[position].deliver_sts != "Delivered"&&items[position].deliver_sts != "Declined"){

        }else{*/
            if (items[position].deliver_sts=="Declined") {
                holder.rate!!.setTextColor(context.resources.getColor(R.color.white))
                holder.rate!!.setBackgroundResource(R.drawable.add_address_can)
                holder.rate!!.setText("Cancelled")
                holder.rate.visibility = View.VISIBLE
            }else if (items[position].deliver_sts=="Delivered") {
                holder.rate!!.setTextColor(context.resources.getColor(R.color.white))
                holder.rate!!.setBackgroundResource(R.drawable.select_address_deli)
                holder.rate!!.setText("Delivered")
                holder.rate.visibility = View.VISIBLE
            }
            else if (items[position].deliver_sts=="Processing") {
                holder.rate!!.setTextColor(context.resources.getColor(R.color.white))
                holder.rate!!.setBackgroundResource(R.drawable.select_address_ship)
                holder.rate!!.setText("Shipping")
                holder.rate.visibility = View.VISIBLE
            }
            else{
                holder.rate!!.setTextColor(context.resources.getColor(R.color.white))
                holder.rate!!.setBackgroundResource(R.drawable.select_address_deli)
                holder.rate!!.setText("Order Placed")
                holder.rate.visibility=View.VISIBLE
            }
        //}
        //Glide.with(context).load(items[position].img).into(holder.image)
        holder.nm!!.setText("Order ID - #"+items[position].id)
        holder.loc!!.setText("Payment mode - "+items[position].payment_mode)
        holder.price!!.setText(rupees+items[position].price)
        holder.items!!.setText(items[position].items)
        if (!items[position].dateandtime.isNullOrEmpty()) {
            val form = SimpleDateFormat("MMM dd, h.mm a").format(items[position].dateandtime!!.toString().toLong() * 1000)
            holder.dt!!.setText(form)
        }
        //holder.dt!!.setText(items[position].dateandtime)
        holder.reorder!!.setOnClickListener {

        }
        holder.rate!!.setOnClickListener {
            println("clickz"+"true")
            if (holder.rate!!.text.toString()=="track order") {
                println("clickz"+"false")

                val its = Intent(context, MapTrackingActivity::class.java)
                its.putExtra("orderid", items[position].id)
                context.startActivity(its)
            }
        }

        holder.rate!!.setOnClickListener {
            if (items[position].deliver_sts!="Declined") {

                val its = Intent(context, OrderDetailActivity::class.java)
                its.putExtra("id", items[position].id.toString())
                its.putExtra("dateandtime", items[position].dateandtime.toString())
                its.putExtra("deliver_sts", items[position].deliver_sts.toString())
                its.putExtra("total", items[position].price.toString())
                its.putExtra("subtotal", items[position].subtotal.toString())
                its.putExtra("delivery", items[position].delivery.toString())
                its.putExtra("discount", items[position].discount.toString())
                its.putExtra("details", items[position].details.toString())
                its.putExtra("order_details", items[position].order_details.toString())
                its.putExtra("shopnm", items[position].shopnm)
                its.putExtra("shoploc", items[position].shoploc)
                its.putExtra("adrs_type", items[position].adrs_type)
                its.putExtra("coupon_code", items[position].coupon_code)
                its.putExtra("adrs", items[position].adrs)
                its.putExtra("payment_mode", items[position].payment_mode)
                context.startActivity(its)
            }

        }

        if (position == items.size - 1){
            onBottomReachedListener.onBottomReached(position);
        }
    }


    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        internal val ITEM_CONTENT_VIEW_TYPE = 1
    }

}
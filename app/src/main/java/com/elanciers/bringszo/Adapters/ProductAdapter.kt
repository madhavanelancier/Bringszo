package com.elanciers.bringszo.Adapters

import android.app.Activity
import android.view.*
import android.widget.*
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.DataClass.CartProduct
import com.elanciers.bringszo.ProductActivity
import com.elanciers.bringszo.R

class ProductAdapter(internal var context: Activity, internal var resource: Int, internal var items: ArrayList<CartProduct>) :
        ArrayAdapter<CartProduct>(context, resource, items) {

    internal var inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
        val db = DBController(context)
        var itemView = itemView
        val holder = ViewHolder()
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.remove_ite_adapter, null)
            itemView!!.tag = holder
        }

        holder.nm = itemView.findViewById(R.id.nm) as TextView
        holder.price = itemView.findViewById(R.id.price) as TextView
        holder.qty = itemView.findViewById(R.id.qty) as TextView
        holder.option = itemView.findViewById(R.id.options) as TextView
        holder.minus = itemView.findViewById(R.id.minus) as ImageView
        holder.plus = itemView.findViewById(R.id.plus) as ImageView

        holder.nm!!.setText(items[position].pnm)
        holder.qty!!.setText(items[position].qty)
        holder.price!!.setText("₹ "+items[position].price)
        holder.option!!.setText(items[position].opnm)

        holder.minus!!.setOnClickListener {
            val qty = db.optqty(items[position].pid.toString(), items[position].opid.toString())
            val cart = CartProduct()
            cart.vendor_id = items[position].vendor_id
            cart.vendor_nm =items[position].vendor_nm
            cart.img = items[position].img
            cart.cid = items[position].cid
            cart.cnm = items[position].cnm
            cart.sid = items[position].sid
            cart.snm = items[position].pnm
            cart.pid = items[position].pid
            cart.pnm = items[position].pnm
            cart.qty = (qty.toString().toInt() - 1).toString()
            cart.opid = items[position].opid
            cart.opnm = items[position].opnm
            cart.price = items[position].price
            val d = db.CartIn_Up(cart)
            println("cartIn_up : " + d)
            items[position].qty = (qty.toString().toInt() - 1).toString()
            if (items[position].qty.toString().toInt()==0){
                items.removeAt(position)
            }
            notifyDataSetChanged()
            (context as ProductActivity).view_cart_lay()
        }

        //Plus
        holder.plus!!.setOnClickListener {
            val qty = db.optqty(items[position].pid.toString(), items[position].opid.toString())
            val cart = CartProduct()
            cart.vendor_id = items[position].vendor_id
            cart.vendor_nm =items[position].vendor_nm
            cart.img = items[position].img
            cart.cid = items[position].cid
            cart.cnm = items[position].cnm
            cart.sid = items[position].sid
            cart.snm = items[position].pnm
            cart.pid = items[position].pid
            cart.pnm = items[position].pnm
            cart.qty = (qty.toString().toInt() + 1).toString()
            cart.opid = items[position].opid
            cart.opnm = items[position].opnm
            cart.price = items[position].price
            val d = db.CartIn_Up(cart)
            println("cartIn_up : " + d)
            items[position].qty = (qty.toString().toInt() + 1).toString()
            (context as ProductActivity).view_cart_lay()
            notifyDataSetChanged()
        }

        return itemView
    }

    internal inner class ViewHolder {
        var nm : TextView? = null
        var price : TextView? = null
        var qty : TextView? = null
        var option : TextView? = null
        var image1 : ImageView? = null
        var minus : ImageView? = null
        var plus : ImageView? = null

    }

    fun OptionPopup(position: Int){

    }
}
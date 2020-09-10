package com.elanciers.adport.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.elanciers.bringszo.Common.Appconstands.ImageDomain
import com.elanciers.bringszo.DataClass.ImageScroll
import com.elanciers.bringszo.R

class CategoriesAdapter(
    internal var activity: Context,
    internal var resource: Int,
    internal var items: ArrayList<ImageScroll>
) :
    ArrayAdapter<ImageScroll>(activity, resource, items) {

    internal var inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(activity)
    }

    override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
        var itemView = itemView
        val holder = ViewHolder()
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.category_list_adapter, null)
            itemView!!.tag = holder
        }

        holder.img = itemView.findViewById(R.id.img) as ImageView
        holder.titl = itemView.findViewById(R.id.title) as TextView
//<div>Icons made by <a href="https://www.flaticon.com/authors/smashicons" title="Smashicons">Smashicons</a> from <a href="https://www.flaticon.com/"             title="Flaticon">www.flaticon.com</a></div>
        /*Picasso.with(activity).load(items[position].img).noFade().into(holder.img!!)*/
        holder.titl!!.setText(items[position].content.toString())
        holder.img!!.setImageResource(items[position].product!!)
        Glide.with(activity).load(ImageDomain+items[position].img).into(holder.img!!)

        return itemView
    }

    internal inner class ViewHolder {
        /*var card : CardView? = null*/
        var img : ImageView? = null
        var titl : TextView? = null

    }
}
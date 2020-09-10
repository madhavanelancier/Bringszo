package com.elanciers.bringszo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elanciers.bringszo.DataClass.StoreItem
import com.elanciers.bringszo.R


class StoreListAdapter(public val context: Context, private val items: ArrayList<StoreItem>, private val listener: OnItemClickListener, private var onBottomReachedListener : OnBottomReachedListener ) : RecyclerView.Adapter<StoreListAdapter.DataObjectHolder>()
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
        internal var image: ImageView
        internal var title : TextView
        internal var location : TextView
        internal var desc : TextView
        internal var times : TextView

        init {

            //card = itemView.findViewById(R.id.card) as CardView
            image= itemView.findViewById(R.id.restaurantimg) as ImageView
            title = itemView.findViewById(R.id.restaurantname) as TextView
            location = itemView.findViewById(R.id.restaurantaddress) as TextView
            desc = itemView.findViewById(R.id.offers) as TextView
            times = itemView.findViewById(R.id.restaurantcity) as TextView


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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.storelist_item_adapter, parent, false)

        return DataObjectHolder(view)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {

        if (!items[position].img.isNullOrEmpty()) {
            Glide.with(context).load(items[position].img).error(R.mipmap.ic_bringszo_logo).into(holder.image)
        }
        holder.title.setText(items[position].name)
        val loc = items[position].distance+" " + items[position].loc
        holder.location.setText(loc)
        if (items[position].offer.isNullOrEmpty()){
            holder.desc.visibility=View.INVISIBLE
        }else {
            holder.desc.setText(items[position].offer)
            holder.desc.visibility=View.VISIBLE
        }
        if(items[position].tim.isNullOrEmpty()){
            holder.times.visibility=View.INVISIBLE
        }else{
            holder.times.setText(items[position].tim)
            holder.times.visibility=View.VISIBLE
        }
        if (position == items.size - 1){
            onBottomReachedListener.onBottomReached(position);
        }
    }
    fun clear(){
        val size: Int = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        internal val ITEM_CONTENT_VIEW_TYPE = 1
    }

}

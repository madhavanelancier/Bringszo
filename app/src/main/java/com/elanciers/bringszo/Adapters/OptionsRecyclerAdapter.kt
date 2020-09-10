package com.elanciers.bringszo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elanciers.booking.DataClass.SpinnerPojo
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.R


class OptionsRecyclerAdapter(public val context: Context, private val items: ArrayList<SpinnerPojo>, private val listener: OnItemClickListener,ps:Int/*, private var onBottomReachedListener : OnBottomReachedListener */) : RecyclerView.Adapter<OptionsRecyclerAdapter.DataObjectHolder>()
{
    private var lastSelectedPosition = ps
    interface OnItemClickListener {
        fun OnItemClick(view: View, position: Int, viewType: Int, lastpos :Int)
    }

    interface OnBottomReachedListener {

        fun onBottomReached(position: Int)

    }

    /*fun OnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener) {

        this.onBottomReachedListener = onBottomReachedListener
    }*/
    internal var resource: Int = 0
    inner class DataObjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var opt : RadioButton
        internal var optpr : TextView
        init {

            opt = itemView.findViewById(R.id.opt) as RadioButton
            optpr = itemView.findViewById(R.id.optpr) as TextView

            /*opt.setOnClickListener {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            }*/

            opt.setOnClickListener(this)

        }

        override fun onClick(v: View) {
            try {
                lastSelectedPosition = adapterPosition
                listener.OnItemClick(v, adapterPosition, ITEM_CONTENT_VIEW_TYPE,lastSelectedPosition)
                notifyDataSetChanged();
            } catch (e: Exception) {

            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DataObjectHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.optioslist_adapter, parent, false)

        return DataObjectHolder(view)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {

        //Glide.with(context).load(items[position].img).into(holder.image)


        holder.opt!!.setText(items[position].name)
        holder.optpr!!.setText(Appconstands.rupees +items[position].price)

        holder.opt.setChecked(lastSelectedPosition == position)
        /*if (position == items.size - 1){
            onBottomReachedListener.onBottomReached(position);
        }*/
    }


    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        internal val ITEM_CONTENT_VIEW_TYPE = 1
    }

}

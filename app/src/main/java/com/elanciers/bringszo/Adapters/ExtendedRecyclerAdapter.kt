package com.elanciers.bringszo.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elanciers.bringszo.Common.ExpandableHeightGridView
import com.elanciers.bringszo.DataClass.AllProItem
import com.elanciers.bringszo.R

/*class ExtendedRecyclerAdapter(private val mActivity: Activity,
                              private val mValues: ArrayList<AllProItem>,
                              private val listener: OnItemClickListener, private var onBottomReachedListener : OnBottomReachedListener) : RecyclerView.Adapter<ExtendedRecyclerAdapter.ViewHolder>()
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

    inner class ViewHolder
    // public final ImageView mImageViewWishlist;

    (val mView: View) : RecyclerView.ViewHolder(mView) {
        var subcat : TextView? = null
        var subcatlist : ExpandableHeightGridView? = null


        init {
            subcat = mView.findViewById(R.id.subcat) as TextView
            subcatlist = mView.findViewById(R.id.subcatlist) as ExpandableHeightGridView

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.extented_recycler_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onViewRecycled(holder: ViewHolder) {

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.subcat!!.setText(mValues[position].name)
        val adp = ProductAdapter(mActivity,R.layout.cart_item_adapter,mValues[position].pros!!)
        holder.subcatlist.adapter=adp

    }

    override fun getItemCount(): Int {
        return mValues.size
    }
}*/

class ExtendedRecyclerAdapter(public val mActivity: Activity, public val mValues: ArrayList<AllProItem>, private val listener: OnItemClickListener, private var onBottomReachedListener : OnBottomReachedListener ) : RecyclerView.Adapter<ExtendedRecyclerAdapter.DataObjectHolder>()
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
        var subcat : TextView? = null
        var subcatlist : ExpandableHeightGridView? = null

        init {

            //card = itemView.findViewById(R.id.card) as CardView
            subcat = itemView.findViewById(R.id.subcat) as TextView
            subcatlist = itemView.findViewById(R.id.subcatlist) as ExpandableHeightGridView


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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.extented_recycler_adapter, parent, false)

        return DataObjectHolder(view)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {

        holder.subcat!!.setText(mValues[position].name)
        holder.subcatlist!!.isExpanded=true
        /*val adp = ProductAdapter(mActivity,R.layout.product_item_adapter,mValues[position].pros!!)
        holder.subcatlist!!.adapter=adp*/

        holder.subcatlist!!.setOnItemClickListener { parent, view, posi, id ->
            println("subcatlist : "+posi)
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

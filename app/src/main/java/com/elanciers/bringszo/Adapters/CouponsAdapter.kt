package com.elanciers.bringszo.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elanciers.bringszo.R
import android.graphics.drawable.GradientDrawable
import android.graphics.Color
import android.text.Html
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.elanciers.bringszo.DataClass.Coupons
import kotlin.random.Random.Default.nextInt


class CouponsAdapter(public val mActivity: Activity, public val mValues: ArrayList<Coupons>, private val listener: OnItemClickListener, private var onBottomReachedListener : OnBottomReachedListener ) : RecyclerView.Adapter<CouponsAdapter.DataObjectHolder>()
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
        var coupon : LinearLayout? = null
        var extend : CardView? = null
        var dis : TextView? = null
        var use : TextView? = null
        var small_dis : TextView? = null
        var more : TextView? = null
        var terms : TextView? = null
        var imageView15 : ImageView? = null

        init {

            //card = itemView.findViewById(R.id.card) as CardView
            coupon = itemView.findViewById(R.id.coupon) as LinearLayout
            extend = itemView.findViewById(R.id.extend) as CardView
            dis = itemView.findViewById(R.id.dis) as TextView
            use = itemView.findViewById(R.id.use) as TextView
            small_dis = itemView.findViewById(R.id.small_dis) as TextView
            more = itemView.findViewById(R.id.more) as TextView
            terms = itemView.findViewById(R.id.terms) as TextView
            imageView15 = itemView.findViewById(R.id.imageView15) as ImageView


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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coupon_adapter, parent, false)

        return DataObjectHolder(view)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {
        val htmlAsString = mActivity.getString(R.string.html);      // used by WebView
        var st = "<html>\n" +
                        "<head>\n" +
                        "</head>\n" +
                        "<body>\n" +
                "Terms & Conditions Apply\n" +
                "<ul>\n"/* +
                "        <li>One</li>\n" +
                "        <li>Two</li>\n" +
                "        <li>Three</li>\n" +
                "        </ul>\n" +
                "        ]]>"*/
        for (h in 0 until mValues[position].brief!!.size) {
            st = st + "<li>On Minimum Order of ${mValues[position].brief!![h]}</li>\n"
        }
        val htmlAsSpanned = Html.fromHtml(st+ "</ul> </body></html>");
        holder.terms!!.setText(htmlAsSpanned)
        holder.dis!!.setText(mValues[position].dis+"%")
        holder.use!!.setText(mValues[position].use)
        holder.small_dis!!.setText("use this Coupon code "+mValues[position].small_dis)
        val color = Color.argb(255, nextInt(256), nextInt(256), nextInt(256))
        var gradients =  mActivity.resources.getIntArray(R.array.grad1)
        if (position %2==0){
            gradients = mActivity.resources.getIntArray(R.array.grad1)
            //holder.textView28!!.setBackgroundColor(mActivity.resources.getColor(R.color.dis1))
            holder.imageView15!!.setImageResource(R.mipmap.coupon_1)
        }else{
            holder.imageView15!!.setImageResource(R.mipmap.coupon_2)
            gradients = mActivity.resources.getIntArray(R.array.grad2)
            //holder.textView28!!.setBackgroundColor(mActivity.resources.getColor(R.color.dis2))
        }
        val colors2 = intArrayOf(Color.argb(255, nextInt(256), nextInt(256), nextInt(256)), Color.argb(255, nextInt(256), nextInt(256), nextInt(256)))
        val colors = intArrayOf(Color.parseColor("#008000"), Color.parseColor("#ADFF2F"))
        val gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradients)
        gd.cornerRadius = 0f


        //holder.coupon!!.setBackground(gd)
        //holder.extend!!.setBackgroundDrawable()

        holder.more!!.setOnClickListener {
            if (holder.extend!!.visibility==View.VISIBLE){
                holder.extend!!.visibility=View.GONE
                holder.more!!.setText("+ More")
            }else{
                holder.extend!!.visibility=View.VISIBLE
                holder.more!!.setText("- Less")
            }
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
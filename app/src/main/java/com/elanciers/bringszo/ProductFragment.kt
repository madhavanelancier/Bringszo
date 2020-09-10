package com.elanciers.bringszo

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.elanciers.bringszo.Adapters.TitledRecyclerAdapter
import com.elanciers.bringszo.DataClass.TitledProduct

class ProductFragment(private val pro_data : ArrayList<TitledProduct>,val title:String,val id:String) : Fragment(),TitledRecyclerAdapter.OnItemClickListener,TitledRecyclerAdapter.OnBottomReachedListener {
    override fun OnItemClick(view: View, position: Int, viewType: Int) {

    }

    override fun onBottomReached(position: Int) {

    }
    lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity
    }

    lateinit var adp : TitledRecyclerAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.product_fragment, container, false)

        dialog = Dialog(view.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        //dialog.setContentView(R.layout.progress_bar)
        dialog.setCancelable(false)

        val nodata = view.findViewById<View>(R.id.nodata) as TextView
        val recyclerView = view.findViewById<View>(R.id.recyclerview) as RecyclerView
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        //recyclerView.adapter = ExtendedRecyclerAdapter(mActivity!!,pro_data,this,this)//SimpleStringRecyclerViewAdapter(recyclerView, pro_data)
        adp = TitledRecyclerAdapter("ProductActivity",mActivity!!,pro_data,title,id,this,this)//SimpleStringRecyclerViewAdapter(recyclerView, pro_data)
        recyclerView.adapter = adp

        return view
    }

    override fun onResume() {
        super.onResume()
        adp.notifyDataSetChanged()
        // cart_text.setText(dbCon.getOverAll() + "");
        /*Intent mIntent = mActivity.getIntent();
        mActivity.finish();
        startActivity(mIntent);*/
    }




    /*inner class SimpleStringRecyclerViewAdapter(private val mRecyclerView: RecyclerView, // private String[] mValues;
                                                private val mValues: ArrayList<ProductItems>*//*String[] items*//*) : RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>()
    {


        inner class ViewHolder
        // public final ImageView mImageViewWishlist;

        (val mView: View) : RecyclerView.ViewHolder(mView) {
            var nm : TextView? = null
            var price : TextView? = null
            var qty : TextView? = null
            var option : Spinner? = null
            var image1 : ImageView? = null
            var minus : ImageView? = null
            var plus : ImageView? = null

            init {
                nm = mView.findViewById(R.id.nm) as TextView
                price = mView.findViewById(R.id.price) as TextView
                //qty = mView.findViewById(R.id.qty) as TextView
                option = mView.findViewById(R.id.option) as Spinner
                //minus = mView.findViewById(R.id.minus) as ImageView
                //plus = mView.findViewById(R.id.plus) as ImageView
                image1 = mView.findViewById(R.id.image1) as ImageView

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item_adapter, parent, false)
            return ViewHolder(view)
        }

        override fun onViewRecycled(holder: ViewHolder) {

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.nm!!.setText(pro_data[position].name)
            //holder.qty!!.setText(pro_data[position].qty)
            holder.price!!.setText("₹ "+pro_data[position].price)

            if (pro_data[position].options!!.isNotEmpty()) {
                holder.option!!.visibility=View.VISIBLE
                val optAdp = SpinAdapter(mActivity!!, R.layout.spinner_list_item, pro_data[position].options!!)
                holder.option!!.adapter = optAdp
            }else{
                holder.option!!.visibility=View.INVISIBLE
            }

            *//*holder.minus!!.setOnClickListener {

            }
            holder.plus!!.setOnClickListener {

            }*//*
        }

        override fun getItemCount(): Int {
            return mValues.size
        }
    }*/

    companion object {
        private var mActivity: Activity? = null
    }
}

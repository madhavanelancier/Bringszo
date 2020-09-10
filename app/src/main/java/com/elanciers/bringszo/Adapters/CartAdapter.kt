package com.elanciers.bringszo.Adapters

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elanciers.bringszo.CartActivity
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.ExpandableHeightGridView
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.CartProduct
import com.elanciers.bringszo.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class CartAdapter(internal var mActivity: Activity, internal var resource: Int, internal var items: ArrayList<CartProduct>) :
        ArrayAdapter<CartProduct>(mActivity, resource, items) {

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
            itemView = inflater.inflate(R.layout.cart_item_adapter, null)
            itemView!!.tag = holder
        }
        holder.nm = itemView.findViewById(R.id.nm) as TextView
        holder.price = itemView.findViewById(R.id.price) as TextView
        holder.qty = itemView.findViewById(R.id.qty) as TextView
        holder.option = itemView.findViewById(R.id.options) as TextView
        holder.minus = itemView.findViewById(R.id.minus) as ImageView
        holder.plus = itemView.findViewById(R.id.plus) as ImageView

        if (items.size!=0) {
            /*if (items[position].qty=="0"){
                items.removeAt(position)
                notifyDataSetChanged()
            }*/
            //SetText
            //val sps = items[position].opid.toString().toInt()
            holder.nm!!.setText(items[position].pnm)
            holder.qty!!.setText(items[position].qty)
            //println("sps : " + sps)
            //println("items[position].options!![sps].price : " + items[position].options!![sps].price)
            holder.option!!.setText(items[position].opnm)
            holder.price!!.setText("₹ " + items[position].price)

            if(items[position].options.isNullOrEmpty()){
                holder.option!!.visibility = View.INVISIBLE
            }else {
                if (items[position].options!![0].name == "") {
                    holder.option!!.visibility = View.INVISIBLE
                } else {
                    holder.option!!.visibility = View.VISIBLE
                }
            }
            //Minus
            holder.minus!!.setOnClickListener {

                val sp = items[position].opid!!.toString().toInt()
                val qty = db.optqty(items[position].pid.toString(), items[position].options!![sp].id.toString())
                val cart = CartProduct()
                cart.vendor_id = items[position].vendor_id
                cart.vendor_nm = items[position].vendor_nm
                cart.img = items[position].img
                cart.cid = items[position].cid
                cart.cnm = items[position].cnm
                cart.sid = items[position].sid
                cart.snm = items[position].snm
                cart.pid = items[position].pid
                cart.pnm = items[position].pnm
                cart.qty = (qty.toString().toInt() - 1).toString()
                cart.opid = items[position].opid
                cart.opnm = items[position].opnm
                cart.price = items[position].price
                items[position].qty = (qty.toString().toInt() - 1).toString()
                val d = db.CartIn_Up(items[position])
                if (items[position].qty.toString().toInt()==0){
                    items.removeAt(position)
                }
                println("cartIn_up : " + d)

                (mActivity as CartActivity).tot()
                notifyDataSetChanged()
            }

            //Plus
            holder.plus!!.setOnClickListener {

                val sp = items[position].opid!!.toString().toInt()
                val qty = db.optqty(items[position].pid.toString(), items[position].options!![sp].id.toString())
                val cart = CartProduct()
                cart.vendor_id = items[position].vendor_id
                cart.vendor_nm = items[position].vendor_nm
                cart.img = items[position].img
                cart.cid = items[position].cid
                cart.cnm = items[position].cnm
                cart.sid = items[position].sid
                cart.snm = items[position].snm
                cart.pid = items[position].pid
                cart.pnm = items[position].pnm
                cart.qty = (qty.toString().toInt() + 1).toString()
                cart.opid = items[position].opid
                cart.opnm = items[position].opnm
                cart.price = items[position].price
                cart.options = items[position].options
                items[position].qty = (qty.toString().toInt() + 1).toString()
                val d = db.CartIn_Up(items[position])
                println("cartIn_up : " + d)

                (mActivity as CartActivity).tot()
                notifyDataSetChanged()

            }

            //OptionPOP
            holder.option!!.setOnClickListener {
                //OptionPop(items[position],position)

            }
        }

        return itemView
    }
    fun RepeatPop(item : CartProduct,pos:Int){
        val db = DBController(mActivity)
        val utils = Utils(mActivity)
        val openwith = BottomSheetDialog(mActivity)
        openwith.setOnDismissListener {
            println("dismiss")
        }
        openwith.requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        val popUpView = mActivity.layoutInflater.inflate(R.layout.repeat_item, null)
        val pronm = popUpView.findViewById(R.id.pronm) as TextView
        val variant = popUpView.findViewById(R.id.variant) as TextView
        val variant2 = popUpView.findViewById(R.id.variant2) as TextView
        val choose = popUpView.findViewById(R.id.choose) as TextView
        val repeat = popUpView.findViewById(R.id.repeat) as TextView
        val cancel = popUpView.findViewById(R.id.cancel) as ImageView
        pronm.setText(item.pnm)
        val cart1 = db.LastItm(item.pid.toString())
        val opid = cart1.opid
        val opnm = cart1.opnm
        val price = cart1.price
        val qty = cart1.qty
        variant.setText(cart1.opnm)
        variant2.setText(Appconstands.rupees +cart1.price)
        val animMoveToTop = AnimationUtils.loadAnimation(mActivity, R.anim.bottom_top)
        popUpView.animation =animMoveToTop
        cancel.setOnClickListener {
            openwith.dismiss()
        }
        repeat.setOnClickListener {
            item.price = price
            item.opid = opid.toString()
            println("db.vendor_id : "+db.vendor_id)
            println("utils.getvendor_id() : "+utils.getvendor_id())
            //val sp = opid.toString().toInt()
            //val qty = db.optqty(items.pid.toString(),opid.toString())
            val cart = CartProduct()
            cart.vendor_id = utils.getvendor_id()
            cart.vendor_nm = utils.getvendor_nm()
            cart.img = utils.getvendor_img()
            cart.cid = item.cid
            cart.cnm = item.cnm
            cart.sid = item.sid
            cart.snm = item.snm
            cart.pid = item.pid
            cart.pnm = item.pnm
            cart.qty = (qty.toString().toInt() + 1).toString()
            cart.opid = opid.toString()
            cart.opnm = opnm.toString()
            cart.price = price.toString()
            cart.options = item.options
            val d = db.CartIn_Up(cart)
            println("cartIn_up : " + d)
            item.qty = (qty.toString().toInt() + 1).toString()
            items[pos] = cart
            //notifyDataSetChanged()
            //holder.add!!.visibility=View.GONE
            //items.qty="1"
            openwith.dismiss()
            (mActivity as CartActivity).tot()
            notifyDataSetChanged()
        }

        choose.setOnClickListener {
            openwith.dismiss()
            OptionPop(item,pos)
        }
        openwith.setContentView(popUpView);
        openwith.setCancelable(true)
        openwith.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        openwith.window!!.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        openwith.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val displaymetrics = DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        val width =  (displaymetrics.widthPixels * 1);
        val height =  (displaymetrics.heightPixels * 1);
        openwith.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        openwith.show()
    }

    fun OptionPop(item: CartProduct,pos : Int){
        val db = DBController(mActivity)
        val utils = Utils(mActivity)
        var selpos = item.opid!!.toString().toInt()
        val openwith = BottomSheetDialog(mActivity)
        openwith.setOnDismissListener {
            println("dismiss")
        }
        openwith.requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        val popUpView = mActivity.layoutInflater.inflate(R.layout.options_popup, null)
        /*popUpView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val eventConsumed = gestureDetector.onTouchEvent(event);
                println("eventConsumed : "+eventConsumed)
                if (eventConsumed) {
                    return true;
                } else {
                    return false;
                }
            }

        });*/
        val pronm = popUpView.findViewById(R.id.pronm) as TextView
        val selected = popUpView.findViewById(R.id.selected) as TextView
        val itmtot = popUpView.findViewById(R.id.itmtot) as TextView
        val add_item = popUpView.findViewById(R.id.add_item) as TextView
        val cancel = popUpView.findViewById(R.id.cancel) as ImageView
        pronm.setText(item.pnm)
        val optionslist = popUpView.findViewById(R.id.optionslist) as RecyclerView
        //optionslist.isExpanded=true
        selected.setText(item.options!![selpos].name)
        itmtot.setText(Appconstands.rupees +item.options!![selpos].price)
        optionslist.adapter=OptionsRecyclerAdapter(mActivity,item.options!!,object : OptionsRecyclerAdapter.OnItemClickListener {
            override fun OnItemClick(view: View, pos: Int, viewType: Int, lastpos : Int) {
                selpos=pos
                println("option clicked name : "+item.options!![selpos].name)
                println("option clicked price : "+item.options!![selpos].price)
                selected.setText(item.options!![selpos].name)
                itmtot.setText(Appconstands.rupees +item.options!![selpos].price)
            }

        },selpos)
        //optionslist.adapter=OptionsAdapter(context,R.layout.optioslist_adapter,items.options!!)
        //val animMoveToTop = AnimationUtils.loadAnimation(mActivity, R.anim.bottom_top)
        /*animMoveToTop.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {}
        })*/
        //popUpView.animation =animMoveToTop
        cancel.setOnClickListener {
            openwith.dismiss()
        }

        add_item.setOnClickListener {
            openwith.dismiss()
            if (db.vendor_id.isNullOrEmpty()||db.vendor_id==utils.getvendor_id()) {
                item.price = item.options!![selpos].price
                item.opid = selpos.toString()
                println("db.vendor_id : "+db.vendor_id)
                println("utils.getvendor_id() : "+utils.getvendor_id())
                val sp = selpos
                val qty = db.optqty(item.pid.toString(),item.options!![sp].id.toString())
                val cart = CartProduct()
                cart.vendor_id = utils.getvendor_id()
                cart.vendor_nm = utils.getvendor_nm()
                cart.img = utils.getvendor_img()
                cart.cid = item.cid
                cart.cnm = item.cnm
                cart.sid = item.sid
                cart.snm = item.snm
                cart.pid = item.pid
                cart.pnm = item.pnm
                cart.qty = (qty.toString().toInt() + 1).toString()
                cart.opid = item.options!![sp].id
                cart.opnm = item.options!![sp].name
                cart.price = item.options!![sp].price
                val d = db.CartIn_Up(cart)
                println("cartIn_up : " + d)
                item.qty = (qty.toString().toInt() + 1).toString()
                items[pos] = cart
                //notifyDataSetChanged()
                //holder.add!!.visibility=View.GONE
                //items.qty="1"
                (mActivity as CartActivity).tot()
                notifyDataSetChanged()
            }else{
                println("db.vendor_id : "+db.vendor_id)
                println("utils.getvendor_id() : "+utils.getvendor_id())
            }
            //notifyDataSetChanged()
        }

        openwith.setContentView(popUpView);
        openwith.setCancelable(true)
        openwith.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        openwith.window!!.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        openwith.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val displaymetrics = DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        val width =  (displaymetrics.widthPixels * 1);
        val height =  (displaymetrics.heightPixels * 1);
        openwith.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        openwith.show()
    }

    fun RemovePop(items: CartProduct){
        val db = DBController(mActivity)
        val utils = Utils(mActivity)
        val openwith = BottomSheetDialog(mActivity)
        openwith.setOnDismissListener {
            println("dismiss")
            notifyDataSetChanged()
        }
        openwith.requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        val popUpView = mActivity.layoutInflater.inflate(R.layout.remove_item, null)
        val pronm = popUpView.findViewById(R.id.pronm) as TextView
        val optionslist = popUpView.findViewById(R.id.optionslist) as ExpandableHeightGridView
        val cancel = popUpView.findViewById(R.id.cancel) as ImageView
        val cart1 = db.LastItm(items.pid.toString())
        pronm.setText("You have multiple customizations for ${cart1.pnm}. Choose which one to remove.")
        val cartlist = db.CartList()
        val adp = ProductAdapter(mActivity,R.layout.remove_ite_adapter,cartlist)
        optionslist.adapter=adp
        optionslist.isExpanded=true

        val animMoveToTop = AnimationUtils.loadAnimation(mActivity, R.anim.bottom_top)
        popUpView.animation =animMoveToTop
        cancel.setOnClickListener {
            openwith.dismiss()
        }
        openwith.setContentView(popUpView);
        openwith.setCancelable(true)
        openwith.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        openwith.window!!.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        openwith.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val displaymetrics = DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        val width =  (displaymetrics.widthPixels * 1);
        val height =  (displaymetrics.heightPixels * 1);
        openwith.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        openwith.show()
    }

    internal inner class ViewHolder {
        //var customize : TextView? = null
        //var image1 : ImageView? = null
        var nm : TextView? = null
        var price : TextView? = null
        var qty : TextView? = null
        var option : TextView? = null
        var minus : ImageView? = null
        var plus : ImageView? = null

    }
}
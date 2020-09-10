package com.elanciers.bringszo.Adapters

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils
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

class CartRecyclerAdapter(public val mActivity: Activity, public val items: ArrayList<CartProduct>, val cnm :String, val cid :String, private val listener: OnItemClickListener, private var onBottomReachedListener : OnBottomReachedListener ) : RecyclerView.Adapter<CartRecyclerAdapter.DataObjectHolder>()
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
        var nm : TextView? = null
        var price : TextView? = null
        var qty : TextView? = null
        var option : TextView? = null
        var minus : ImageView? = null
        var plus : ImageView? = null
        lateinit var db : DBController

        init {

            nm = itemView.findViewById(R.id.nm) as TextView
            price = itemView.findViewById(R.id.price) as TextView
            qty = itemView.findViewById(R.id.qty) as TextView
            option = itemView.findViewById(R.id.options) as TextView
            minus = itemView.findViewById(R.id.minus) as ImageView
            plus = itemView.findViewById(R.id.plus) as ImageView

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_adapter, parent, false)

        return DataObjectHolder(view)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {
        val db = DBController(mActivity)
        val utils = Utils(mActivity)

        //Option
        if (items[position].options!!.isNotEmpty()) {
            //val sp = items[position].opid!!.toString().toInt()
            val qty = db.qty(items[position].pid.toString())//,items[position].options!![sp].id.toString()
            println("qty : "+qty)
            items[position].qty = qty
            holder.option!!.visibility= View.VISIBLE
            holder.option!!.setText(items[position].options!![0].name)
            println("items[position].options!!.size : "+items[position].options!!.size)
            /*if (items[position].options!!.size==1){
                holder.customize!!.visibility=View.GONE
            }else{
                holder.customize!!.visibility=View.VISIBLE
            }*/
        }else{
            holder.option!!.visibility= View.INVISIBLE
        }

        //SetText
        holder.nm!!.setText(items[position].pnm)
        holder.qty!!.setText(items[position].qty)
        holder.price!!.setText("₹ "+items[position].options!![0].price)
        /*val varient = db.varients(items[position].pid.toString())
        println("varient : "+varient)
        if (!varient.isNullOrEmpty()) {
            if (varient!="0") {
                holder!!.customize!!.setText(varient + " Varients added")
            }
        }*/

        /*//Qty
        if (items[position].qty=="0"){
            holder.add!!.visibility=View.VISIBLE
        }else{
            holder.add!!.visibility=View.GONE
        }*/

        /*//Add
        holder.add!!.setOnClickListener {
            if (db.vendor_id.isNullOrEmpty()||db.vendor_id==utils.getvendor_id()) {
                println("db.vendor_id : "+db.vendor_id)
                println("utils.getvendor_id() : "+utils.getvendor_id())
                val sp = items[position].opid!!.toString().toInt()
                val qty = db.optqty(items[position].pid.toString(),items[position].options!![sp].id.toString())
                val cart = CartProduct()
                cart.vendor_id = utils.getvendor_id()
                cart.vendor_nm = utils.getvendor_nm()
                cart.img = utils.getvendor_img()
                cart.cid = cid
                cart.cnm = cnm
                cart.sid = items[position].sid
                cart.snm = items[position].name
                cart.pid = items[position].pid
                cart.pnm = items[position].name
                cart.qty = (qty.toString().toInt() + 1).toString()
                cart.opid = items[position].options!![sp].id
                cart.opnm = items[position].options!![sp].name
                cart.price = items[position].options!![sp].price
                val d = db.CartIn_Up(cart)
                println("cartIn_up : " + d)
                items[position].qty = (qty.toString().toInt() + 1).toString()
                //notifyDataSetChanged()
                //holder.add!!.visibility=View.GONE
                //items[position].qty="1"
                (mActivity as CartActivity).tot()
                notifyDataSetChanged()
            }else{
                println("db.vendor_id : "+db.vendor_id)
                println("utils.getvendor_id() : "+utils.getvendor_id())
                //CartRemove
            }
        }*/

        //Minus
        holder.minus!!.setOnClickListener {
            if (db.vendor_id.isNullOrEmpty()||db.vendor_id==utils.getvendor_id()) {
                if (db.more(items[position].pid.toString())>1){
                    //RemovePop
                    RemovePop(items[position])
                }else {
                    println("db.vendor_id : " + db.vendor_id)
                    println("utils.getvendor_id() : " + utils.getvendor_id())
                    val sp = items[position].opid!!.toString().toInt()
                    val qty = db.optqty(items[position].pid.toString(), items[position].options!![sp].id.toString())
                    val cart = CartProduct()
                    cart.vendor_id = utils.getvendor_id()
                    cart.vendor_nm = utils.getvendor_nm()
                    cart.img = utils.getvendor_img()
                    cart.cid = items[position].cid
                    cart.cnm = items[position].cnm
                    cart.sid = items[position].sid
                    cart.snm = items[position].snm
                    cart.pid = items[position].pid
                    cart.pnm = items[position].pnm
                    cart.qty = (qty.toString().toInt() - 1).toString()
                    cart.opid = items[position].options!![sp].id
                    cart.opnm = items[position].options!![sp].name
                    cart.price = items[position].options!![sp].price
                    val d = db.CartIn_Up(cart)
                    println("cartIn_up : " + d)
                    items[position].qty = (qty.toString().toInt() - 1).toString()
                    //notifyDataSetChanged()
                    //holder.add!!.visibility=View.GONE
                    //items[position].qty="1"
                    (mActivity as CartActivity).tot()
                    notifyDataSetChanged()
                }
            }else{
                println("db.vendor_id : "+db.vendor_id)
                println("utils.getvendor_id() : "+utils.getvendor_id())
                //CartRemove
            }
            //if (holder.qty!!.text.toString().toInt()>=1){
            //items[position].qty = (holder.qty!!.text.toString().toInt()-1).toString()
            //}else{
            //items[position].qty = "0"
            //}
            //notifyDataSetChanged()
        }

        //Plus
        holder.plus!!.setOnClickListener {
            if (db.vendor_id.isNullOrEmpty()||db.vendor_id==utils.getvendor_id()) {
                if (db.more(items[position].pid.toString())>1){
                    //db.LastItm(items[position].pid.toString())
                    RepeatPop(items[position])
                }else {
                    if (items[position].options!!.size>1){
                        RepeatPop(items[position])
                    }else {
                        println("db.vendor_id : " + db.vendor_id)
                        println("utils.getvendor_id() : " + utils.getvendor_id())
                        val sp = items[position].opid!!.toString().toInt()
                        val qty = db.optqty(items[position].pid.toString(), items[position].options!![sp].id.toString())
                        val cart = CartProduct()
                        cart.vendor_id = utils.getvendor_id()
                        cart.vendor_nm = utils.getvendor_nm()
                        cart.img = utils.getvendor_img()
                        cart.cid = items[position].cid
                        cart.cnm = items[position].cnm
                        cart.sid = items[position].sid
                        cart.snm = items[position].snm
                        cart.pid = items[position].pid
                        cart.pnm = items[position].pnm
                        cart.qty = (qty.toString().toInt() + 1).toString()
                        cart.opid = items[position].options!![sp].id
                        cart.opnm = items[position].options!![sp].name
                        cart.price = items[position].options!![sp].price
                        val d = db.CartIn_Up(cart)
                        println("cartIn_up : " + d)
                        items[position].qty = (qty.toString().toInt() + 1).toString()
                        notifyDataSetChanged()
                    }
                }
            }else{
                println("db.vendor_id : "+db.vendor_id)
                println("utils.getvendor_id() : "+utils.getvendor_id())
                //CartRemove
            }

            //OptionPOP
            holder.option!!.setOnClickListener {
                OptionPop(items[position])
            }
        }

        //OptionPOP
        holder.option!!.setOnClickListener {
            OptionPop(items[position])
            //OptionPopup(position)
        }

        if (position == items.size - 1){
            onBottomReachedListener.onBottomReached(position);
        }


    }

    fun RepeatPop(items : CartProduct){
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
        pronm.setText(items.pnm)
        val cart1 = db.LastItm(items.pid.toString())
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
            items.price = price
            items.opid = opid.toString()
            println("db.vendor_id : "+db.vendor_id)
            println("utils.getvendor_id() : "+utils.getvendor_id())
            //val sp = opid.toString().toInt()
            //val qty = db.optqty(items.pid.toString(),opid.toString())
            val cart = CartProduct()
            cart.vendor_id = utils.getvendor_id()
            cart.vendor_nm = utils.getvendor_nm()
            cart.img = utils.getvendor_img()
            cart.cid = items.cid
            cart.cnm = items.cnm
            cart.sid = items.sid
            cart.snm = items.snm
            cart.pid = items.pid
            cart.pnm = items.pnm
            cart.qty = (qty.toString().toInt() + 1).toString()
            cart.opid = opid.toString()
            cart.opnm = opnm.toString()
            cart.price = price.toString()
            val d = db.CartIn_Up(cart)
            println("cartIn_up : " + d)
            items.qty = (qty.toString().toInt() + 1).toString()
            //notifyDataSetChanged()
            //holder.add!!.visibility=View.GONE
            //items.qty="1"
            openwith.dismiss()
            (mActivity as CartActivity).tot()
            notifyDataSetChanged()
        }

        choose.setOnClickListener {
            openwith.dismiss()
            OptionPop(items)
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

    fun OptionPop(items: CartProduct){
        val db = DBController(mActivity)
        val utils = Utils(mActivity)
        var selpos = items.opid!!.toString().toInt()
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
        pronm.setText(items.pnm)
        val optionslist = popUpView.findViewById(R.id.optionslist) as RecyclerView
        //optionslist.isExpanded=true
        selected.setText(items.options!![selpos].name)
        itmtot.setText(Appconstands.rupees +items.options!![selpos].price)
        optionslist.adapter=OptionsRecyclerAdapter(mActivity,items.options!!,object : OptionsRecyclerAdapter.OnItemClickListener {
            override fun OnItemClick(view: View, pos: Int, viewType: Int, lastpos : Int) {
                selpos=pos
                println("option clicked name : "+items.options!![selpos].name)
                println("option clicked price : "+items.options!![selpos].price)
                selected.setText(items.options!![selpos].name)
                itmtot.setText(Appconstands.rupees +items.options!![selpos].price)
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
                items.price = items.options!![selpos].price
                items.opid = selpos.toString()
                println("db.vendor_id : "+db.vendor_id)
                println("utils.getvendor_id() : "+utils.getvendor_id())
                val sp = selpos
                val qty = db.optqty(items.pid.toString(),items.options!![sp].id.toString())
                val cart = CartProduct()
                cart.vendor_id = utils.getvendor_id()
                cart.vendor_nm = utils.getvendor_nm()
                cart.img = utils.getvendor_img()
                cart.cid = items.cid
                cart.cnm = items.cnm
                cart.sid = items.sid
                cart.snm = items.snm
                cart.pid = items.pid
                cart.pnm = items.pnm
                cart.qty = (qty.toString().toInt() + 1).toString()
                cart.opid = items.options!![sp].id
                cart.opnm = items.options!![sp].name
                cart.price = items.options!![sp].price
                val d = db.CartIn_Up(cart)
                println("cartIn_up : " + d)
                items.qty = (qty.toString().toInt() + 1).toString()
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

    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        internal val ITEM_CONTENT_VIEW_TYPE = 1
    }

}
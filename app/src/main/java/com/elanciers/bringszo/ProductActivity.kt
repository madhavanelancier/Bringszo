package com.elanciers.bringszo

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.elanciers.booking.DataClass.SpinnerPojo
import com.elanciers.bringszo.Common.Appconstands
import com.elanciers.bringszo.Adapters.TabFragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.Utils
import com.elanciers.bringszo.DataClass.*
import com.elanciers.bringszo.retrofit.ApproveUtils
import com.elanciers.bringszo.retrofit.Resp
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.content_product.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener,Filterable {
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val maxScroll = appBarLayout!!.getTotalScrollRange()
        val percentage = Math.abs(verticalOffset).toFloat() / maxScroll.toFloat()

        if (percentage == 1f && isHideToolbarView) {
            //toolbarHeaderView.setVisibility(View.VISIBLE)
            isHideToolbarView = !isHideToolbarView
            invalidateOptionsMenu()

        } else if (percentage < 1f && !isHideToolbarView) {
            //toolbarHeaderView.setVisibility(View.GONE)
            isHideToolbarView = !isHideToolbarView
            invalidateOptionsMenu()
        }
    }
    val tag = "Product"
    val activity = this
    lateinit var utils : Utils
    lateinit var db : DBController
    lateinit var pDialog: Dialog

    private var isHideToolbarView = false
    val pro_data = ArrayList<ProductItems>()
    val Products = ArrayList<AllCat>()
    var filterProducts = ArrayList<AllCat>()
    lateinit var adapter : FragmentsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appconstands.changeStatusBarColor(activity, R.color.statusbar)
        setContentView(R.layout.activity_product)
        utils = Utils(activity)
        db = DBController(activity)
        val tool = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tool)
        val ab = supportActionBar
        ab!!.setDisplayShowHomeEnabled(true)
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.back_arrow)
        ab!!.setTitle(intent.extras.getString("name"))
        //ab!!.setSubtitle(intent.extras.getString("loc"))
        appbar.addOnOffsetChangedListener(activity)

        del.setText("for delivery")
        var options = ArrayList<SpinnerPojo>()
        var sdata = SpinnerPojo()
        sdata.name = "500 Gms"
        sdata.price = "70"
        options.add(sdata)
        sdata = SpinnerPojo()
        sdata.name = "1 Kg"
        sdata.price = "140"
        options.add(sdata)

        var AllPro = ArrayList<TitledProduct>()
        var dts = TitledProduct()
        dts.title = "Dhals"
        dts.sid = ""
        dts.name = "Thuvaram Paruppu"
        dts.qty = "0"
        dts.nm = "500 Gms"
        dts.price = "70"
        dts.selpos = 0
        dts.img = ""
        dts.options = options
        AllPro.add(dts)
        dts = TitledProduct()
        dts.title = "Dhals"
        dts.sid = ""
        dts.name = "Paasiparuppu"
        dts.qty = "0"
        dts.nm = "500 Gms"
        dts.price = "70"
        dts.selpos = 0
        dts.img = ""
        dts.options = options
        AllPro.add(dts)

        dts = TitledProduct()
        dts.title = "Dhals Im"
        dts.sid = ""
        dts.name = "Thuvaram Paruppu"
        dts.qty = "0"
        dts.nm = "500 Gms"
        dts.price = "70"
        dts.selpos = 0
        dts.img = ""
        dts.options = options
        AllPro.add(dts)
        dts = TitledProduct()
        dts.title = "Dhals Im"
        dts.sid = ""
        dts.name = "Paasiparuppu"
        dts.qty = "0"
        dts.nm = "500 Gms"
        dts.price = "70"
        dts.selpos = 0
        dts.img = ""
        dts.options = options
        AllPro.add(dts)

        dts = TitledProduct()
        dts.title = "Ex-Dhals"
        dts.sid = ""
        dts.name = "Thuvaram Paruppu"
        dts.qty = "0"
        dts.nm = "500 Gms"
        dts.price = "70"
        dts.selpos = 0
        dts.img = ""
        dts.options = options
        AllPro.add(dts)
        dts = TitledProduct()
        dts.title = "Ex-Dhals"
        dts.sid = ""
        dts.name = "Paasiparuppu"
        dts.qty = "0"
        dts.nm = "500 Gms"
        dts.price = "70"
        dts.selpos = 0
        dts.img = ""
        dts.options = options
        AllPro.add(dts)

        var fr = AllCat()
        fr.title = "Dhal"
        fr.pros = AllPro
        //Products.add(fr)

        options = ArrayList<SpinnerPojo>()
        sdata = SpinnerPojo()
        sdata.name = "500 Gms"
        sdata.price = "80"
        options.add(sdata)
        sdata = SpinnerPojo()
        sdata.name = "1 Kg"
        sdata.price = "160"
        options.add(sdata)

        AllPro = ArrayList<TitledProduct>()
        dts = TitledProduct()
        dts.title = "Flours"
        dts.sid = ""
        dts.name = "Wheat"
        dts.qty = "0"
        dts.nm = "500 Gms"
        dts.price = "80"
        dts.selpos = 0
        dts.img = ""
        dts.options = options
        AllPro.add(dts)
        dts = TitledProduct()
        dts.title = "Flours"
        dts.sid = ""
        dts.name = "Ragi"
        dts.qty = "0"
        dts.nm = ""
        dts.price = "70"
        dts.selpos = 0
        dts.img = ""
        dts.options = ArrayList<SpinnerPojo>()
        AllPro.add(dts)

        fr = AllCat()
        fr.title = "Flour"
        fr.pros = AllPro
        //Products.add(fr)
        adapter = FragmentsAdapter(supportFragmentManager,activity)

        if (viewpager != null) {
            // setupViewPager(viewPager);
            /*adapter = FragmentsAdapter(supportFragmentManager,activity)*/
            var fragment: ProductFragment
            /*for (j in 0 until AllPro.size) {
                // adapter = new Adapter(getSupportFragmentManager());
                fragment = ProductFragment(Products[j].pros!!)
                val bundle = Bundle()
                // bundle.putInt("type", 0);
                *//*bundle.putString("products", subcat_data[j].product)
                bundle.putString("sub_cat_id", subcat_data[j].catid)
                fragment.arguments = bundle*//*
                adapter.addFragment(fragment, Products[j].title.toString())
            }*/
            /*viewpager!!.adapter = adapter
            tabs.setupWithViewPager(viewpager)*/
        }

        try {
            highLightCurrentTab(0)
        }catch (e:Exception){

        }
        if (Appconstands.net_status(activity)){
            product_layout.visibility = View.GONE
            product_shimmer.visibility = View.VISIBLE
            product_shimmer.startShimmerAnimation()
            getProducts()
        }
        tabs!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewpager!!.setCurrentItem(tab.position, true)
                //tabLayout!!.getTabAt(tab.position)!!.select()
                highLightCurrentTab(tab.position)
                if (tab.position ==1){
                    //NearFragment().getNearBy()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        viewpager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

                tabs.getTabAt(position)!!.select()
                highLightCurrentTab(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        searchedit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        searchedit.setOnClickListener {
            val its = Intent(activity,ProductSearchActivity::class.java)
            startActivity(its)
        }

        /*if (searchedit.text.trim().isNotEmpty()){
                    filter!!.filter(searchedit.text.trim())
                }else{
                    adapter = FragmentsAdapter(supportFragmentManager,activity)
                    viewpager!!.adapter = adapter
                    tabs.setupWithViewPager(viewpager)
                    adapter.notifyDataSetChanged()
                    for (h in 0 until Products.size){
                        val fragment = ProductFragment(Products[h].pros!!,Products[h].title.toString(),Products[h].cid.toString())
                        adapter.addFragment(fragment,Products[h].title.toString(),Products[h].cid.toString())
                        viewpager!!.adapter = adapter
                        tabs.setupWithViewPager(viewpager)
                        println("Products[h].title.toString() : "+Products[h].title.toString())
                    }
                }*/

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; activity adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search_menu, menu)
        if (isHideToolbarView){
            menu.findItem(R.id.search).setVisible(false)
        }else{
            menu.findItem(R.id.search).setVisible(true)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()
        when(id){
            android.R.id.home -> {
                //onBackPressed()
                finish()
                return true
            }
            R.id.search -> {
                val its = Intent(activity,ProductSearchActivity::class.java)
                startActivity(its)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    private fun highLightCurrentTab(position:Int) {
        for (i in 0 until tabs.getTabCount())
        {
            val tab = tabs.getTabAt(i)
            assert(tab != null)
            tab!!.setCustomView(null)
            tab!!.setCustomView(adapter.getTabView(i))
        }
        val tab = tabs.getTabAt(position)
        assert(tab != null)
        tab!!.setCustomView(null)
        tab!!.setCustomView(adapter.getSelectedTabView(position))
    }

    class FragmentsAdapter(fm: FragmentManager,val context: Activity) : FragmentPagerAdapter(fm) {
        private val mFragments = ArrayList<Fragment>()
        private val mFragmentTitles = ArrayList<String>()
        private val mFragmentId = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String,id:String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
            mFragmentId.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            //return mFragmentTitleList.get(position);
            return null
        }
        fun getTabView(position: Int): View {
            val view = LayoutInflater.from(context).inflate(R.layout.fragment_tab, null)
            val tabTextView = view.findViewById<TextView>(R.id.textView)
            tabTextView.setText(mFragmentTitles.get(position))
            val tabImageView = view.findViewById<LinearLayout>(R.id.lin)
            tabImageView.setBackgroundResource(/*context.resources.getDrawable(*/R.drawable.product_cat_unselected)//)
            //tabImageView.setImageResource(mFragmentIconList.get(position))
            return view
        }

        fun getSelectedTabView(position: Int): View {
            val view = LayoutInflater.from(context).inflate(R.layout.fragment_tab, null)
            val tabTextView = view.findViewById<TextView>(R.id.textView)
            tabTextView.setText(mFragmentTitles.get(position))
            tabTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
            val tabImageView = view.findViewById<LinearLayout>(R.id.lin)
            tabImageView.setBackgroundResource(/*context.resources.getDrawable(*/R.drawable.product_cat_selected)//)
            //tabImageView.setImageResource(mFragmentIconList.get(position))
            /*tabImageView.setColorFilter(
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    PorterDuff.Mode.SRC_ATOP
            )*/
            return view
        }
    }

    inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val title = arrayOf("One", "Two", "Three")

        override fun getItem(position: Int): Fragment {
            return TabFragment.getInstance(position)
        }

        override fun getCount(): Int {
            return title.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return title[position]
        }
    }

    inner class TabAdapter constructor(fm: FragmentManager, private val context: Context) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int {
            return mFragmentList.size
        }

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()
        private val mFragmentIconList = ArrayList<Int>()
        /*val count: Int
            get() = mFragmentList.size
    */
        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        fun addFragment(fragment: Fragment, title: String, tabIcon: Int) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
            mFragmentIconList.add(tabIcon)
        }


        override fun getPageTitle(position: Int): CharSequence? {
            //return mFragmentTitleList.get(position);
            return null
        }
        fun getTabView(position: Int): View {
            val view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
            val tabTextView = view.findViewById<TextView>(R.id.tabTextView)
            tabTextView.setText(mFragmentTitleList.get(position))
            val tabImageView = view.findViewById<ImageView>(R.id.tabImageView)
            tabImageView.setImageResource(mFragmentIconList.get(position))
            return view
        }

        fun getSelectedTabView(position: Int): View {
            val view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
            val tabTextView = view.findViewById<TextView>(R.id.tabTextView)
            tabTextView.setText(mFragmentTitleList.get(position))
            tabTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            val tabImageView = view.findViewById<ImageView>(R.id.tabImageView)
            tabImageView.setImageResource(mFragmentIconList.get(position))
            tabImageView.setColorFilter(
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    PorterDuff.Mode.SRC_ATOP
            )
            return view
        }
    }

    fun view_cart_lay(){
        val tot = db.cartTotal.toString()
        println("db.cartTotal : "+db.cartTotal)
        if (db.cartTotal!=0.toDouble()/*&&(db.vendor_id==utils.getvendor_id())*/) {
            val vendor_nm = db.vendor_nm
            val items = db.cartItem.toString()
            cart_layout.visibility = View.VISIBLE
            tot_items.setText(items+" items - ")
            tot_price.setText("₹ "+tot)
            shop.setText("From : "+vendor_nm)
            continue_cart.setOnClickListener {
                startActivity(Intent(activity, CartActivity::class.java))
            }
        }else{
            cart_layout.visibility = View.GONE
        }

    }

    fun dis(){

    }

    override fun onResume() {
        super.onResume()
        view_cart_lay()
    }

    fun getProducts(){
        val typeid  = utils.gettypeid()
        val vendor_id  = utils.getvendor_id()
        //loading_show(activity, pDialog).show()
        println("type : "+typeid)
        println("vendor_id : "+vendor_id)
        val call = ApproveUtils.Get.getProducts(typeid.toString(),vendor_id.toString(),utils.zoneid())
        call.enqueue(object : Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Log.e("$tag response", response.toString())
                if (response.isSuccessful()) {
                    val example = response.body() as Resp
                    println(example)
                    if (example.status == "Success") {
                        val slid = response.body() as Resp
                        val res = slid.response!!
                        for (h in 0 until res.size){
                            val details = res[0].details
                            for (d in 0 until details!!.size){
                                val fr = AllCat()
                                fr.cid = details[d].sid
                                fr.title = details[d].name
                                val pro = details[d].pro
                                val sid = details[d].sid
                                val name = details[d].name
                                val AllPro = ArrayList<TitledProduct>()

                                for (p in 0 until pro!!.size){
                                    val pid = pro[p].pid
                                    val pname = pro[p].pname
                                    val image = pro[p].image
                                    val option = pro[p].option
                                    val options = ArrayList<SpinnerPojo>()
                                    for (o in 0 until option!!.size){
                                        val sdata = SpinnerPojo()
                                        val price = option[o].price
                                        val opname = option[o].name
                                        sdata.id = o.toString()
                                        sdata.name = opname
                                        sdata.price = price
                                        options.add(sdata)
                                    }
                                    val dts = TitledProduct()
                                    dts.title = name
                                    dts.sid = sid
                                    dts.pid = pid
                                    dts.name = pname
                                    dts.qty = "0"
                                    dts.nm = options[0].name
                                    dts.price = options[0].price
                                    dts.selpos = 0
                                    dts.img =image
                                    dts.options = options
                                    AllPro.add(dts)

                                }
                                val fragment = ProductFragment(AllPro,details[d].name.toString(),details[d].sid.toString())
                                adapter.addFragment(fragment, details[d].name.toString(),details[d].id.toString())
                                viewpager!!.adapter = adapter
                                tabs.setupWithViewPager(viewpager)
                                fr.pros = AllPro
                                Products.add(fr)
                            }
                        }
                    }
                }
                //pDialog.dismiss()
                product_layout.visibility = View.VISIBLE
                product_shimmer.visibility = View.GONE
                product_shimmer.stopShimmerAnimation()
            }

            override fun onFailure(call: Call<Resp>, t: Throwable) {
                Log.e("$tag Fail response", t.toString())
                if (t.toString().contains("time")) {
                    Toast.makeText(
                            activity,
                            "Poor network connection",
                            Toast.LENGTH_LONG
                    ).show()
                }
                //pDialog.dismiss()
                //loading_show(activity).dismiss()
                product_layout.visibility = View.VISIBLE
                product_shimmer.visibility = View.GONE
                product_shimmer.stopShimmerAnimation()
            }
        })
    }
    override fun getFilter(): Filter? {
        return object : Filter() {
            protected override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filterProducts = Products
                } else {
                    val filteredList = ArrayList<AllCat>()
                    for (row in Products) { // name match condition. this might differ depending on your requirement
// here we are looking for name or phone number match
                        for (nm in row.pros!!) {
                            if (nm.name!!.toLowerCase().contains(charString.toLowerCase())
                            ) {
                                filteredList.add(row)
                            }
                        }
                    }
                    filterProducts = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filterProducts
                return filterResults
            }

            protected override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                filterProducts = filterResults.values as ArrayList<AllCat>
                adapter = FragmentsAdapter(supportFragmentManager,activity)
                viewpager!!.adapter = adapter
                tabs.setupWithViewPager(viewpager)
                for (h in 0 until filterProducts.size){
                    val fragment = ProductFragment(filterProducts[h].pros!!,filterProducts[h].title.toString(),filterProducts[h].cid.toString())
                    adapter.addFragment(fragment,filterProducts[h].title.toString(),filterProducts[h].cid.toString())
                    viewpager!!.adapter = adapter
                    tabs.setupWithViewPager(viewpager)
                    println("filterProducts[h].title.toString() : "+filterProducts[h].title.toString())
                }
            }
        }
    }
}

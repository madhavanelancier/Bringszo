package com.elanciers.bringszo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.*
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.elanciers.bringszo.Common.Utils
import kotlinx.android.synthetic.main.activity_welcomescreen.*


class WelcomeScreen : AppCompatActivity() {
    val tag = "Welcome"
    val activity = this
    lateinit var utils : Utils
    lateinit var pDialog: Dialog
    private var dots: Array<TextView?> = emptyArray()
    private var layouts: IntArray? = null

    //private PrefManager prefManager;
    //internal var utils: Utils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_welcomescreen)
        utils = Utils(activity)
        if (utils.login()){
            startActivity(Intent(activity, Mpin_Activity::class.java))
            finish()
        }

        /*Handler().postDelayed(Runnable {
            if (!utils.loadFirstTime().trim().equalsIgnoreCase("")) {
                if (!utils.loaduname().trim().equalsIgnoreCase("")) {
                    startActivity(Intent(this@WelcomeScreen, JewelHome::class.java))
                } else {
                    startActivity(Intent(this@WelcomeScreen, MainScreen::class.java))
                }
            } else {
                val `in` = Intent(this@WelcomeScreen, Welcome::class.java)
                startActivity(`in`)
            }
        }, 5000)*/

        layouts = intArrayOf(R.layout.slide1,R.layout.slide3)

        // adding bottom dots
        //addBottomDots(0)
        //pageIndicatorView.setSelection(0);
        // making notification bar transparent
        changeStatusBarColor()

        val myViewPagerAdapter = MyViewPagerAdapter()
        view_pager.setAdapter(myViewPagerAdapter)
        view_pager.addOnPageChangeListener(viewPagerPageChangeListener)
        dots_indicator.setViewPager(view_pager)
        btn_skip.setOnClickListener(View.OnClickListener { launchHomeScreen() })

        btn_next.setOnClickListener(View.OnClickListener {
            // checking for last page
            // if last page home screen will be launched
            val current = getItem(+1)
            if (current < layouts!!.size) {
                // move to next screen
                view_pager.setCurrentItem(current)
            } else {
                launchHomeScreen()
            }
        })


        //pageIndicatorView.setCount(5) // specify total count of indicators
        //pageIndicatorView.setSelection(2)

    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(layouts!!.size)

        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)

        layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.setText(Html.fromHtml("&#8226;"))
            dots[i]!!.setTextSize(35f)
            dots[i]!!.setTextColor(colorsInactive[0])
            layoutDots.addView(dots[i])
        }

        if (dots.size > 0)
            dots[currentPage]!!.setTextColor(colorsActive[1])
    }

    private fun getItem(i: Int): Int {
        return view_pager.getCurrentItem() + i
    }
    internal var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                //addBottomDots(position)
                //pageIndicatorView.setSelection(position);
                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == layouts!!.size - 1) {
                    // last page. make button text to GOT IT
                    //btn_next.setText(getString(R.string.start))
                    //btn_skip.setVisibility(View.GONE)
                } else {
                    // still pages are left
                    //btn_next.setText(getString(R.string.Next))
                    //btn_skip.setVisibility(View.VISIBLE)
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

            }

            override fun onPageScrollStateChanged(arg0: Int) {

            }
        }


    inner class MyViewPagerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return layouts!!.size
        }

        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = layoutInflater!!.inflate(layouts!![position], container, false)
            container.addView(view)

            return view
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun launchHomeScreen() {
        // prefManager.setFirstTimeLaunch(false);
        //utils.savePreferences("first_time", "No")
        startActivity(Intent(this, OptionSelect_Activity::class.java))
        finish()
    }
}

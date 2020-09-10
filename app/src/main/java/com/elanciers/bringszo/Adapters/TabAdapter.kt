package com.elanciers.bringszo.Adapter

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.elanciers.bringszo.R


class TabAdapter constructor(fm: FragmentManager, private val context: Context) : FragmentStatePagerAdapter(fm) {
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
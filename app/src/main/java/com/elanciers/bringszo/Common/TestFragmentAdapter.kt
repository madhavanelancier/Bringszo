package com.elanciers.bringszo.Common

import android.content.Context
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

internal class TestFragmentAdapter(fm: FragmentManager,
                                   private val context: Context, private val content: Array<String>) : FragmentStatePagerAdapter(fm) {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return if (`object` != null) {
            (`object` as Fragment).view === view
        } else {
            false
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return PagerAdapter.POSITION_NONE
    }

    override fun getItem(position: Int): Fragment {
        return TestFragment.newInstance(content!![position],
                context)
    }

    override fun getCount(): Int {
        return content?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return content!![position]
    }

}

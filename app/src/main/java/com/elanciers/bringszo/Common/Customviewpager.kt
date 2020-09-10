package com.elanciers.bringszo.Common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager

class Customviewpager : ViewPager {
    private var swipeable = false

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    // Call this method in your motion events when you want to disable or enable
    // It should work as desired.
    fun setSwipeable(swipeable: Boolean) {
        this.swipeable = swipeable
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        return if (this.swipeable) super.onInterceptTouchEvent(arg0) else false
    }
}

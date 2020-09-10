package com.elanciers.bringszo.Common

import android.content.Context
import android.view.View

import androidx.viewpager.widget.ViewPager


internal class CustPageTransformer(context: Context) : ViewPager.PageTransformer {
    private var MAX_SCALE = 0.0f
    private var mPageMargin: Int = 0
    private var animationEnabled = true
    private var fadeEnabled = false
    private var fadeFactor = 0.5f

    var max_scale = 1.0f
    var min_scale = 0.8f

    private var smallerScale: Float = 0.toFloat()

    private val maxTranslateOffsetX: Int
    private var viewPager: ViewPager? = null

    init {
        this.maxTranslateOffsetX = dp2px(context, 280f)
        //this.smallerScale = smaller;
    }

    override fun transformPage(view: View, position: Float) {
        if (viewPager == null) {
            viewPager = view.parent as ViewPager
        }

        val leftInScreen = view.left - viewPager!!.scrollX
        val centerXInViewPager = leftInScreen + view.measuredWidth / 2
        val offsetX = centerXInViewPager - viewPager!!.measuredWidth / 2
        val offsetRate = offsetX.toFloat() * 0.38f / viewPager!!.measuredWidth
        val scaleFactor = 1 - Math.abs(offsetRate)
        //println("scaleFactor : "+scaleFactor)
        if (scaleFactor > 0) {
            view.scaleX = scaleFactor
            view.scaleY = scaleFactor
            view.translationX = -maxTranslateOffsetX * offsetRate
        }

        /*var position = position
        if (mPageMargin <= 0 || !animationEnabled)
            return
        view.setPadding(mPageMargin / 3, mPageMargin / 3, mPageMargin / 3, mPageMargin / 3)

        if (MAX_SCALE == 0.0f && position > 0.0f && position < 1.0f) {
            MAX_SCALE = position
        }
        position = position - MAX_SCALE
        val absolutePosition = Math.abs(position)
        if (position <= -1.0f || position >= 1.0f) {
            if (fadeEnabled)
                view.alpha = fadeFactor
            // Page is not visible -- stop any running animations

        } else if (position == 0.0f) {

            // Page is selected -- reset any views if necessary
            view.scaleX = 1 + MAX_SCALE
            view.scaleY = 1 + MAX_SCALE
            view.alpha = 1f
        } else {
            view.scaleX = 1 + MAX_SCALE * (1 - absolutePosition)
            view.scaleY = 1 + MAX_SCALE * (1 - absolutePosition)
            if (fadeEnabled)
                view.alpha = Math.max(fadeFactor, 1 - absolutePosition)
        }*/

        /*var position = position
        position = if (position < -1) (-1).toFloat() else position
        position = if (position > 1) 1F else position
        val tempScale = if (position < 0) 1 + position else 1 - position
        val slope = (max_scale - min_scale) / 1
        val scale_val = min_scale + tempScale * slope
        view.scaleX = scale_val
        view.scaleY = scale_val
        view.parent.requestLayout()*/

        /*val absPosition = Math.abs(position - startOffset)

        if (absPosition >= 1) {
            //view.setElevation(baseElevation)
            view.setScaleY(smallerScale)
        } else {
            // This will be during transformation
            //view.setElevation((1 - absPosition) * raisingElevation + baseElevation)
            view.setScaleY((smallerScale - 1) * absPosition + 1)
        }*/

        /*if (position < -1) {
            view.setScaleY(0.7f)
            view.setAlpha(1f)
        } else if (position <= 1) {
            val scaleFactor = Math.max(0.7f, 1 - Math.abs(position - 0.14285715f))
            view.setScaleX(scaleFactor)
            Log.e("scale", Gson().toJson(scaleFactor))
            view.setScaleY(scaleFactor)
            view.setAlpha(scaleFactor)
        } else {
            view.setScaleY(0.7f)
            view.setAlpha(1f)
        }*/
    }

    /**
     * dp和像素转换
     */
    private fun dp2px(context: Context, dipValue: Float): Int {
        val m = context.resources.displayMetrics.density
        return (dipValue * m + 0.5f).toInt()
    }

}

package com.elanciers.bringszo.Common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import androidx.annotation.RequiresApi
import com.elanciers.bringszo.R

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
fun circleReveal(context:Activity,viewID:Int, posFromRight:Int, containsOverflow:Boolean, isShow:Boolean) {
    val myView = context.findViewById<View>(viewID)
    var width = myView.getWidth()
    if (posFromRight > 0) {
        width = width - (posFromRight * context.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (context.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2)
    }
    if (containsOverflow) {
        width = width -  context.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)
    }
    val cx = width
    val cy = myView.getHeight() / 2
    val anim: Animator
    if (isShow)
        anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, width.toFloat())
    else
        anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, width.toFloat(), 0f)
    anim.setDuration(220.toLong())
    // make the view invisible when the animation is done
    anim.addListener(object: AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation:Animator) {
            if (!isShow)
            {
                super.onAnimationEnd(animation)
                myView.setVisibility(View.INVISIBLE)
            }
        }
    })
    // make the view visible and start the animation
    if (isShow)
        myView.setVisibility(View.VISIBLE)
    // start the animation
    anim.start()
}

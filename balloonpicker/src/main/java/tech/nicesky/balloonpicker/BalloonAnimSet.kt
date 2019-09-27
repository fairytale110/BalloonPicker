package tech.nicesky.balloonpicker

import android.content.Context
import android.view.animation.*
import android.view.animation.AnimationUtils.loadAnimation

class BalloonAnimSet{

    companion object{
        fun create(show: Boolean, fx : Float, tx : Float, fy : Float, ty : Float, context : Context, listener: Animation.AnimationListener): AnimationSet{
            val animSet = AnimationSet(true)
            animSet.duration = 300
            animSet.repeatCount = 0
            animSet.fillAfter = false
            animSet.fillBefore = show

            val trans = TranslateAnimation(fx,tx,fy,ty)

            val scale = if (show) loadAnimation(context, R.anim.balloon_scale_enter) else  loadAnimation(context, R.anim.balloon_scale_exit)

            val alpha = if (show) AlphaAnimation(0F,1F) else AlphaAnimation(0.8F,0F)

            animSet.addAnimation(alpha)
            animSet.addAnimation(scale)
            animSet.addAnimation(trans)
            animSet.setAnimationListener(listener)
            return animSet
        }
    }
}
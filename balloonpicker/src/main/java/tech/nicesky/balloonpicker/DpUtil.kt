package tech.nicesky.balloonpicker

import android.content.res.Resources

class DpUtil {

    companion object{
        fun dp2px(float: Float): Float{
            return  0.5f + float * Resources.getSystem().displayMetrics.density
        }

        fun px2dp(float: Float): Float{
            val scale = Resources.getSystem().displayMetrics.density
            return (float / scale + 0.5f)
        }
    }
}
package tech.nicesky.balloonpicker

import android.animation.Animator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AnimationSet
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

class BalloonView : View {
    private val TAG by lazy { BalloonView :: class.java.simpleName }

    private var paintOfBalloon = Paint()
    private var paintOfValue   = Paint()
    internal var colorOFValue  = "#FFFFFF".toColorInt()
        set(value) {
            field = value
            paintOfValue.color = colorOFValue
            postInvalidate()
        }
    internal var colorOFBalloon = "#512DA8".toColorInt()
        set(value) {
            field = value
            paintOfBalloon.color = colorOFBalloon
            img?.setTint(colorOFBalloon)
            bitmapBalloon = getBitmapFromVectorDrawable(context, img)
            postInvalidate()
        }
    internal var valueOfBalloon = 0L
        set(value) {
            field = value
            postInvalidate()
        }

    private var img : VectorDrawableCompat? = null
    private var bitmapBalloon : Bitmap = Bitmap.createBitmap(1,
        1, Bitmap.Config.ARGB_8888)

    constructor(context: Context?) : super(context){
        configure()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){}

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        configure()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes){
        configure()
    }

    private fun configure(){
        paintOfBalloon.style = Paint.Style.FILL_AND_STROKE
        paintOfBalloon.color = colorOFBalloon

        paintOfValue.style = Paint.Style.FILL_AND_STROKE
        paintOfValue.color = colorOFValue
        paintOfValue.textSize = DpUtil.dp2px(20F)
        paintOfValue.textAlign = Paint.Align.CENTER
        img = VectorDrawableCompat.create(context.resources, R.drawable.ic_wb_cloudy_black_24dp, null)
        img?.setTint(colorOFBalloon)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //L("onMeasure")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //L("onLayout")
        post {
            bitmapBalloon = getBitmapFromVectorDrawable(context, img)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
     //   L("onSizeChanged")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (bitmapBalloon != null){
            canvas?.drawBitmap(bitmapBalloon, 0F, 0F, paintOfBalloon)
        }
        canvas?.drawText(valueOfBalloon.toString(), width/2F , height/2F, paintOfValue)
    }

    private fun L(log : String){
        Log.w(TAG, log)
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawable: VectorDrawableCompat?): Bitmap {
        val widthOfBalloon  = if (width <= 0) 1 else width
        val heightOfBalloon  = if (height <= 0) 1 else height
        val bitmap = Bitmap.createBitmap(widthOfBalloon,
            heightOfBalloon, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }

}
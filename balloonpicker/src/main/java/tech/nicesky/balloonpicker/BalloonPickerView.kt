package tech.nicesky.balloonpicker

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.*
import android.view.animation.Animation.AnimationListener
import androidx.core.animation.doOnEnd
import androidx.core.graphics.toColorInt
import androidx.core.text.TextUtilsCompat
import tech.nicesky.balloonpicker.BalloonPickerListener
import kotlin.math.*
import android.graphics.Typeface
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.name



class BalloonPickerView : ViewGroup, TrackLayerListener, ValueAnimator.AnimatorUpdateListener {

    private val TAG = BalloonPickerView::class.java.simpleName
    private var balloon : BalloonView? = null
    private var trackLayer : TrackLayerView? = null
    private var balloonAnim = ValueAnimator.ofInt(0, 1)
    var valueListener : BalloonPickerListener? = null

    private var duration = 200L
    private var pointThumb  = PointF(0F,0F)
    private var centerOfBalloon = PointF()
    private var lengthOFTrackLayer = 0F
    private var distanceVerticalBetweenBalloonAndTrackLayer = 0F

    private var paintOfTempLine = Paint()
    private var paintOfDesc        = Paint()
    private var paintOfValue       = Paint()
    private val maxScale = 0.66F
    private var balloonDefaultY = 0F
    private var balloonDefaultX = 0F
    private var balloonWidthDefault = DpUtil.dp2px(50F)
    private var balloonHeightDefault = DpUtil.dp2px(70F)
    public var colorOfDesc = "#000000".toColorInt()
        set(value) {
            field = value
            paintOfDesc.color  = colorOfDesc
            postInvalidate()
        }

     public var colorOfValue = "#000000".toColorInt()
        set(value) {
            field = value
            paintOfValue.color  = colorOfValue
            postInvalidate()
        }

    public var desc = "Quantity"
        set(value) {
            field = value
            postInvalidate()
        }

    private val listenerEnter = object : AnimationListener{
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            balloon?.clearAnimation()
            balloon?.visibility = View.VISIBLE
        }
    }

    private val listenerExit = object : AnimationListener{
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            balloon?.visibility = View.INVISIBLE
            balloon?.clearAnimation()
        }
    }

    constructor(context: Context?) : super(context){
        configure()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        configure()
    }

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
        paintOfTempLine.color = "#BDBDBD".toColorInt()
        paintOfTempLine.style = Paint.Style.FILL_AND_STROKE
        paintOfTempLine.isAntiAlias = true
        paintOfTempLine.strokeWidth = DpUtil.dp2px(3F)

        val font = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paintOfDesc.isAntiAlias = true
        paintOfDesc.textSize = DpUtil.dp2px(15F)
        paintOfDesc.color     =  colorOfDesc
        paintOfDesc.textAlign = Paint.Align.LEFT
        paintOfDesc.typeface = font

        paintOfValue.isAntiAlias = true
        paintOfValue.textSize = DpUtil.dp2px(15F)
        paintOfValue.color     =  colorOfValue
        paintOfValue.textAlign = Paint.Align.RIGHT
        paintOfValue.typeface = font

        setBackgroundColor("#00FFFFFF".toColorInt())
        balloon = BalloonView(context)
        trackLayer = TrackLayerView(context)
        trackLayer?.listener = this

        trackLayer?.layoutParams = LayoutParams(width,DpUtil.dp2px(100F).toInt())
        balloon?.layoutParams = LayoutParams(balloonWidthDefault.toInt(), balloonHeightDefault.toInt())
        balloon?.visibility = View.INVISIBLE

        addView(balloon)
        addView(trackLayer)
        initAnimation(ValueAnimator())

    }

    private fun initAnimation(anim: ValueAnimator){
        balloonAnim = anim
        balloonAnim.interpolator = LinearInterpolator()
        balloonAnim.repeatCount = 0
        balloonAnim.duration = duration
        balloonAnim.addUpdateListener(this)
        balloonAnim.doOnEnd {
            initAnimation(ValueAnimator.ofInt(centerOfBalloon.x.toInt(), pointThumb.x.toInt()))
            moveBalloonWithAnim()
        }
    }

    private fun moveBalloon() {
        val b = pointThumb.x - centerOfBalloon.x
        moveBalloonWithAnim()
    }

    private fun moveBalloonWithAnim(){
        if (balloonAnim != null && balloonAnim.isRunning){
           return
        }
        if(balloonAnim != null && pointThumb.x.toInt() == centerOfBalloon.x.toInt()){
             return
        }
        initAnimation(ValueAnimator.ofInt(centerOfBalloon.x.toInt(), pointThumb.x.toInt()))
        balloonAnim.start()
    }

    private fun resetBalloonRotationAngleMax(){
        lengthOFTrackLayer =  width.toFloat() - trackLayer?.getPadding()!! * 2F
        balloon?.rotation = 0F
    }

    private fun rotateBalloon(){
        val b = pointThumb.x - centerOfBalloon.x
        val angleRoTan = -atan(b/distanceVerticalBetweenBalloonAndTrackLayer) / PI  * 180F
        balloon?.rotation = angleRoTan.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val size   = MeasureSpec.getSize(widthMeasureSpec)
        return when(mode){
           MeasureSpec.UNSPECIFIED -> DpUtil.dp2px(360F).toInt()
           else -> if (size < DpUtil.dp2px(100F))  DpUtil.dp2px(100F).toInt() else size
        }
    }
    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val size   = MeasureSpec.getSize(heightMeasureSpec)
        return DpUtil.dp2px(220F).toInt()
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //L("onLayout  trackLayer height: ${trackLayer?.layoutParams!!.height} ")
        balloon?.layout(trackLayer?.getPadding()!!.toInt() - balloon?.layoutParams!!.width/2, measuredHeight - trackLayer?.maxThumbRadius()!!.toInt()*4 -  balloon?.layoutParams!!.height ,  trackLayer?.getPadding()!!.toInt() + balloon?.layoutParams!!.width/2, measuredHeight - trackLayer?.maxThumbRadius()!!.toInt()*4)
        trackLayer?.layout(0, measuredHeight - trackLayer?.layoutParams!!.height, measuredWidth, measuredHeight)
        centerOfBalloon.set(balloon?.x!! + balloon?.layoutParams!!.width/2F, balloon?.y!! + balloon?.layoutParams!!.height/2)

        distanceVerticalBetweenBalloonAndTrackLayer = trackLayer?.maxThumbRadius()!!*3F + balloon?.layoutParams!!.height/2F

        balloonDefaultX = balloon?.x!!
        balloonDefaultY = balloon?.y!!
        yOfValueForDraw = trackLayer?.maxThumbRadius()!!*5.2F
        resetBalloonRotationAngleMax()
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        val disScaleHeight =maxScale *  balloonHeightDefault * (trackLayer?.selectedValue()!! - trackLayer?.minValue()!!) / (trackLayer?.maxValue()!! - trackLayer?.minValue()!!)
        val disScaleWidth =maxScale *  balloonWidthDefault/2 * (trackLayer?.selectedValue()!! - trackLayer?.minValue()!!) / (trackLayer?.maxValue()!! - trackLayer?.minValue()!!)
        val xOfBalloon =animation?.animatedValue.toString().toInt() - (balloonWidthDefault / 2F).toInt() - disScaleWidth.toInt()

        balloon?.layout(xOfBalloon, balloonDefaultY.toInt() - disScaleHeight.toInt(), xOfBalloon + balloonWidthDefault.toInt() +disScaleWidth.toInt()*2 , (balloonDefaultY + balloonHeightDefault).toInt())
        centerOfBalloon.set(xOfBalloon.toFloat() + balloonWidthDefault/2 +disScaleWidth.toInt(), balloonDefaultY + balloonHeightDefault/2 - disScaleHeight/2)
        rotateBalloon()
    }

    override fun layerTouchedDown() {
        balloon?.startAnimation(BalloonAnimSet.create(true, 0F, 0F, pointThumb.y - balloon?.y!! - trackLayer?.maxThumbRadius()!! * 2, 0F, context , listenerEnter))
    }

    override fun layerTouchedUp() {
        balloon?.visibility = View.INVISIBLE
        pointThumb.set(trackLayer?.centerPoint()!!.x, height.toFloat() - trackLayer?.getPadding()!!)
        initAnimation(ValueAnimator.ofInt(centerOfBalloon.x.toInt(), pointThumb.x.toInt()))
        moveBalloon()
        balloon?.startAnimation(BalloonAnimSet.create(false, 0F, 0F, 0F, pointThumb.y - balloon?.y!!, context , listenerExit))
        valueListener?.changed(trackLayer?.selectedValue()!!)
    }

    override fun layerTouchedMoving(value: Long, pointAtLayer: PointF) {
        pointThumb.set(pointAtLayer.x, height.toFloat() - trackLayer?.getPadding()!!)
        moveBalloon()
        balloon?.valueOfBalloon = value
    }

    private var yOfValueForDraw = 0F;
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!TextUtils.isEmpty(desc))
            canvas?.drawText(desc, trackLayer?.getPadding()!!, yOfValueForDraw,  paintOfDesc)

        if (!trackLayer?.increase!!)
            canvas?.drawText(getValue().toString(), width - trackLayer?.getPadding()!!, yOfValueForDraw,  paintOfValue)
    }

////     Draw this for DEV
//    override fun dispatchDraw(canvas: Canvas?) {
//        super.dispatchDraw(canvas)
//        if (pointThumb.x != 0F) {
//            canvas?.drawLine(centerOfBalloon.x, centerOfBalloon.y,
//                pointThumb.x, pointThumb.y, paintOfTempLine)
//            canvas?.drawLine(0F, balloonDefaultY + balloonHeightDefault,
//                measuredWidth.toFloat(), balloonDefaultY + balloonHeightDefault, paintOfTempLine)
//        }
//    }

    private fun L(log : String){
        Log.w(TAG, log)
    }

    fun layerValues(defaultValue: Long, maxValue: Long, minValue: Long){
        trackLayer?.values(defaultValue, minValue, maxValue)
    }

    fun defaultValue(defaultValue: Long){
        trackLayer?.defaultValue(defaultValue)
    }

    fun setColorForLayer(colorSelected : Int, colorUnselected : Int){
        trackLayer?.colorOfSelected = colorSelected
        trackLayer?.colorOfUnSelected = colorUnselected
    }

    fun setColorFoThumb(colorInner: Int, colorOuter : Int){
        trackLayer?.colorOFThumb = colorInner
        trackLayer?.colorOFThumbStroke = colorOuter
    }

    fun setColorForBalloon(colorForBalloon: Int){
        balloon?.colorOFBalloon = colorForBalloon
    }

    fun setColorForBalloonValue(colorForValue: Int){
        balloon?.colorOFValue = colorForValue
    }

    fun getValue(): Long{
        return trackLayer?.selectedValue()!!
    }
}
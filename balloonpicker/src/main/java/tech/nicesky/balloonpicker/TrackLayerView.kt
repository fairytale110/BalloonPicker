package tech.nicesky.balloonpicker

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.graphics.toColorInt
import tech.nicesky.balloonpicker.TrackLayerListener

class TrackLayerView : View {

    private val TAG : String by lazy { TrackLayerView::class.java.simpleName }

    private var paintOfValueSelected = Paint()
    private var paintOfTrackLayerSelected = Paint()
    private var paintOfTrackLayerUnSelected= Paint()
    private var paintOfThumb = Paint()
    private var paintOfThumbStroke = Paint()

    /**当前位置*/
    private var pointOfThumb = PointF(-1F,-1F)
    /**出师位置*/
    private var pointOfThumbDefault = PointF(-1F, -1F)
    private var pointOfThumbTemp = PointF(0F,0F)

    private var pointOfTouchDown= PointF(0F,0F)

    private var thumbOuterCircleRadiusMax : Float = DpUtil.dp2px(25.toFloat())
    private var thumbOuterCircleRadiusDefault : Float =  DpUtil.dp2px(15.toFloat())
    private var thumbOuterCircleRadius : Float = thumbOuterCircleRadiusDefault
    private var thumbOuterCircleRadiusTemp : Float = thumbOuterCircleRadius

    private var thumbInnerCircleRadiusDefault : Float = DpUtil.dp2px(7.5.toFloat())
    private var thumbInnerCircleRadiusMax : Float = thumbOuterCircleRadiusMax - DpUtil.dp2px(0.8F)
    private var thumbInnerCircleRadius : Float = thumbInnerCircleRadiusDefault
    private var thumbInnerCircleRadiusTemp : Float = thumbInnerCircleRadius

    private var yOfTrackLayer : Float = 0F
    private var padding : Float = DpUtil.dp2px(5F)
    private var widthOfView : Float =  DpUtil.dp2px(10F)
    private var heightOfView : Float =  DpUtil.dp2px(10F)
    private var xOfTrackLayerStart = 0F
    private var xOfTrackLayerEnd = 0F
    private var xOfValue = 0F
    private var yOfValue = 0F

    private var anim : ValueAnimator = ValueAnimator.ofInt(0,1)

    private val duration = 200L
    internal var increase : Boolean = false
    private var touchWithinRange : Boolean = false
    internal var colorOFThumb = "#FFFFFF".toColorInt()
        set(value) {
            field = value
            paintOfThumb.color = colorOFThumb
            postInvalidate()
        }
    internal var colorOFThumbStroke = "#512DA8".toColorInt()
        set(value) {
            field = value
            paintOfThumbStroke.color = colorOFThumbStroke
            postInvalidate()
        }

    internal var colorOfSelected = "#512DA8".toColorInt()
        set(value) {
            field = value
            paintOfTrackLayerSelected.color = colorOfSelected
            postInvalidate()
        }

    internal var colorOfUnSelected = "#BDBDBD".toColorInt()
        set(value) {
            field = value
            paintOfTrackLayerUnSelected.color = colorOfUnSelected
            postInvalidate()
        }

    var listener : TrackLayerListener? = null
        set(value) {
            requireNotNull(value) { "listener cant be null" }
            field = value
        }

    private var minValue : Long = 0
    private var maxValue : Long = 100
    private var selectedValue : Long = minValue

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

    private fun configure(){
        //setBackgroundColor(Color.YELLOW)
        paintOfTrackLayerSelected.strokeWidth = DpUtil.dp2px(1.toFloat())
        paintOfTrackLayerSelected.color = colorOfSelected
        paintOfTrackLayerSelected.isAntiAlias = true
        paintOfTrackLayerSelected.style = Paint.Style.FILL_AND_STROKE

        paintOfTrackLayerUnSelected.strokeWidth = DpUtil.dp2px(1.toFloat())
        paintOfTrackLayerUnSelected.color = colorOfUnSelected
        paintOfTrackLayerUnSelected.isAntiAlias = true
        paintOfTrackLayerUnSelected.style = Paint.Style.FILL_AND_STROKE

        paintOfThumb.color = colorOFThumb
        paintOfThumb.isAntiAlias = true
        paintOfThumb.style = Paint.Style.FILL

        paintOfThumbStroke.color = colorOFThumbStroke
        paintOfThumbStroke.isAntiAlias = true
        paintOfThumbStroke.style = Paint.Style.FILL

        paintOfValueSelected.color = Color.BLACK
        paintOfValueSelected.isAntiAlias = true
        paintOfValueSelected.textSize = DpUtil.dp2px(13F)

        pointOfThumb.set(0.toFloat(), 0.toFloat())

        anim.interpolator = LinearInterpolator()
        anim.repeatCount = 0
        anim.duration = duration
        anim.addUpdateListener { animation ->
            val tiem = if (animation.currentPlayTime > animation.duration) animation.duration else animation.currentPlayTime
            //Log.w(TAG, "now $tiem")
            if (increase && thumbInnerCircleRadius < thumbInnerCircleRadiusMax){
                thumbInnerCircleRadius = thumbInnerCircleRadiusTemp +  (thumbInnerCircleRadiusMax - thumbInnerCircleRadiusTemp ) * tiem.toFloat() /animation.duration.toFloat()
            }else if (!increase && thumbInnerCircleRadius > thumbInnerCircleRadiusDefault){
                thumbInnerCircleRadius = thumbInnerCircleRadiusTemp - ( thumbInnerCircleRadiusTemp - thumbInnerCircleRadiusDefault  ) * tiem.toFloat() /animation.duration.toFloat()
            }

            if (increase && thumbOuterCircleRadius < thumbOuterCircleRadiusMax){
                thumbOuterCircleRadius = thumbOuterCircleRadiusTemp +  (thumbOuterCircleRadiusMax - thumbOuterCircleRadiusTemp ) * tiem.toFloat() /animation.duration.toFloat()
            }else if (!increase && thumbOuterCircleRadius > thumbOuterCircleRadiusDefault){
                thumbOuterCircleRadius = thumbOuterCircleRadiusTemp - ( thumbOuterCircleRadiusTemp - thumbOuterCircleRadiusDefault  ) * tiem.toFloat() /animation.duration.toFloat()
            }
            postInvalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.w(TAG, "onMeasure")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
       // Log.w(TAG, "onSizeChanged")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
         Log.w(TAG, "onLayout")
        widthOfView = width.toFloat()
        heightOfView = height.toFloat()

        xOfTrackLayerStart = thumbOuterCircleRadiusMax + padding
        xOfTrackLayerEnd  = widthOfView - xOfTrackLayerStart
        val xNew = xOfTrackLayerStart + ( (selectedValue - minValue).toFloat()/ (maxValue - minValue).toFloat() )* (widthOfView - 2 * thumbOuterCircleRadiusMax - 2 * padding)
        yOfTrackLayer = height.toFloat() - thumbOuterCircleRadiusMax - padding

        pointOfThumbDefault.set(if (xNew < (xOfTrackLayerStart)) xOfTrackLayerStart else xNew, yOfTrackLayer)
        pointOfThumb = pointOfThumbDefault
        pointOfThumbTemp = pointOfThumb

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawLine(xOfTrackLayerStart, yOfTrackLayer ,pointOfThumb.x,  yOfTrackLayer, paintOfTrackLayerSelected)
        canvas?.drawLine(pointOfThumb.x, yOfTrackLayer ,xOfTrackLayerEnd,  yOfTrackLayer, paintOfTrackLayerUnSelected)
        canvas?.drawCircle(pointOfThumb.x, pointOfThumb.y, thumbOuterCircleRadius,  paintOfThumbStroke)
        canvas?.drawCircle(pointOfThumb.x, pointOfThumb.y, thumbInnerCircleRadius,  paintOfThumb)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                if (event.x < pointOfThumb.x + DpUtil.dp2px(50F) && event.x > pointOfThumb.x - DpUtil.dp2px(50F)
                    && event.y < pointOfThumb.y + DpUtil.dp2px(50F) && event.y > pointOfThumb.y - DpUtil.dp2px(50F)
                ){
                  //  Log.w(TAG, "in side")
                    //动画变大内圆paintOfThumb 的 radius半径
                    touchWithinRange = true
                    pointOfTouchDown = PointF(event.x, event.y)
                    pointOfThumbTemp = pointOfThumb
                    scalingInnerCircleRadius(true)
                    listener?.layerTouchedDown()
                }else{
                    touchWithinRange = false
                }
                true
            }
            MotionEvent.ACTION_UP ->{
                //Log.d("onTouchEvent", "UP")
                    //动画变大内圆paintOfThumb 的 radius半径

                xOfValue = xOfTrackLayerEnd - selectedValue.toString().length.toFloat() * paintOfValueSelected.textSize
                yOfValue = yOfTrackLayer - thumbOuterCircleRadiusMax - padding
                scalingInnerCircleRadius(false)
                if (touchWithinRange){
                    listener?.layerTouchedUp()
                }
                true
            }
            MotionEvent.ACTION_MOVE->{
                //Log.d("onTouchEvent", "MOVE")

                //TODO  Refining logic
                if (touchWithinRange){
                    val x = pointOfThumbTemp.x + event.x - pointOfTouchDown.x
                    pointOfThumb = PointF( if (x > xOfTrackLayerEnd) xOfTrackLayerEnd  else ( if (x < xOfTrackLayerStart) xOfTrackLayerStart else x), pointOfThumbTemp.y)
                    selectedValue = (this.minValue.toFloat() + (this.maxValue.toFloat() - this.minValue.toFloat()) * (pointOfThumb.x - xOfTrackLayerStart) / (widthOfView - 2 * xOfTrackLayerStart)).toLong()
                    postInvalidate()
                    //Log.w(TAG, "move speed is $xv")
                    listener?.layerTouchedMoving(selectedValue, PointF(pointOfThumb.x, pointOfThumb.y))
                }
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun scalingInnerCircleRadius(increase: Boolean){
        this.increase = increase
        this.thumbInnerCircleRadiusTemp = thumbInnerCircleRadius
        this.thumbOuterCircleRadiusTemp = thumbOuterCircleRadius
        if (anim.isRunning){
            anim.end()
            val remainingTime: Long = when {
                this.increase -> (duration* (thumbInnerCircleRadiusMax - thumbInnerCircleRadiusTemp)/(thumbInnerCircleRadiusMax - thumbInnerCircleRadiusDefault)).toLong()
                else -> (duration* (thumbInnerCircleRadiusTemp - thumbInnerCircleRadiusDefault)/(thumbInnerCircleRadiusMax - thumbInnerCircleRadiusDefault)).toLong()
            }
            //Log.w(TAG, "Last time is $remainingTime")
            anim.duration = if (remainingTime < 0) 5 else remainingTime
        }
        anim.duration = duration
        anim.start()
    }

    private fun refreshView(){
        post {
            val xNew = xOfTrackLayerStart + ( (selectedValue - minValue).toFloat()/ (maxValue - minValue).toFloat() )* (widthOfView - 2 * thumbOuterCircleRadiusMax - 2 * padding)
            pointOfThumb = PointF( if (xNew < (xOfTrackLayerStart)) xOfTrackLayerStart else xNew, yOfTrackLayer)
            invalidate()
            listener?.layerTouchedMoving(selectedValue, PointF(pointOfThumb.x, pointOfThumb.y))
        }
    }

    fun values(defaultValue : Long, minValue : Long, maxValue : Long){
        require(maxValue > minValue && defaultValue >= minValue && defaultValue <= maxValue) { "Illegal values" }
        this.minValue = minValue
        this.maxValue = maxValue
        this.selectedValue = defaultValue

        refreshView()
    }

    fun defaultValue(value : Long){
        require(maxValue > minValue && value >= minValue && value <= maxValue) { "Illegal values" }
        this.selectedValue = value
        refreshView()
    }

    fun getPadding(): Float{
        return xOfTrackLayerStart
    }

    fun centerPoint(): PointF{
        return pointOfThumb
    }

    fun maxThumbRadius(): Float{
        return thumbOuterCircleRadiusMax
    }

    fun maxValue(): Long{
        return maxValue
    }

    fun minValue(): Long{
        return minValue
    }

    fun selectedValue(): Long{
        return selectedValue
    }

}
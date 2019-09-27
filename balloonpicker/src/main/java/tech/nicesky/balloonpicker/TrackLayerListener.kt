package tech.nicesky.balloonpicker

import android.graphics.PointF

interface TrackLayerListener {
   fun layerTouchedDown()
   fun layerTouchedUp()
   fun layerTouchedMoving(value : Long, pointAtLayer : PointF)
}
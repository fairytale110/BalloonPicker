package tech.nicesky.balloonpickerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.graphics.toColorInt
import kotlinx.android.synthetic.main.activity_main.*
import tech.nicesky.balloonpicker.BalloonPickerListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        balloon_picker.layerValues(10, 50, 5)
        balloon_picker.setColorFoThumb("#FFFFFF".toColorInt(), "#512DA8".toColorInt())
        balloon_picker.setColorForLayer("#512DA8".toColorInt(), "#BDBDBD".toColorInt())
        balloon_picker.setColorForBalloon("#512DA8".toColorInt())
        balloon_picker.setColorForBalloonValue("#FFFFFF".toColorInt())
        balloon_picker.colorOfDesc = "#000000".toColorInt()
        balloon_picker.colorOfValue = "#000000".toColorInt()
        balloon_picker.desc = "Quantity"
        balloon_picker.valueListener = object : BalloonPickerListener {
            override fun changed(value: Long) {
                Log.w("MainActivity","value: $value")
            }
        }

        Handler().postDelayed({
            balloon_picker.defaultValue(30)
        },3000)
        //val valueSelected = balloon_picker.getValue()
    }
}

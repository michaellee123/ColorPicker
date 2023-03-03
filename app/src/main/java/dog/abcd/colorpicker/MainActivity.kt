package dog.abcd.colorpicker

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        immersionBar {
            fitsSystemWindows(false)
            transparentStatusBar()
            transparentNavigationBar()
        }
        val huePicker = findViewById<ColorHuePicker>(R.id.huePicker)
        val svPicker = findViewById<ColorSVPicker>(R.id.svPicker)
        val tvColor = findViewById<TextView>(R.id.tvColor)
        huePicker.pickerSize = resources.displayMetrics.density * 15
        huePicker.hueHeight = resources.displayMetrics.density * 10
        huePicker.round = resources.displayMetrics.density * 5
        huePicker.maxHue = 310
        huePicker.postInvalidate()
        svPicker.pickerSize = resources.displayMetrics.density * 18
        svPicker.round = resources.displayMetrics.density * 12
        svPicker.postInvalidate()
        huePicker.onHueChange { hue, byUser, done ->
            svPicker.setColorH(hue)
        }
        val container = findViewById<View>(R.id.container)
        svPicker.onColorPick { color, byUser, done ->
            val hsvColor = HSVColor.valueOf(color)
            tvColor.text = hsvColor.toString()
            container.setBackgroundColor(color)
            val isDark = hsvColor.s < 0.3f && hsvColor.v > 0.7f
            immersionBar {
                statusBarDarkFont(isDark)
                navigationBarDarkIcon(isDark)
            }
        }
        val color = HSVColor.parseColor("#762ddf")
        huePicker.setColorH(color.h)
        svPicker.setColor(color.color)
    }
}
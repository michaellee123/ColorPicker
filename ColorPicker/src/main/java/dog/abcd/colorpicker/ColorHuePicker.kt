package dog.abcd.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.Integer.max
import java.lang.Integer.min

class ColorHuePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paintHue = Paint()

    var paintPicker = Paint()
    private val paintShadow = Paint()

    //include the shadow size
    var pickerSize = (resources.displayMetrics.density * 22) / 2

    var shadowSize = (resources.displayMetrics.density * 2)

    private var pickerY = 0f

    private var hueRect = RectF()

    var hueHeight = resources.displayMetrics.density * 8

    var round = resources.displayMetrics.density * 4

    var maxHue = 359

    private var hue = 0
        set(value) {
            field = max(0, min(maxHue, value))
            postInvalidate()
        }

    fun setColorH(hue: Int) {
        this.hue = hue
        hueChangedCallback?.invoke(hue, false, false)
    }

    fun getColorH() = hue

    private var huePercent: Float = 0.0f
        get() {
            return hue.toFloat() / maxHue
        }
        set(value) {
            field = 0f.coerceAtLeast(1.0f.coerceAtMost(value))
            hue = (maxHue * field).toInt()
        }

    init {
        paintPicker.color = Color.WHITE
        paintPicker.isAntiAlias = true
        paintShadow.isAntiAlias = true
        paintHue.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hueRect = RectF(pickerSize, (h - hueHeight) / 2, w - pickerSize, (h + hueHeight) / 2)
        pickerY = h.toFloat() / 2
        paintHue.shader = LinearGradient(
            0f,
            0f,
            w.toFloat(),
            0f,
            intArrayOf(
                Color.HSVToColor(floatArrayOf(0f, 1f, 1f)),
                Color.HSVToColor(floatArrayOf(60f, 1f, 1f)),
                Color.HSVToColor(floatArrayOf(120f, 1f, 1f)),
                Color.HSVToColor(floatArrayOf(180f, 1f, 1f)),
                Color.HSVToColor(floatArrayOf(240f, 1f, 1f)),
                Color.HSVToColor(floatArrayOf(300f, 1f, 1f)),
                Color.HSVToColor(floatArrayOf(maxHue.toFloat(), 1f, 1f))
            ),
            null,
            Shader.TileMode.CLAMP
        )
    }

    var shadowColorList = intArrayOf(Color.parseColor("#CC000000"), Color.TRANSPARENT)

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.drawRoundRect(hueRect, round, round, paintHue)


        paintShadow.shader = RadialGradient(
            pickerSize + huePercent * (hueRect.right - pickerSize), pickerY, pickerSize,
            shadowColorList, null, Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            pickerSize + huePercent * (hueRect.right - pickerSize),
            pickerY,
            pickerSize,
            paintShadow
        )

        canvas.drawCircle(
            pickerSize + huePercent * (hueRect.right - pickerSize),
            pickerY,
            pickerSize - shadowSize,
            paintPicker
        )
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                huePercent = event.x / hueRect.right
                hueChangedCallback?.invoke(hue, true, false)
                postInvalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                hueChangedCallback?.invoke(hue, true, true)
            }
        }
        return true
    }

    private var hueChangedCallback: ((hue: Int, byUser: Boolean, done: Boolean) -> Unit)? = null

    fun onHueChange(callback: (hue: Int, byUser: Boolean, done: Boolean) -> Unit) {
        hueChangedCallback = callback
    }
}
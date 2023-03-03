package dog.abcd.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.Float.min
import kotlin.math.max

class ColorSVPicker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paintSV = Paint()
    var paintPicker = Paint()
    var paintBorder = Paint()

    private val paintShadow = Paint()
    private var svRect = RectF()

    var round = (resources.displayMetrics.density * 10)

    //include both the shadow and border size
    var pickerSize = (resources.displayMetrics.density * 24) / 2

    var shadowSize = (resources.displayMetrics.density * 2)

    var pickerBorderSize = (resources.displayMetrics.density * 3)

    fun getColorS(): Float = s
    fun getColorV(): Float = v
    fun getColorH(): Int = hue

    fun getHSVColor(): HSVColor {
        return HSVColor.fromSVPicker(this)
    }

    fun setColorH(hue: Int) {
        setColor(Color.HSVToColor(floatArrayOf(hue.toFloat(), getColorS(), getColorV())))
    }

    fun setColor(color: Int) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hue = hsv[0].toInt()
        s = hsv[1]
        v = hsv[2]
        colorPickCallback?.invoke(color, false, false)
    }

    private var hue = 0
        set(value) {
            field = value
            postInvalidate()
        }

    private var s = 1f
        set(value) {
            field = max(0f, min(1f, value))
            postInvalidate()
        }

    private var v = 1f
        set(value) {
            field = max(0f, min(1f, value))
            postInvalidate()
        }

    init {
        paintBorder.color = Color.WHITE
        paintPicker.isAntiAlias = true
        paintSV.isAntiAlias = true
        paintBorder.isAntiAlias = true
        paintShadow.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        svRect = RectF(pickerSize, pickerSize, w - pickerSize, h - pickerSize)
    }

    private val shadowColorList = intArrayOf(Color.parseColor("#CC000000"), Color.TRANSPARENT)

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        canvas.save()
        paintSV.shader = getSvShader()
        canvas.drawRoundRect(svRect, round, round, paintSV)

        paintShadow.shader = RadialGradient(
            pickerSize + (svRect.right - pickerSize) * s,
            pickerSize + (svRect.bottom - pickerSize) * (1 - v),
            pickerSize,
            shadowColorList, null, Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            pickerSize + (svRect.right - pickerSize) * s,
            pickerSize + (svRect.bottom - pickerSize) * (1 - v),
            pickerSize,
            paintShadow
        )

        canvas.drawCircle(
            pickerSize + (svRect.right - pickerSize) * s,
            pickerSize + (svRect.bottom - pickerSize) * (1 - v),
            pickerSize - shadowSize,
            paintBorder
        )

        paintPicker.color = Color.HSVToColor(floatArrayOf(hue.toFloat(), s, v))

        canvas.drawCircle(
            pickerSize + (svRect.right - pickerSize) * s,
            pickerSize + (svRect.bottom - pickerSize) * (1 - v),
            pickerSize - pickerBorderSize - shadowSize,
            paintPicker
        )

        canvas.restore()
    }

    private fun getSvShader(): ComposeShader {
        val vShader = LinearGradient(
            svRect.left,
            svRect.top,
            svRect.left,
            svRect.bottom,
            intArrayOf(Color.WHITE, Color.BLACK),
            null,
            Shader.TileMode.CLAMP
        )
        val sShader = LinearGradient(
            svRect.left, svRect.top, svRect.right, svRect.top, intArrayOf(
                Color.WHITE, Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 1f))
            ), null, Shader.TileMode.CLAMP
        )
        return ComposeShader(vShader, sShader, PorterDuff.Mode.MULTIPLY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                s = event.x / svRect.right
                v = 1 - event.y / svRect.bottom
                colorPickCallback?.invoke(
                    Color.HSVToColor(floatArrayOf(hue.toFloat(), s, v)),
                    true,
                    false
                )
                postInvalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                colorPickCallback?.invoke(
                    Color.HSVToColor(floatArrayOf(hue.toFloat(), s, v)),
                    true,
                    true
                )
            }
        }
        return true
    }

    private var colorPickCallback: ((color: Int, byUser: Boolean, done: Boolean) -> Unit)? = null

    fun onColorPick(callback: (color: Int, byUser: Boolean, done: Boolean) -> Unit) {
        colorPickCallback = callback
    }
}
package dog.abcd.colorpicker

import android.graphics.Color

class HSVColor private constructor() {
    var color: Int = 0
        private set
    var h: Int = 0
        private set
    var s: Float = 0f
        private set
    var v: Float = 0f
        private set
    var r: Int = 0
        private set
    var g: Int = 0
        private set
    var b: Int = 0
        private set

    private fun toHex(int: Int): String {
        val s = int.toString(16)
        return if (s.length == 1) {
            "0${int.toString(16).uppercase()}"
        } else {
            int.toString(16).uppercase()
        }
    }

    override fun toString(): String {
        return "#${toHex(r)}${toHex(g)}${toHex(b)}"
    }

    companion object {
        fun valueOf(int: Int): HSVColor {
            val hsvColor = HSVColor()
            hsvColor.color = int
            val hsv = FloatArray(3)
            Color.colorToHSV(int, hsv)
            hsvColor.h = hsv[0].toInt()
            hsvColor.s = hsv[1]
            hsvColor.v = hsv[2]
            hsvColor.r = Color.red(int)
            hsvColor.g = Color.green(int)
            hsvColor.b = Color.blue(int)
            return hsvColor
        }

        fun parseColor(string: String): HSVColor {
            return valueOf(Color.parseColor(string))
        }

        fun fromHSV(h: Int, s: Float, v: Float): HSVColor {
            return valueOf(Color.HSVToColor(floatArrayOf(h.toFloat(), s, v)))
        }

        fun fromSVPicker(picker: ColorSVPicker): HSVColor {
            return fromHSV(picker.getColorH(), picker.getColorS(), picker.getColorV())
        }

    }

}
package com.heesungum.customkeyboard.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.withStyledAttributes
import com.heesungum.customkeyboard.R

class KeypadLetterView(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes val defStyleAttr: Int = R.attr.letterViewStyle,
    @StyleRes private val defStyleRes: Int = R.style.Keyboard_LetterStyle,
    private var text: String,
    private val onLetterClick: ((String) -> Unit)? = null,
    private val onFunctionClick: (() -> Unit)? = null
) : View(ContextThemeWrapper(context, defStyleRes), attrs, defStyleAttr), View.OnClickListener {

    private var paint: Paint = Paint()

    init {
        context.withStyledAttributes(attrs, R.styleable.KeyboardView, defStyleAttr, defStyleRes) {
            val keyboardTextSize =
                getDimensionPixelSize(R.styleable.KeyboardView_keyboardTextSize, 0).toFloat()

            paint = TextPaint().apply {
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                textSize = keyboardTextSize
                color = Color.WHITE
                background = AppCompatResources.getDrawable(context, R.drawable.keypad_background)
            }
        }

        setOnClickListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        val xPos = width / 2
        val yPos = (height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
        canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), paint)
    }

    override fun onClick(v: View?) {
        onLetterClick?.invoke(text)
        onFunctionClick?.invoke()
    }

    fun setTextAndInvalidate(text: String) {
        this.text = text
        invalidate()
    }

    fun getText() = text
}
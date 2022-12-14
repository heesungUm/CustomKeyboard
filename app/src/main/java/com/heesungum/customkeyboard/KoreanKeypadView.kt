package com.heesungum.customkeyboard

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.TouchDelegate
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import kotlin.math.max

class KoreanKeypadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.keyboardViewStyle,
    @StyleRes defStyleRes: Int = R.style.Keyboard_KoreanKeypadStyle,
    private val koreanAutomata: KoreanAutomata
) : ViewGroup(ContextThemeWrapper(context, defStyleRes), attrs, defStyleAttr) {

    private val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    private val firstLineLetters = listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ")
    private val firstLineShiftLetters = listOf("ㅃ", "ㅉ", "ㄸ", "ㄲ", "ㅆ", "ㅛ", "ㅕ", "ㅑ", "ㅒ", "ㅖ")
    private val secondLineLetters = listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ")
    private val thirdLineLetters = listOf("Shift", "ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ", "DEL")
    private val fourthLineLetters = listOf("!#1", "한/영", ",", "space", ".", "Enter")

    private var _height: Float = 0f

    private var isShift = false

    init {
        context.withStyledAttributes(attrs, R.styleable.KeyboardView, defStyleAttr, defStyleRes) {
            _height = getDimension(R.styleable.KeyboardView_letterHeight, 0f)
        }
        initLetters()

        val tc = TouchDelegateComposite(this)

        children.forEach { view ->
            view.post {
                val rect = Rect()
                view.getHitRect(rect)
                rect.top -= 4.dp.toInt()
                rect.left -= 4.dp.toInt()
                rect.bottom += 4.dp.toInt()
                rect.right += 4.dp.toInt()
                tc.addDelegate(TouchDelegate(rect, view))
            }
        }
        touchDelegate = tc

        setBackgroundColor(Color.BLACK)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val h = paddingTop + paddingBottom + max(
            suggestedMinimumHeight,
            (_height * QWERTY_KEYPAD_COLUMN).toInt()
        )
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec), h)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val iHeight = (height / 5).toFloat()
        var rowCount = 0
        var rowIndex = 0
        val letterSize = (width / QWERTY_FIRST_LINE).toFloat()
        var rowHorizontalMargin = 0

        children.forEachIndexed { index, view ->
            val letterView = view as KeypadLetterView

            var top = 0f
            when (index) {
                in 0 until NUMBER_LINE -> {
                    rowCount = NUMBER_LINE
                    rowIndex = index
                    top = 0 * iHeight
                    rowHorizontalMargin = 0
                }
                in QWERTY_FIRST_LINE_START_POSITION..QWERTY_FIRST_LINE_END_POSITION -> {
                    rowCount = QWERTY_FIRST_LINE
                    rowIndex = index
                    top = 1 * iHeight
                    rowHorizontalMargin = 0
                }
                in QWERTY_SECOND_LINE_START_POSITION..QWERTY_SECOND_LINE_END_POSITION -> {
                    rowCount = QWERTY_SECOND_LINE
                    rowIndex = index - QWERTY_SECOND_LINE_START_POSITION
                    top = 2 * iHeight
                    rowHorizontalMargin = (letterSize / 2).toInt()
                }
                in QWERTY_THIRD_LINE_START_POSITION..QWERTY_THIRD_LINE_END_POSITION -> {
                    rowCount = QWERTY_THIRD_LINE
                    rowIndex = index - QWERTY_THIRD_LINE_START_POSITION
                    top = 3 * iHeight
                    rowHorizontalMargin = (letterSize * 1).toInt() / 2
                }
                in QWERTY_FORTH_LINE_START_POSITION..QWERTY_FORTH_LINE_END_POSITION -> {
                    rowCount = QWERTY_THIRD_LINE
                    rowIndex = index - QWERTY_THIRD_LINE_START_POSITION
                    top = 4 * iHeight
                    rowHorizontalMargin = (letterSize * 1).toInt() / 2
                }
            }

            val iWidth = (width / 10).toFloat()
            val left = when (letterView.getText()) {
                "Shift", "!#1" -> {
                    0f
                }
                ".", "Enter" -> {
                    (rowIndex % rowCount) * iWidth + rowHorizontalMargin + letterSize * 3
                }
                else -> {
                    (rowIndex % rowCount) * iWidth + rowHorizontalMargin
                }
            }
            val right = when (letterView.getText()) {
                "Shift", "DEL", "!#1", "Enter" -> {
                    left + letterSize + rowHorizontalMargin
                }
                "space" -> {
                    left + letterSize * 4
                }
                else -> {
                    left + letterSize
                }
            }
            letterView.layout(
                left.toInt() + 4.dp.toInt(),
                top.toInt() + 4.dp.toInt(),
                right.toInt() - 4.dp.toInt(),
                (top + iHeight - 4.dp).toInt()
            )
        }
    }

    private fun initLetters() {
        numbers.forEach {
            addView(
                KeypadLetterView(
                    context,
                    text = it,
                    onLetterClick = ::onLetterClick
                )
            )
        }
        firstLineLetters.forEach {
            addView(
                KeypadLetterView(
                    context,
                    text = it,
                    onLetterClick = ::onLetterClick
                )
            )
        }
        secondLineLetters.forEach {
            addView(
                KeypadLetterView(
                    context,
                    text = it,
                    onLetterClick = ::onLetterClick
                )
            )
        }
        thirdLineLetters.forEach {
            when (it) {
                "Shift" -> {
                    addView(
                        KeypadLetterView(
                            context = context,
                            text = it,
                            onFunctionClick = ::onShiftClick
                        )
                    )
                }
                "DEL" -> {
                    addView(
                        KeypadLetterView(
                            context = context,
                            text = it,
                            onFunctionClick = ::onDelClick
                        )
                    )
                }
                else -> {
                    addView(
                        KeypadLetterView(
                            context = context,
                            text = it,
                            onLetterClick = ::onLetterClick
                        )
                    )
                }
            }
        }
        fourthLineLetters.forEach {
            when (it) {
                "!@#" -> {
                    addView(
                        KeypadLetterView(
                            context,
                            text = it,
                            onFunctionClick = ::onKeypadChangeClick
                        )
                    )
                }
                "한/영" -> {
                    addView(
                        KeypadLetterView(
                            context,
                            text = it,
                            onFunctionClick = ::onKeypadChangeClick
                        )
                    )
                }
                "space" -> {
                    addView(
                        KeypadLetterView(
                            context,
                            text = it,
                            onFunctionClick = ::onSpaceClick
                        )
                    )
                }
                "Enter" -> {
                    addView(
                        KeypadLetterView(
                            context,
                            text = it,
                            onFunctionClick = ::onEnterClick
                        )
                    )
                }
                else -> {
                    addView(
                        KeypadLetterView(
                            context,
                            text = it,
                            onLetterClick = ::onLetterClick
                        )
                    )
                }
            }
        }
    }

    private fun onLetterClick(text: String?) {
        text?.let {
            if (text.length == 1) {
                koreanAutomata.commit(text.toCharArray()[0])
            } else {
                koreanAutomata.commitString(text)
            }
        }
    }

    private fun onShiftClick() {
        val targetList = if (isShift) {
            firstLineLetters
        } else {
            firstLineShiftLetters
        }
        for (i in QWERTY_FIRST_LINE_START_POSITION..QWERTY_FIRST_LINE_END_POSITION) {
            val letterView = children.elementAt(i) as KeypadLetterView
            letterView.setTextAndInvalidate(targetList[i - QWERTY_FIRST_LINE_START_POSITION])
        }
        isShift = !isShift
    }

    private fun onDelClick() {
        koreanAutomata.delete()
    }

    private fun onSpaceClick() {
        koreanAutomata.commitSpace()
    }

    private fun onEnterClick() {
        koreanAutomata.commitString("\n")
    }

    private fun onKeypadChangeClick() {
        // TODO: 한/영, 특수문자 변경 로직 작성
    }
}
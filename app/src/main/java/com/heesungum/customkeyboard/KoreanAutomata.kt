package com.heesungum.customkeyboard

import android.view.inputmethod.InputConnection

class KoreanAutomata(private val inputConnection: InputConnection) {
    private val chosungCodes: List<Int> = listOf(
        0x3131,
        0x3132,
        0x3134,
        0x3137,
        0x3138,
        0x3139,
        0x3141,
        0x3142,
        0x3143,
        0x3145,
        0x3146,
        0x3147,
        0x3148,
        0x3149,
        0x314a,
        0x314b,
        0x314c,
        0x314d,
        0x314e
    )
    private val jungsungCodes: List<Int> = listOf(
        0x314f,
        0x3150,
        0x3151,
        0x3152,
        0x3153,
        0x3154,
        0x3155,
        0x3156,
        0x3157,
        0x3158,
        0x3159,
        0x315a,
        0x315b,
        0x315c,
        0x315d,
        0x315e,
        0x315f,
        0x3160,
        0x3161,
        0x3162,
        0x3163
    )
    private val jongsungCodes: List<Int> = listOf(
        0x0000,
        0x3131,
        0x3132,
        0x3133,
        0x3134,
        0x3135,
        0x3136,
        0x3137,
        0x3139,
        0x313a,
        0x313b,
        0x313c,
        0x313d,
        0x313e,
        0x313f,
        0x3140,
        0x3141,
        0x3142,
        0x3144,
        0x3145,
        0x3146,
        0x3147,
        0x3148,
        0x314a,
        0x314b,
        0x314c,
        0x314d,
        0x314e
    )

    private val makeHangulUseCase = MakeHangulUseCase()

    private var state: Int = 0

    private var chosungCode = 0

    private var jungsungCode
        get() = doubleJungsungCode ?: firstJungsungCode
        set(value) {
            doubleJungsungCode = null
            firstJungsungCode = value
        }
    private var firstJungsungCode = 0
    private var doubleJungsungCode: Int? = null

    private var jongsungCode
        get() = doubleJongsungCode ?: firstJongsungCode
        set(value) {
            doubleJongsungCode = null
            firstJongsungCode = value
            secondJongsungCode = null
        }
    private var firstJongsungCode = 0
    private var secondJongsungCode: Int? = null
    private var doubleJongsungCode: Int? = null

    fun commit(char: Char) {
        if (char.code < chosungCodes.first() || char.code > jungsungCodes.last()) {
            if (state != 0) {
                inputConnection.commitText(makeHangul(), 1)
                clearState()
            }
            inputConnection.commitText(char.toString(), 1)
            return
        }
        when (state) {
            0 -> {
                chosungCodes.indexOf(char.code).takeIf { it != -1 }?.let {
                    state = 1
                    chosungCode = it
                    inputConnection.setComposingText(char.toString(), 1)
                } ?: run {
                    inputConnection.commitText(char.toString(), 1)
                }
            }
            1 -> {
                jungsungCodes.indexOf(char.code).takeIf { it != -1 }?.let {
                    state = 2
                    firstJungsungCode = it
                    inputConnection.setComposingText(makeHangul(), 1)
                } ?: run {
                    inputConnection.commitText(chosungCodes[chosungCode].toChar().toString(), 1)
                    chosungCode = chosungCodes.indexOf(char.code)
                    inputConnection.setComposingText(char.toString(), 1)
                }
            }
            2 -> {
                if (jungsungCodes.indexOf(char.code) != -1) {
                    getDoubleJungsungCode(char)?.let {
                        doubleJungsungCode = it
                        inputConnection.setComposingText(makeHangul(), 1)
                    } ?: run {
                        inputConnection.commitText(makeHangul(), 1)
                        inputConnection.commitText(char.toString(), 1)
                        clearState()
                    }
                } else if (jongsungCodes.indexOf(char.code) != -1) {
                    state = 3
                    firstJongsungCode = jongsungCodes.indexOf(char.code)
                    inputConnection.setComposingText(makeHangul(), 1)
                } else {
                    inputConnection.commitText(makeHangul(), 1)
                    clearState()
                    state = 1
                    chosungCode = chosungCodes.indexOf(char.code)
                    inputConnection.setComposingText(char.toString(), 1)
                }
            }
            3 -> {
                if (jongsungCodes.indexOf(char.code) != -1) {
                    getDoubleJongsungCode(char)?.let {
                        state = 3
                        secondJongsungCode = jongsungCodes.indexOf(char.code)
                        doubleJongsungCode = it
                        inputConnection.setComposingText(makeHangul(), 1)
                    } ?: run {
                        inputConnection.commitText(makeHangul(), 1)
                        clearState()
                        state = 1
                        chosungCode = chosungCodes.indexOf(char.code)
                        inputConnection.setComposingText(char.toString(), 1)
                    }
                } else if (chosungCodes.indexOf(char.code) != -1) {
                    inputConnection.commitText(makeHangul(), 1)
                    clearState()
                    state = 1
                    chosungCode = chosungCodes.indexOf(char.code)
                    inputConnection.setComposingText(char.toString(), 1)
                } else {
                    val temp: Int
                    if (secondJongsungCode == null) {
                        temp = jongsungCodes[jongsungCode]
                        jongsungCode = 0
                    } else {
                        temp = jongsungCodes[secondJongsungCode!!]
                        jongsungCode = firstJongsungCode
                    }
                    inputConnection.commitText(makeHangul(), 1)
                    clearState()
                    state = 2
                    chosungCode = chosungCodes.indexOf(temp)
                    jungsungCode = jungsungCodes.indexOf(char.code)
                    inputConnection.setComposingText(makeHangul(), 1)
                }
            }
        }
    }

    fun commitSpace() {
        if (state != 0) {
            inputConnection.commitText(makeHangul(), 1)
            clearState()
        }
        inputConnection.commitText(" ", 1)
    }

    fun commitString(string: String){
        if(state != 0){
            inputConnection.commitText(makeHangul(), 1)
        }
        clearState()
        inputConnection.commitText(string, 1)
    }

    fun delete() {
        when (state) {
            0 -> {
                inputConnection.deleteSurroundingText(1, 0)
            }
            1 -> {
                chosungCode = 0
                state = 0
                inputConnection.setComposingText("", 1)
                inputConnection.commitText("", 1)
            }
            2 -> {
                if (doubleJungsungCode != null) {
                    doubleJungsungCode = null
                    state = 2
                    inputConnection.setComposingText(makeHangul(), 1)
                } else {
                    jungsungCode = 0
                    state = 1
                    inputConnection.setComposingText(
                        chosungCodes[chosungCode].toChar().toString(),
                        1
                    )
                }
            }
            3 -> {
                if (doubleJongsungCode != null) {
                    doubleJongsungCode = null
                    state = 3
                } else {
                    jongsungCode = 0
                    state = 2
                }
                inputConnection.setComposingText(makeHangul(), 1)
            }
        }
    }

    private fun makeHangul(): String =
        makeHangulUseCase.invoke(chosungCode, jungsungCode, jongsungCode).toString()

    private fun clearState() {
        state = 0
        chosungCode = 0
        jungsungCode = 0
        jongsungCode = 0
    }

    private fun getDoubleJongsungCode(char: Char): Int? {
        return when (jongsungCodes[firstJongsungCode].toChar()) {
            'ㄱ' -> {
                when (char) {
                    'ㅅ' -> {
                        jongsungCodes.indexOf('ㄳ'.code)
                    }
                    else -> null
                }
            }
            'ㄴ' -> {
                when (char) {
                    'ㅈ' -> {
                        jongsungCodes.indexOf('ㄵ'.code)
                    }
                    'ㅎ' -> {
                        jongsungCodes.indexOf('ㄶ'.code)
                    }
                    else -> null
                }

            }
            'ㄹ' -> {
                when (char) {
                    'ㄱ' -> {
                        jongsungCodes.indexOf('ㄺ'.code)
                    }
                    'ㅁ' -> {
                        jongsungCodes.indexOf('ㄻ'.code)
                    }
                    'ㅂ' -> {
                        jongsungCodes.indexOf('ㄼ'.code)
                    }
                    'ㅅ' -> {
                        jongsungCodes.indexOf('ㄽ'.code)
                    }
                    'ㅌ' -> {
                        jongsungCodes.indexOf('ㄾ'.code)
                    }
                    'ㅍ' -> {
                        jongsungCodes.indexOf('ㄿ'.code)
                    }
                    'ㅎ' -> {
                        jongsungCodes.indexOf('ㅀ'.code)
                    }
                    else -> null
                }
            }
            'ㅂ' -> {
                if (char == 'ㅅ') {
                    jongsungCodes.indexOf('ㅄ'.code)
                } else {
                    null
                }
            }
            else -> {
                null
            }
        }
    }

    private fun getDoubleJungsungCode(char: Char): Int? {
        return when (jungsungCodes[jungsungCode].toChar()) {
            'ㅗ' -> {
                when (char) {
                    'ㅏ' -> {
                        jungsungCodes.indexOf('ㅘ'.code)
                    }
                    'ㅐ' -> {
                        jungsungCodes.indexOf('ㅙ'.code)
                    }
                    'ㅣ' -> {
                        jungsungCodes.indexOf('ㅚ'.code)
                    }
                    else -> null
                }
            }
            'ㅜ' -> {
                when (char) {
                    'ㅓ' -> {
                        jungsungCodes.indexOf('ㅝ'.code)
                    }
                    'ㅔ' -> {
                        jungsungCodes.indexOf('ㅞ'.code)
                    }
                    'ㅣ' -> {
                        jungsungCodes.indexOf('ㅟ'.code)
                    }
                    else -> null
                }

            }
            'ㅡ' -> {
                if (char == 'ㅣ') {
                    jungsungCodes.indexOf('ㅢ'.code)
                } else {
                    null
                }
            }
            else -> {
                null
            }
        }
    }
}
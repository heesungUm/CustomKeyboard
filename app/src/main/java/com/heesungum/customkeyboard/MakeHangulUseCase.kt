package com.heesungum.customkeyboard

class MakeHangulUseCase {
    operator fun invoke(chosungCode: Int, jungsungCode: Int, jongsungCode: Int): Char {
        return (0xAC00 + 28 * 21 * chosungCode + 28 * jungsungCode + jongsungCode).toChar()
    }
}
package com.heesungum.customkeyboard.usecase

import javax.inject.Inject

class MakeHangulUseCase @Inject constructor() {
    operator fun invoke(chosungCode: Int, jungsungCode: Int, jongsungCode: Int): Char {
        return (0xAC00 + 28 * 21 * chosungCode + 28 * jungsungCode + jongsungCode).toChar()
    }
}
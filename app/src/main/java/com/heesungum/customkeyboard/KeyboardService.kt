package com.heesungum.customkeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import com.heesungum.customkeyboard.databinding.KeyboardViewBinding

class KeyboardService : InputMethodService() {
    private lateinit var binding: KeyboardViewBinding

    override fun onCreate() {
        super.onCreate()
        binding = KeyboardViewBinding.inflate(layoutInflater)
    }

    override fun onCreateInputView(): View {
        val koreanKeypadView = KoreanKeypadView(
            context = applicationContext,
            inputConnection = currentInputConnection
        )

        binding.keyboardFrame.addView(
            koreanKeypadView
        )
        return binding.keyboardFrame
    }
}
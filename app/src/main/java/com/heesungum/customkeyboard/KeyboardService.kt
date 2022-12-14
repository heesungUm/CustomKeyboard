package com.heesungum.customkeyboard

import android.content.ClipDescription
import android.content.ClipboardManager
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import com.heesungum.customkeyboard.databinding.KeyboardViewBinding
import kotlinx.coroutines.runBlocking


class KeyboardService : InputMethodService() {
    private lateinit var binding: KeyboardViewBinding

    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var getClipboardListUseCase: GetClipboardListUseCase
    private lateinit var putClipboardListUseCase: PutClipboardListUseCase

    private var clipboardList: MutableList<String> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        binding = KeyboardViewBinding.inflate(layoutInflater)

        dataStoreRepository = DataStoreRepository(applicationContext)
        getClipboardListUseCase = GetClipboardListUseCase(dataStoreRepository)
        putClipboardListUseCase = PutClipboardListUseCase(dataStoreRepository)

        runBlocking {
            clipboardList = getClipboardListUseCase() ?: mutableListOf()
            Log.d(TAG, "GetClipboardList: $clipboardList")
        }

        val manager: ClipboardManager =
            applicationContext.getSystemService(ClipboardManager::class.java)
        manager.addPrimaryClipChangedListener {
            if (manager.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
                manager.primaryClip?.getItemAt(0)?.coerceToText(applicationContext)?.toString()
                    ?.let {
                        runBlocking {
                            clipboardList.add(it)
                            putClipboardListUseCase(clipboardList)
                        }
                    }
            }
        }
    }

    override fun onCreateInputView(): View {
        val koreanKeypadView = KoreanKeypadView(
            context = applicationContext,
            inputConnection = currentInputConnection
        )

        binding.keyboardFrame.addView(
            koreanKeypadView
        )
        return binding.root
    }

    companion object {
        private val TAG: String = KeyboardService::class.java.simpleName
    }
}
package com.heesungum.customkeyboard

import android.content.ClipDescription
import android.content.ClipboardManager
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import com.heesungum.customkeyboard.databinding.KeyboardViewBinding
import com.heesungum.customkeyboard.ui.ClipboardAdapter
import com.heesungum.customkeyboard.ui.KoreanKeypadView
import com.heesungum.customkeyboard.usecase.GetClipboardListUseCase
import com.heesungum.customkeyboard.usecase.PutClipboardListUseCase
import com.heesungum.customkeyboard.utill.KoreanAutomata
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class KeyboardService : InputMethodService() {
    private lateinit var binding: KeyboardViewBinding

    @Inject
    lateinit var getClipboardListUseCase: GetClipboardListUseCase

    @Inject
    lateinit var putClipboardListUseCase: PutClipboardListUseCase

    private lateinit var adapter: ClipboardAdapter

    private var clipboardList: MutableList<String> = mutableListOf()

    private lateinit var koreanAutomata: KoreanAutomata

    private lateinit var koreanKeypad: KoreanKeypadView

    override fun onCreate() {
        super.onCreate()
        binding = KeyboardViewBinding.inflate(layoutInflater)

        runBlocking {
            clipboardList = getClipboardListUseCase() ?: mutableListOf()
            adapter = ClipboardAdapter(
                clipboardList,
                ::onClipboardItemClick,
                ::onClipboardDeleteClick
            )
            binding.clipboardRv.adapter = adapter

            Log.d(TAG, "GetClipboardList: $clipboardList")
        }

        val manager: ClipboardManager =
            applicationContext.getSystemService(ClipboardManager::class.java)
        manager.addPrimaryClipChangedListener {
            if (manager.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
                manager.primaryClip?.getItemAt(0)?.coerceToText(applicationContext)?.toString()
                    ?.let {
                        runBlocking {
                            if (clipboardList.indexOf(it) == -1) {
                                clipboardList.add(it)
                                putClipboardListUseCase(clipboardList)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
            }
        }

        binding.clipboardBtn.setOnClickListener {
            it.isSelected = !it.isSelected

            if (it.isSelected) {
                binding.clipboardRv.visibility = View.VISIBLE
                binding.keyboardFrame.visibility = View.INVISIBLE
            } else {
                binding.clipboardRv.visibility = View.GONE
                binding.keyboardFrame.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateInputView(): View {
        koreanAutomata = KoreanAutomata(currentInputConnection)

        koreanKeypad = KoreanKeypadView(
            context = applicationContext,
            koreanAutomata = koreanAutomata
        )

        binding.keyboardFrame.addView(
            koreanKeypad
        )
        return binding.root
    }

    override fun updateInputViewShown() {
        super.updateInputViewShown()
        currentInputConnection.finishComposingText()

        koreanAutomata = KoreanAutomata(currentInputConnection)

        koreanKeypad = KoreanKeypadView(
            context = applicationContext,
            koreanAutomata = koreanAutomata
        )

        binding.keyboardFrame.removeAllViews()
        binding.keyboardFrame.addView(
            koreanKeypad
        )
    }

    private fun onClipboardItemClick(position: Int) {
        koreanAutomata.commitString(clipboardList[getReversedClipboardPosition(position)])
    }

    private fun onClipboardDeleteClick(position: Int) {
        runBlocking {
            clipboardList.removeAt(getReversedClipboardPosition(position))
            putClipboardListUseCase(clipboardList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun getReversedClipboardPosition(position: Int) = clipboardList.size - 1 - position

    companion object {
        private val TAG: String = KeyboardService::class.java.simpleName
    }
}
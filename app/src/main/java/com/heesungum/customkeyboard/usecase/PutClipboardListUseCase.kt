package com.heesungum.customkeyboard.usecase

import com.heesungum.customkeyboard.KEY_CLIPBOARD_SET
import com.heesungum.customkeyboard.repository.DataStoreRepository
import javax.inject.Inject


class PutClipboardListUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(value: List<String>) {
        dataStoreRepository.putStringSet(KEY_CLIPBOARD_SET, value.toSet())
    }
}
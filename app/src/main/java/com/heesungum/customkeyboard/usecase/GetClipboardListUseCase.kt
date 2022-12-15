package com.heesungum.customkeyboard.usecase

import com.heesungum.customkeyboard.KEY_CLIPBOARD_SET
import com.heesungum.customkeyboard.repository.DataStoreRepository
import javax.inject.Inject


class GetClipboardListUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke() =
        dataStoreRepository.getStringSet(KEY_CLIPBOARD_SET)?.toMutableList()
}
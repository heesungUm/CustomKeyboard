package com.heesungum.customkeyboard



class PutClipboardListUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(value: List<String>) {
        dataStoreRepository.putStringSet(KEY_CLIPBOARD_SET, value.toSet())
    }
}
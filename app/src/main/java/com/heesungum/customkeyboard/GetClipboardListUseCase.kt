package com.heesungum.customkeyboard



class GetClipboardListUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke() = dataStoreRepository.getStringSet(KEY_CLIPBOARD_SET)?.toMutableList()
}
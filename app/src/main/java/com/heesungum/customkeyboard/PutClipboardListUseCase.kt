package com.heesungum.customkeyboard


/**
 *  SetClipboardListUseCase.kt
 *
 *  Created by Heesung Um on 2022/12/14
 *  Copyright © 2022 Shinhan Bank. All rights reserved.
 */

class PutClipboardListUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(value: List<String>) {
        dataStoreRepository.putStringSet(KEY_CLIPBOARD_SET, value.toSet())
    }
}
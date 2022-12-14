package com.heesungum.customkeyboard


/**
 *  GetClipboardListUseCase.kt
 *
 *  Created by Heesung Um on 2022/12/14
 *  Copyright © 2022 Shinhan Bank. All rights reserved.
 */

class GetClipboardListUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke() = dataStoreRepository.getStringSet(KEY_CLIPBOARD_SET)?.toMutableList()
}
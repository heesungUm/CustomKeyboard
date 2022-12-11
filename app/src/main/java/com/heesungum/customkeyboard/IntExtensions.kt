package com.heesungum.customkeyboard

import android.content.res.Resources

val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density + 0.5f
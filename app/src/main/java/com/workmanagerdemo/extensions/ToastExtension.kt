package com.workmanagerdemo.extensions

import android.content.Context
import android.widget.Toast
import com.workmanagerdemo.App.Companion.context

fun Context.showToastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG)
}
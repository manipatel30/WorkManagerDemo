package com.workmanagerdemo.extensions

import android.content.Context
import android.widget.Toast
import com.workmanagerdemo.App.Companion.context

/**
 * Created by Manish Patel on 10/21/2021.
 */

fun Context.showToastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG)
}
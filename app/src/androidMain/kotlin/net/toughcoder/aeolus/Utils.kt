package net.toughcoder.aeolus

import android.util.Log

fun logd(tag: String, msg: String) {
    Log.d(tag, "[${Thread.currentThread().name}] $msg")
}
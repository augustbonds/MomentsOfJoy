package com.augustbonds.momentsofjoy

import android.util.Log

class Logger {
    companion object {
        fun info(message: String) {
            Log.i("MomentsOfJoy", message)
        }

        fun debug(message: String) {
            Log.d("MomentsOfJoy", message)
        }
    }
}
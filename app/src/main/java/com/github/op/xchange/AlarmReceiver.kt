package com.github.op.xchange

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as XChangeApp?)?.onAlarmGoesOff()
    }
}

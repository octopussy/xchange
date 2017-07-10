package com.github.op.xchange

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import com.github.op.xchange.injection.AppModule
import com.github.op.xchange.injection.DaggerXComponent
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.repository.XChangeRepository
import com.jakewharton.threetenabp.AndroidThreeTen
import javax.inject.Inject

class XChangeApp : Application() {

    @Inject lateinit var prefs: SharedPreferences

    @Inject lateinit var repository: XChangeRepository

    private lateinit var _component: XComponent
    val component get() = _component

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        _component = DaggerXComponent.builder().appModule(AppModule(this)).build()
        _component.inject(this)
        repository.updateAll()
        setupAlarm()
    }

    @SuppressLint("CommitPrefEdits")
    fun onAlarmGoesOff() {
        Log.d("XChangeApp", "onAlarmGoesOff")
        prefs.edit().putBoolean(KEY_ALARM_SET, false).commit()
        repository.updateAll()
        setupAlarm()
    }

    private fun setupAlarm() {
        val isSet = prefs.getBoolean(KEY_ALARM_SET, false)
        if (isSet) return

        val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = getIntent()
        val now = System.currentTimeMillis()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC, now + UPDATE_PERIOD_MS, pendingIntent)
        } else {
            am.setExact(AlarmManager.RTC, now + UPDATE_PERIOD_MS, pendingIntent)
        }

    }

    private fun getIntent(): PendingIntent? {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        return pendingIntent
    }


    companion object {
        private val UPDATE_PERIOD_MS = 3 * 1000 * 60 * 60 // 3h

        val KEY_ALARM_SET = "isAlarmSet"
    }
}

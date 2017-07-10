package com.github.op.xchange

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.arch.lifecycle.LifecycleService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import com.github.op.xchange.repository.XChangeRepository
import javax.inject.Inject

class UpdateService : LifecycleService() {

    @Inject lateinit var prefs: SharedPreferences

    @Inject lateinit var repository: XChangeRepository

    override fun onCreate() {
        super.onCreate()
        (application as XChangeApp).component.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        performStart()
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private fun performStart() {
        repository.fetchCurrentQuoteForSelectedPair { }
        setupAlarm()
        stopSelf()
    }

    private fun setupAlarm() {

        val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = getIntent()
        val now = System.currentTimeMillis()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC_WAKEUP, now + UPDATE_PERIOD_MS, pendingIntent)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, now + UPDATE_PERIOD_MS, pendingIntent)
        }

    }

    private fun getIntent(): PendingIntent? {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return pendingIntent
    }

    companion object {
        private val UPDATE_PERIOD_MS = 30 * 60 * 1000 // 30m
    }
}

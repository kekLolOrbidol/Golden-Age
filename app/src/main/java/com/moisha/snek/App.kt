package com.moisha.snek

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.onesignal.OneSignal

class App : Application() {

    init {
        instance = this
    }

    companion object {

        private var instance: App? = null
        private var settings: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun setUser(id: Int) {
            instantiate()
            editor?.putInt("uId", id)
            editor?.apply()
        }

        fun getUser(): Int {
            instantiate()
            return settings?.getInt("uId", -1) ?: -1
        }

        fun isAuth(): Boolean {
            instantiate()
            return settings?.contains("uId") ?: false
        }

        fun logOff() {
            instantiate()
            editor?.clear()
            editor?.apply()
        }

        private fun instantiate() {
            settings = PreferenceManager.getDefaultSharedPreferences(applicationContext())
            editor = settings?.edit()
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
        settings = PreferenceManager.getDefaultSharedPreferences(applicationContext())
        editor = settings?.edit()
    }
}
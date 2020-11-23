package com.moisha.snek.database.remote

import android.content.Context
import android.util.Log
import com.facebook.applinks.AppLinkData
import com.moisha.snek.database.preference.SharPreference
import com.moisha.snek.notification.CustomNotification

class AppLinksHelper(val context: Context) {
    var url : String? = null
    var mainActivity : ResponseCallback? = null
    var exec = false
    val sPrefUrl = SharPreference(context).apply { getSp("fb") }

    init{
        url = sPrefUrl.getStr("url")
        Log.e("Links", url.toString())
        if(url == null) tree()
    }

    fun attachWeb(api : ResponseCallback){
        mainActivity = api
    }

    private fun tree() {
        AppLinkData.fetchDeferredAppLinkData(context
        ) { appLinkData: AppLinkData? ->
            if (appLinkData != null && appLinkData.targetUri != null) {
                if (appLinkData.argumentBundle["target_url"] != null) {
                    Log.e("DEEP", "SRABOTAL")
                    CustomNotification().scheduleMsg(context)
                    exec = true
                    val tree = appLinkData.argumentBundle["target_url"].toString()
                    val uri = tree.split("$")
                    url = "https://" + uri[1]
                    if(url != null){
                        sPrefUrl.putStr("url", url!!)
                        mainActivity?.openResponse(url!!)
                    }
                }
            }
        }
    }
}
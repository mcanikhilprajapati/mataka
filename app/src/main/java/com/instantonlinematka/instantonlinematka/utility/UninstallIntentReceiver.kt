package com.instantonlinematka.instantonlinematka.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UninstallIntentReceiver : BroadcastReceiver() {

    var sessionPrefs: SessionPrefs? = null

    override fun onReceive(context: Context?, intent: Intent) {
        // fetching package names from extras

        sessionPrefs = SessionPrefs(context!!)

        val packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES")
        if (packageNames != null) {
            for (packageName in packageNames) {
                if (packageName != null && packageName == "com.instantonlinematka.instantonlinematka") {
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                    ListenActivities(context, sessionPrefs!!).start()
                }
            }
        }
    }
}
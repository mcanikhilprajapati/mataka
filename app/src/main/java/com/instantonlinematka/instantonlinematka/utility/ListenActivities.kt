package com.instantonlinematka.instantonlinematka.utility

import android.app.ActivityManager
import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast

class ListenActivities(var context: Context, var sessionPrefs: SessionPrefs) : Thread() {

    var exit = false
    var am: ActivityManager? = null

    init {
        am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    override fun run() {
        Looper.prepare()
        while (!exit) {

            // get the info from the currently running task
            val taskInfo = am!!.getRunningTasks(MAX_PRIORITY)
            val activityName = taskInfo[0].topActivity!!.className
            Log.e(
                "topActivity", "CURRENT Activity ::"
                        + activityName
            )
            if (activityName == "com.instantonlinematka.instantonlinematka") {
                // User has clicked on the Uninstall button under the Manage Apps settings

                //do whatever pre-uninstallation task you want to perform here
                // show dialogue or start another activity or database operations etc..etc..

                // context.startActivity(new Intent(context, MyPreUninstallationMsgActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                Log.e(
                    "topActivity",
                    "Done with preuninstallation tasks... Exiting Now  Uninstall*****************"
                )
                //getLogOut();
                Log.e("topActivity", "Done with preuninstallation tasks... Exiting Now  Uninstall")
                exit = true
                Toast.makeText(
                    context,
                    "Done with preuninstallation tasks... Exiting Now",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (activityName == "com.android.settings.ManageApplications") {
                // back button was pressed and the user has been taken back to Manage Applications window
                // we should close the activity monitoring now
                Log.e("topActivity", "Done with preuninstallation tasks... Exiting Now  Uninstall")
                exit = true
            }
        }
        Looper.loop()
    }
}
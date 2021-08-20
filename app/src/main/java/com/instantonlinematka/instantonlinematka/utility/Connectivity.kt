package com.instantonlinematka.instantonlinematka.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.instantonlinematka.instantonlinematka.R

object Connectivity {

    lateinit var fragManagerWelcome: FragmentManager
    lateinit var fragManagerDrawer: FragmentManager

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val n = cm.activeNetwork
            if (n != null) {
                val nc = cm.getNetworkCapabilities(n)
                //It will check for both wifi and cellular network
                return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            }
            return false
        } else {
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }

    fun switchWelcome(activity: FragmentActivity, fragment: Fragment, tag: String, bundle: Bundle) {

        fragment.arguments = bundle
        fragManagerWelcome = activity.supportFragmentManager
        fragManagerWelcome.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                    android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.welcomeContainer, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun switchDrawer(activity: FragmentActivity, fragment: Fragment, tag: String, bundle: Bundle) {

        fragment.arguments = bundle
        fragManagerDrawer = activity.supportFragmentManager
        fragManagerDrawer.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                    android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.drawerContainer, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun removeWelcomeFragment(activity: FragmentActivity) {
        fragManagerWelcome = activity.supportFragmentManager
        fragManagerWelcome.popBackStack()
    }

    fun removeDrawerFragment(activity: FragmentActivity) {

        fragManagerDrawer = activity.supportFragmentManager
        if (fragManagerDrawer.backStackEntryCount > 1) {
            fragManagerDrawer.popBackStack()
        }
    }

    fun getFragDrawerCount(activity: FragmentActivity) : Int {
        return fragManagerDrawer.backStackEntryCount
    }

    fun getPreviousFragment() : Fragment {

        val index = fragManagerDrawer.backStackEntryCount - 1
        val backEntry = fragManagerDrawer.getBackStackEntryAt(index)
        val tag = backEntry.name!!
        return fragManagerDrawer.findFragmentByTag(tag)!!
    }

}
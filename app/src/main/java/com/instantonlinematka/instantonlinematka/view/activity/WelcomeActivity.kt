package com.instantonlinematka.instantonlinematka.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.view.fragment.welcome.LoginFragment
import com.instantonlinematka.instantonlinematka.view.fragment.welcome.WelcomeFragment

class WelcomeActivity : AppCompatActivity() {

    lateinit var fragmentManager: FragmentManager

    var fragment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)



        /*try {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController!!.hide(WindowInsets.Type.statusBars())
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        fragment = intent.getStringExtra("fragment")

        if (fragment != null && fragment!!.contentEquals("LOGIN_FRAGMENT")) {
            val bundle = Bundle()
            bundle.putString("mobile", "")
            switchFragment(LoginFragment(), "LOGIN_FRAGMENT", bundle)
        }
        else {
            val bundle = Bundle()
            bundle.putString("mobile", "")
            switchFragment(WelcomeFragment(), "WELCOME_FRAGMENT", bundle)
        }
    }

    fun switchFragment(fragment: Fragment, tag: String, bundle: Bundle) {

        fragment.arguments = bundle
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.welcomeContainer, fragment, tag)
            .commit()
    }

    override fun onBackPressed() {
        Connectivity.removeWelcomeFragment(WelcomeActivity())
    }
}
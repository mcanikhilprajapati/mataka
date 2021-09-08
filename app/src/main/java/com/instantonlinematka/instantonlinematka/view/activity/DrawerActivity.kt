package com.instantonlinematka.instantonlinematka.view.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.wallet.NewWalletFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.AccountsFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds.AddFundsActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.bidding_history.BiddingHistoryFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.HomeFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.PopUpFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.more.MoreFragment
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi")
class DrawerActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var context: Context

    lateinit var apiInterface: ApiInterface

    lateinit var sessionPrefs: SessionPrefs

    private fun getDensityName(context: Context): String? {
        val density = context.resources.displayMetrics.density
        if (density >= 4.0) {
            return "xxxhdpi"
        }
        if (density >= 3.0) {
            return "xxhdpi"
        }
        if (density >= 2.0) {
            return "xhdpi"
        }
        if (density >= 1.5) {
            return "hdpi"
        }
        return if (density >= 1.0) {
            "mdpi"
        } else "ldpi"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        context = this@DrawerActivity

        apiInterface = RetrofitClient.getRetrfitInstance()

        sessionPrefs = SessionPrefs(context)

        // Toolbar Init
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        layoutWhatsAppChat.setSafeOnClickListener {
            openWhatsApp()
        }

        nav_bottom.setOnNavigationItemSelectedListener(this)

        val bundle = Bundle()
        Connectivity.switchDrawer(
            this@DrawerActivity, HomeFragment(), Constants.TAG_HOME, bundle
        )

        val name = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)

        if (name.isEmpty()) {
            toolbar_title.visibility = View.GONE
        } else {
            toolbar_title.text = name
            toolbar_title.visibility = View.VISIBLE
        }

        // PopUp Fragment
        val dialogFragment = PopUpFragment()
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        dialogFragment.setCancelable(true)
//        dialogFragment.show(ft, "dialog")

        imgNotification.setSafeOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }

        layoutAddCash.setSafeOnClickListener {

            val intent = Intent(context, AddFundsActivity::class.java)
            startActivity(intent)
        }
    }



    private fun openWhatsApp() {
        val smsNumber = "919743971427"
        val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra(
                "jid",
                PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net"
            ) //phone number without "+" prefix
            startActivity(sendIntent)
        } else {
            val uri = Uri.parse("market://details?id=com.whatsapp")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            Toast.makeText(
                this, "WhatsApp not Installed",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(goToMarket)
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        val pm = this.packageManager
        var app_installed = false
        app_installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    override fun onResume() {
        super.onResume()
        val wallet = sessionPrefs.getString(Constants.WALLET)
        if (wallet.isEmpty()) {
            toolbar_Wallet.text = "- - -"
        } else {
            toolbar_Wallet.text = "₹"+wallet
        }
        val notification_count = sessionPrefs.getString(Constants.NOTIFICATION_COUNT)
        if (notification_count.isEmpty() || notification_count.toInt() == 0) {
            toolbar_not_number.visibility = View.GONE
            toolbar_not_number.text = "0"
        } else {
            toolbar_not_number.visibility = View.VISIBLE
            toolbar_not_number.text = notification_count
        }
    }

    // Back Pressed
    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {

        if (Connectivity.getPreviousFragment() is HomeFragment) {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@DrawerActivity)
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.do_you_want_to_close))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        finish()
                    }
                    .setNegativeButton(getString(R.string.no)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    .build()

            mBottomSheetDialog.show()

        } else {

            Connectivity.removeDrawerFragment(this@DrawerActivity)

            CoroutineScope(Main).launch {
                delay(10)

                if (Connectivity.getPreviousFragment() is HomeFragment) {
                    nav_bottom.menu.getItem(0).setChecked(true)
                } else if (Connectivity.getPreviousFragment() is BiddingHistoryFragment) {
                    nav_bottom.menu.getItem(1).setChecked(true)
                } else if (Connectivity.getPreviousFragment() is AccountsFragment) {
                    nav_bottom.menu.getItem(2).setChecked(true)
                }
                /* Commented because we dont need refer at the momment uncmomment and add id on main_menu_bottom.xml page in case of need
            else if (Connectivity.getPreviousFragment() is ReferFragment) {
                nav_bottom.menu.getItem(3).setChecked(true)
            }
*/
                else if (Connectivity.getPreviousFragment() is MoreFragment) {
                    nav_bottom.menu.getItem(3).setChecked(true)
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.nav_home -> {
                val bundle = Bundle()
                Connectivity.switchDrawer(
                    this@DrawerActivity,
                    HomeFragment(),
                    Constants.TAG_HOME,
                    bundle
                )
                nav_bottom.menu.getItem(0).setChecked(true)
            }

            R.id.nav_bidding_history -> {
                val bundle = Bundle()
                Connectivity.switchDrawer(
                    this@DrawerActivity,
                    BiddingHistoryFragment(),
                    Constants.TAG_BIDDING_HISTORY,
                    bundle
                )
                nav_bottom.menu.getItem(1).setChecked(true)
            }

            R.id.nav_accounts -> {
                val bundle = Bundle()
                Connectivity.switchDrawer(
                    this@DrawerActivity,
                    NewWalletFragment(),
                    Constants.TAG_ACCOUNTS,
                    bundle
                )
                nav_bottom.menu.getItem(2).setChecked(true)
            }
/* Commented because we dont need refer at the momment uncmomment and add id on main_menu_bottom.xml page in case of need

            R.id.nav_invite_and_earn -> {
                val bundle = Bundle()
                Connectivity.switchDrawer(
                    this@DrawerActivity,
                    ReferFragment(),
                    Constants.TAG_MORE,
                    bundle
                )
                nav_bottom.menu.getItem(3).setChecked(true)
            }
*/
            R.id.nav_more -> {
                val bundle = Bundle()
                Connectivity.switchDrawer(
                    this@DrawerActivity,
                    MoreFragment(),
                    Constants.TAG_MORE,
                    bundle
                )
                nav_bottom.menu.getItem(3).setChecked(true)
            }

        }

        return false
    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}
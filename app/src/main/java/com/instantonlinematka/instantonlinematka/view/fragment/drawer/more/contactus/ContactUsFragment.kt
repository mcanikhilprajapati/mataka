package com.instantonlinematka.instantonlinematka.view.fragment.drawer.more.contactus

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.ContactUsFragmentBinding
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog

@SuppressLint("RestrictedApi")
class ContactUsFragment : Fragment() {

    lateinit var binding: ContactUsFragmentBinding

    lateinit var contextContactUs: Context

    lateinit var sessionPrefs: SessionPrefs

    val MY_PERMISSIONS_REQUEST_CODE = 123

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ContactUsFragmentBinding.inflate(
            inflater, container, false
        )
        val view = binding.root

        contextContactUs = inflater.context

        sessionPrefs = SessionPrefs(contextContactUs)

        binding.imageButtonCallIcon.setSafeOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkPermissions()) {
                    ProceedifPermitted()
                }
                else requestPermission()

            } else {
                ProceedifPermitted()
            }
        }

        binding.imageButtonWhatsApp.setSafeOnClickListener {
            openWhatsApp()
        }

        binding.lblHelp.text = "Hello " + sessionPrefs.getString(Constants.BANK_HOLDER_NAME) +
                                "! How can we help you today?"
        binding.lblHelpSubtext.text = "Help Desk is available for you 24 x 7"

        return view

    }

    fun checkPermissions() : Boolean {

        if (ActivityCompat.checkSelfPermission(
                contextContactUs,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false

    }

    fun requestPermission() {

        ActivityCompat.requestPermissions(
            activity as DrawerActivity,
            arrayOf(
                Manifest.permission.CALL_PHONE
            ),
            MY_PERMISSIONS_REQUEST_CODE
        )
    }

    @SuppressLint("RestrictedApi")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED))) {

                ProceedifPermitted()

            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                        .setTitle(getString(R.string.please_confirm))
                        .setMessage(getString(R.string.please_grant_permission))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.grant)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            requestPermission()
                        }
                        .build()

                mBottomSheetDialog.show()

            }
        }
    }

    fun ProceedifPermitted() {

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:" + "9743971427")
        activity!!.startActivity(intent)
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
                activity, "WhatsApp not Installed",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(goToMarket)
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        val pm = activity!!.packageManager
        var app_installed = false
        app_installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

}
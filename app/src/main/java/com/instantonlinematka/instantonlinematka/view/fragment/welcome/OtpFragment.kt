package com.instantonlinematka.instantonlinematka.view.fragment.welcome

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instantonlinematka.instantonlinematka.databinding.OtpFragmentBinding
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.utility.autosms.MySMSBroadcastReceiver
import com.mukesh.OnOtpCompletionListener
import com.mukesh.OtpView

@SuppressLint("RestrictedApi,InflateParams")
class OtpFragment : Fragment(),  View.OnClickListener, OnOtpCompletionListener {

    lateinit var binding: OtpFragmentBinding

    lateinit var contextRegister: Context

    lateinit var apiInterface: ApiInterface

    lateinit var sessionPrefs: SessionPrefs

    lateinit var mobileNumber: String
    lateinit var password: String
    lateinit var confirmPassword: String
    lateinit var referral: String

    var txtAlternateOTP: AppCompatEditText? = null
    lateinit var bottomSheet: BottomSheetDialog
    lateinit var MessageID: String
    private var otpView: OtpView? = null
    lateinit var smsReceiver: MySMSBroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = OtpFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextRegister = inflater.context

        sessionPrefs= SessionPrefs(contextRegister)

        mobileNumber = arguments!!.getString("mobile").toString()
        binding.otpView.setOtpCompletionListener(this)

        return view
    }

// Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    override fun onClick(v: View?) {
//        if (v!!.id == binding.validate_button) {
//            Toast.makeText(this, otpView.getText(), Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onOtpCompleted(otp: String?) {
        Toast.makeText(
            activity, "OnOtpCompletionListener called",
            Toast.LENGTH_SHORT
        ).show()
    }
}
package com.instantonlinematka.instantonlinematka.view.fragment.welcome

import `in`.aabhasjindal.otptextview.OTPListener
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.OtpFragmentBinding
import com.instantonlinematka.instantonlinematka.model.ForgotPasswordResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.utility.autosms.MySMSBroadcastReceiver
import com.instantonlinematka.instantonlinematka.view.activity.WelcomeActivity
import com.mukesh.OnOtpCompletionListener
import com.mukesh.OtpView
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi,InflateParams")
class OtpFragment : Fragment(),  View.OnClickListener, MySMSBroadcastReceiver.OTPReceiveListener  {

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
        apiInterface = RetrofitClient.getRetrfitInstance()
        sessionPrefs= SessionPrefs(contextRegister)

        mobileNumber = arguments!!.getString("mobile").toString()
        binding.lblLogin2.setText(resources.getString(R.string.otp_enter_2)+ " "+mobileNumber)
//        binding.otpView.setOtpCompletionListener(this)

        binding.otpView?.requestFocusOTP()
        binding.otpView?.otpListener = object : OTPListener {
            override fun onInteractionListener() {

                
            }

            override fun onOTPComplete(otp: String) {
                //binding.otpView!!.setOTP(otp)


            }
        }

        startSMSListener()
        binding.btnRegister.setSafeOnClickListener {
            if (! binding.otpView!!.otp!!.isEmpty()) {

                val OTP = binding.otpView!!.otp!!.toString().trim()

                if (OTP.isEmpty()) {
                    //txtAlternateOTP!!.error = getString(R.string.enter_four_digit_otp)
                    binding.otpView!!.requestFocus()
                }
                else {
                    makeVerifyApiCall(OTP)
                }
            }
         }

        return view
    }

    private fun startSMSListener() {
        try {
            smsReceiver = MySMSBroadcastReceiver()
            smsReceiver.initOTPListener(this)

            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            contextRegister.registerReceiver(smsReceiver, intentFilter)

            val client = SmsRetriever.getClient(contextRegister)

            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }

            task.addOnFailureListener {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
 
    override fun onPause() {
        super.onPause()

        contextRegister.unregisterReceiver(smsReceiver)
    }

    fun makeVerifyApiCall(otp: String) {

        if (Connectivity.isOnline(contextRegister)) {

            binding.wp7progressBar.visibility = View.VISIBLE


            val call = apiInterface.verifyForgotPassword(
                mobileNumber, otp
            )

            call.enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {

                    binding.wp7progressBar.visibility = View.GONE

                    val responseData = response.body()!!

                    val isResponse = responseData.response

                    if (isResponse) {


                        val bundle = Bundle()
                        bundle.putString("phone", mobileNumber)
                        Connectivity.switchWelcome((activity as WelcomeActivity), ForgotPasswordFragment(),
                            "FORGOT_PASSWORD", bundle)

                       

                    } else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(activity!!)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.invalid_otp))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }

                }

                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {

                    binding.wp7progressBar.visibility = View.GONE

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                makeVerifyApiCall(otp)
                            }
                            .build()

                    mBottomSheetDialog.show()
                }

            })
        } else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity!!)
                    .setTitle(getString(R.string.uhoh))
                    .setMessage(getString(R.string.no_internet_found))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        makeVerifyApiCall(otp)
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    override fun onOTPReceived(otp: String) {

        binding.otpView!!.setOTP(otp)

        if (! binding.otpView!!.otp!!.isEmpty()) {

            val OTP = binding.otpView!!.otp!!.toString().trim()

            if (OTP.isEmpty()) {
                //txtAlternateOTP!!.error = getString(R.string.enter_four_digit_otp)
                binding.otpView!!.requestFocus()
            }
            else {
                makeVerifyApiCall(OTP)
            }
        }
    }

    override fun onOTPTimeOut() {}

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
 
}
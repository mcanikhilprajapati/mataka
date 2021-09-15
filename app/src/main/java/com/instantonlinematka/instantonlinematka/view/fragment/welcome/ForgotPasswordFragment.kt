package com.instantonlinematka.instantonlinematka.view.fragment.welcome

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.ForgotPasswordFragmentBinding
import com.instantonlinematka.instantonlinematka.model.ForgotPasswordResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.utility.autosms.AppSignatureHelper
import com.instantonlinematka.instantonlinematka.utility.autosms.MySMSBroadcastReceiver
import com.instantonlinematka.instantonlinematka.view.activity.WelcomeActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.android.synthetic.main.forgot_password_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("NAME_SHADOWING")
@SuppressLint("RestrictedApi,InflateParams")
class ForgotPasswordFragment : Fragment(), MySMSBroadcastReceiver.OTPReceiveListener {

    lateinit var binding: ForgotPasswordFragmentBinding

    lateinit var contextForgot: Context

    lateinit var mobileNumber: String
    lateinit var password: String
    lateinit var confirmPassword: String

    lateinit var apiInterface: ApiInterface

    var txtAlternateOTP: AppCompatEditText? = null
    lateinit var bottomSheet: BottomSheetDialog
    lateinit var MessageID: String

    lateinit var smsReceiver: MySMSBroadcastReceiver

    lateinit var mSessionPrefs: SessionPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ForgotPasswordFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextForgot = inflater.context

        mobileNumber = arguments!!.getString("phone").toString()

        apiInterface = RetrofitClient.getRetrfitInstance()

        mSessionPrefs = SessionPrefs(contextForgot)

        val appSignatureHelper = AppSignatureHelper(contextForgot)
        MessageID = appSignatureHelper.appSignatures.get(0);

//        val mBottomSheetDialog = BottomSheetDialog(activity!!)
//        val sheetView: View =
//            activity!!.layoutInflater.inflate(R.layout.sheet_enter_mobile_number, null)
//        mBottomSheetDialog.setContentView(sheetView)
//        mBottomSheetDialog.setCancelable(false)
//        mBottomSheetDialog.show()

//        val txtMobileNumber: AppCompatEditText = sheetView.findViewById(R.id.txtMobileNumber)
//        txtMobileNumber.setText(mobileNumber)
//
//        val btnSendOTP: AppCompatButton = sheetView.findViewById(R.id.btnSendOTP)
//
//        btnSendOTP.setSafeOnClickListener {
//
//            mobileNumber = txtMobileNumber.text.toString().trim()
//
//            if (mobileNumber.isEmpty()) {
//                txtMobileNumber.error = getString(R.string.mobile_number_required)
//                txtMobileNumber.requestFocus()
//            } else if (mobileNumber.length != 10) {
//                txtMobileNumber.error = getString(R.string.mobie_number_must_be_ten_digit)
//                txtMobileNumber.requestFocus()
//            } else {
//                makeSendOTPApiCall(mBottomSheetDialog)
//            }
//        }

        binding.btnResetPassword.setSafeOnClickListener {

            if (Connectivity.isOnline(contextForgot)) {

                password = txtPassword.text.toString().trim()
                confirmPassword = txtConfirmPassword.text.toString().trim()

                if (password.isEmpty()) {
                    binding.txtPassword.error = getString(R.string.please_enter_the_password)
                    binding.txtPassword.requestFocus()
                } else if (confirmPassword.isEmpty()) {
                    binding.txtConfirmPassword.error = getString(R.string.enter_confirm_password)
                    binding.txtConfirmPassword.requestFocus()
                } else if (password.length < 5) {
                    binding.txtPassword.error = getString(R.string.password_must_be_five_chars)
                    binding.txtPassword.requestFocus()
                } else if (!password.contentEquals(confirmPassword)) {
                    binding.txtConfirmPassword.error =
                        getString(R.string.password_confirm_do_not_match)
                    binding.txtConfirmPassword.requestFocus()
                } else {
                    makeResetPasswordApiCall()
                }
            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity!!)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.no_internet_found))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }

        }

        return view
    }

    fun makeSendOTPApiCall(bottomInitial: BottomSheetDialog) {

        if (Connectivity.isOnline(contextForgot)) {

            binding.wp7progressBar.showProgressBar()

            val call = apiInterface.forgotPassword(
                mobileNumber, MessageID
            )

            call.enqueue(object : Callback<ForgotPasswordResponse> {

                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {

                    binding.wp7progressBar.hideProgressBar()

                    startSMSListener()

                    val responseData = response.body()!!

                    val isResponse = responseData.response

                    if (isResponse) {

                        bottomInitial.dismiss()
//
//                        val bundle = Bundle()
//                        bundle.putString("mobile", mobileNumber)
//                        Connectivity.switchWelcome(
//                            activity!!, OtpFragment(),
//                            "OTP_FRAGMENT", bundle
//                        )

                        val mBottomSheetDialog = BottomSheetDialog(activity!!)
                        val sheetView: View =
                            activity!!.layoutInflater.inflate(R.layout.sheet_enter_otp, null)
                        mBottomSheetDialog.setContentView(sheetView)
                        mBottomSheetDialog.setCancelable(false)
                        mBottomSheetDialog.show()
                        bottomSheet = mBottomSheetDialog

                        val txtOTP: AppCompatEditText = sheetView.findViewById(R.id.txtOTPNumber)
                        txtAlternateOTP = txtOTP

                        val btnVerify: AppCompatButton = sheetView.findViewById(R.id.btnVerify)
                        val btnCancel: AppCompatButton = sheetView.findViewById(R.id.btnCancel)

                        btnCancel.visibility = View.GONE

                        btnVerify.setSafeOnClickListener {

                            val OTP = txtOTP.text.toString().trim()

                            if (OTP.isEmpty()) {
                                txtOTP.error = getString(R.string.enter_four_digit_otp)
                                txtOTP.requestFocus()
                            } else {
                                makeVerifyApiCall(OTP, mBottomSheetDialog)
                            }
                        }
                    } else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(activity!!)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.mobile_number_invalid))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }
                }

                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {

                    binding.wp7progressBar.hideProgressBar()

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                makeSendOTPApiCall(bottomInitial)
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
                        makeSendOTPApiCall(bottomInitial)
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    fun makeVerifyApiCall(otp: String, bottonSheet: BottomSheetDialog) {

        if (Connectivity.isOnline(contextForgot)) {

            binding.wp7progressBar.showProgressBar()

            val call = apiInterface.verifyForgotPassword(
                mobileNumber, otp
            )

            call.enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {

                    binding.wp7progressBar.hideProgressBar()

                    val responseData = response.body()!!

                    val isResponse = responseData.response

                    if (isResponse) {

                        bottonSheet.dismiss()

                        binding.txtPassword.isEnabled = true
                        binding.txtConfirmPassword.isEnabled = true
                        binding.btnResetPassword.isEnabled = true

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

                    binding.wp7progressBar.hideProgressBar()

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                makeVerifyApiCall(otp, bottonSheet)
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
                        makeVerifyApiCall(otp, bottonSheet)
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    fun makeResetPasswordApiCall() {

        binding.wp7progressBar.showProgressBar()

        val call = apiInterface.resetPassword(
            mobileNumber, password, confirmPassword
        )

        call.enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(
                call: Call<ForgotPasswordResponse>,
                response: Response<ForgotPasswordResponse>
            ) {

                binding.wp7progressBar.hideProgressBar()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val mDialog = Dialog(contextForgot)
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog.setCancelable(false)
                    mDialog.setContentView(R.layout.alert_success)
                    mDialog.window!!.setGravity(Gravity.CENTER)

                    mDialog.show()

                    val lblSuccessTitle = mDialog.lblSuccessTitleAnswer
                    val lblSuccessMessage = mDialog.lblSuccessMessageAnswer

                    lblSuccessTitle.text = getString(R.string.successful)
                    lblSuccessMessage.text = getString(R.string.password_reset_successful)

                    CoroutineScope(Main).launch {
                        delay(3000L)
                        mDialog.dismiss()
                        Connectivity.removeWelcomeFragment(activity as WelcomeActivity)
                    }

                } else {

                    binding.wp7progressBar.hideProgressBar()

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                binding.btnResetPassword.callOnClick()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }

            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {

                binding.wp7progressBar.hideProgressBar()

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity!!)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            binding.btnResetPassword.callOnClick()
                        }
                        .build()

                mBottomSheetDialog.show()
            }

        })

    }

    private fun startSMSListener() {
        try {
            smsReceiver = MySMSBroadcastReceiver()
            smsReceiver.initOTPListener(this)

            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            contextForgot.registerReceiver(smsReceiver, intentFilter)

            val client = SmsRetriever.getClient(contextForgot)

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

    override fun onOTPReceived(otp: String) {

        txtAlternateOTP!!.setText(otp)

        if (!txtAlternateOTP!!.text!!.isEmpty()) {

            val OTP = txtAlternateOTP!!.text.toString().trim()

            if (OTP.isEmpty()) {
                txtAlternateOTP!!.error = getString(R.string.enter_four_digit_otp)
                txtAlternateOTP!!.requestFocus()
            }
            else {
                makeVerifyApiCall(OTP, bottomSheet)
            }
        }
    }

    override fun onOTPTimeOut() {}

    override fun onPause() {
        super.onPause()

        contextForgot.unregisterReceiver(smsReceiver)
    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}
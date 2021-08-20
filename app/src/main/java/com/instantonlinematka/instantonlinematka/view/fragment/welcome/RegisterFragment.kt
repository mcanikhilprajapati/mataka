package com.instantonlinematka.instantonlinematka.view.fragment.welcome

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.RegisterFragmentBinding
import com.instantonlinematka.instantonlinematka.model.RegisterResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.utility.autosms.AppSignatureHelper
import com.instantonlinematka.instantonlinematka.utility.autosms.MySMSBroadcastReceiver
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.activity.WelcomeActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi,InflateParams")
class RegisterFragment : Fragment(), MySMSBroadcastReceiver.OTPReceiveListener {

    lateinit var binding: RegisterFragmentBinding

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

    lateinit var smsReceiver: MySMSBroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = RegisterFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextRegister = inflater.context

        sessionPrefs= SessionPrefs(contextRegister)

        mobileNumber = arguments!!.getString("mobile").toString()

        binding.txtMobileNumber.setText(mobileNumber)

        apiInterface = RetrofitClient.getRetrfitInstance()

        val appSignatureHelper = AppSignatureHelper(contextRegister)
        MessageID = appSignatureHelper.appSignatures.get(0);

        binding.btnRegister.setSafeOnClickListener {

            if (validation()) {
                makeSendOTPApiCall()
//                val bundle = Bundle()
//                bundle.putString("mobile", mobileNumber)
//                Connectivity.switchWelcome(
//                    activity!!, OtpFragment(),
//                    "REGISTER_FRAGMENT", bundle
//                )
            }
        }

        binding.chkReferral.setOnCheckedChangeListener { checkbox, isChecked ->

            if (isChecked) {
                binding.constraintReferral.visibility = View.VISIBLE
            }
            else {
                binding.constraintReferral.visibility = View.GONE
            }
        }

        return view
    }

    fun validation(): Boolean {

        mobileNumber = binding.txtMobileNumber.text.toString().trim()
        password = binding.txtPassword.text.toString().trim()
        confirmPassword = binding.txtConfirmPassword.text.toString()
        referral = binding.txtReferral.text.toString()

        if (mobileNumber.isEmpty()) {
            binding.txtMobileNumber.error = getString(R.string.enter_mobile_number)
            binding.txtMobileNumber.requestFocus()
            return false
        }
        else if (mobileNumber.length != 10) {
            binding.txtMobileNumber.error = getString(R.string.mobie_number_must_be_ten_digit)
            binding.txtMobileNumber.requestFocus()
            return false
        }
        else if (password.isEmpty()) {
            binding.txtPassword.error = getString(R.string.please_enter_the_password)
            binding.txtPassword.requestFocus()
            return false
        }
        else if (confirmPassword.isEmpty()) {
            binding.txtConfirmPassword.error = getString(R.string.enter_confirm_password)
            binding.txtConfirmPassword.requestFocus()
            return false
        }
        else if (password.length < 5) {
            binding.txtPassword.error = getString(R.string.password_must_be_five_chars)
            binding.txtPassword.requestFocus()
            return false
        }
        else if (!password.contentEquals(confirmPassword)) {
            binding.txtConfirmPassword.error = getString(R.string.password_confirm_do_not_match)
            binding.txtConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    // Send OTP
    fun makeSendOTPApiCall() {

        if (Connectivity.isOnline(contextRegister)) {

            binding.wp7progressBar.showProgressBar()

            val call = apiInterface.sendOTP(
                mobileNumber, mobileNumber, MessageID
            )

            call.enqueue(object : Callback<RegisterResponse> {

                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {

                    binding.wp7progressBar.hideProgressBar()

                    val responseData = response.body()!!

                    val isResponse = responseData.response

                    if (isResponse) {

                        startSMSListener()

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

                        btnCancel.setSafeOnClickListener {
                            mBottomSheetDialog.dismiss()
                        }

                        btnVerify.setSafeOnClickListener {

                            val OTP = txtOTP.text.toString().trim()

                            if (OTP.isEmpty()) {
                                txtOTP.error = getString(R.string.enter_four_digit_otp)
                                txtOTP.requestFocus()
                            }
                            else {
                                makeVerifyApiCall(OTP, bottomSheet)
                            }
                        }
                    }
                    else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(activity!!)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.something_went_wrong))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                    binding.btnRegister.callOnClick()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

                    binding.wp7progressBar.hideProgressBar()

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                binding.btnRegister.callOnClick()
                            }
                            .build()

                    mBottomSheetDialog.show()

                }

            })
        }
        else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity!!)
                    .setTitle(getString(R.string.uhoh))
                    .setMessage(getString(R.string.no_internet_found))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        binding.btnRegister.callOnClick()
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    fun makeVerifyApiCall(otp: String, bottonSheet: BottomSheetDialog) {

        if (Connectivity.isOnline(contextRegister)) {

            binding.wp7progressBar.showProgressBar()

            val call = apiInterface.verifyOTP(
                mobileNumber, mobileNumber, otp
            )

            call.enqueue(object: Callback<RegisterResponse>{
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {

                    val responseData = response.body()!!

                    val isResponse = responseData.response

                    if (isResponse) {

                        bottonSheet.dismiss()

                        makeUserRegisterApiCall()
                    }
                    else {

                        binding.wp7progressBar.hideProgressBar()

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

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

                    binding.wp7progressBar.hideProgressBar()

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                makeVerifyApiCall(otp, bottomSheet)
                            }
                            .build()

                    mBottomSheetDialog.show()
                }

            })
        }
        else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity!!)
                    .setTitle(getString(R.string.uhoh))
                    .setMessage(getString(R.string.no_internet_found))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        makeVerifyApiCall(otp, bottomSheet)
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    fun makeUserRegisterApiCall() {

        val call = apiInterface.register(
            mobileNumber, mobileNumber, password, confirmPassword, referral,
            sessionPrefs.getString(Constants.FCM_KEY)
        )

        call.enqueue(object: Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {

                binding.wp7progressBar.hideProgressBar()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val values = responseData.user

                    val id = values.id ?: "- - -"
                    val name = values.name ?: "- - -"
                    val phone = values.phone ?: "- - -"
                    val email = values.email ?: "- - -"
                    val gender = values.gender ?: "- - -"
                    val dob = values.dob ?: "- - -"
                    val address = values.address ?: "- - -"
                    val referal = values.referal ?: "- - -"
                    val wallet = values.wallet ?: "- - -"
                    val bank_name = values.bank_name ?: "- - -"
                    val bankholder_name = values.bankholder_name ?: "- - -"
                    val account_no = values.account_no ?: "- - -"
                    val ifc_code = values.Ifc_code ?: "- - -"
                    val profile_pic = values.profile_pic ?: "- - -"

                    sessionPrefs.addString(Constants.USER_ID, id)
                    sessionPrefs.addString(Constants.USER_NAME, name)
                    sessionPrefs.addString(Constants.USER_PHONE, phone)
                    sessionPrefs.addString(Constants.USER_EMAIL, email)
                    sessionPrefs.addString(Constants.USER_GENDER, gender)
                    sessionPrefs.addString(Constants.USER_DOB, dob)
                    sessionPrefs.addString(Constants.USER_ADDRESS, address)
                    sessionPrefs.addString(Constants.REFERRAL, referal)
                    sessionPrefs.addString(Constants.WALLET, wallet)
                    sessionPrefs.addString(Constants.BANK_NAME, bank_name)
                    sessionPrefs.addString(Constants.BANK_HOLDER_NAME, bankholder_name)
                    sessionPrefs.addString(Constants.BANK_ACCOUNT_NUMBER, account_no)
                    sessionPrefs.addString(Constants.BANK_IFSC_CODE, ifc_code)
                    sessionPrefs.addString(Constants.USER_PROF_PIC, profile_pic)

                    val mDialog = Dialog(contextRegister)
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog.setCancelable(false)
                    mDialog.setContentView(R.layout.alert_success)
                    mDialog.window!!.setGravity(Gravity.CENTER)

                    mDialog.show()

                    val lblSuccessTitle = mDialog.lblSuccessTitleAnswer
                    val lblSuccessMessage = mDialog.lblSuccessMessageAnswer

                    lblSuccessTitle.text = getString(R.string.instant_online_matka)
                    lblSuccessMessage.text = getString(R.string.register_successful)

                    CoroutineScope(Main).launch {
                        delay(3000L)

                        mDialog.dismiss()
                        val intent = Intent(contextRegister, DrawerActivity::class.java)
                        startActivity(intent)
                        (activity as WelcomeActivity).finish()
                    }

                }
                else {

                    binding.wp7progressBar.hideProgressBar()

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

                binding.wp7progressBar.hideProgressBar()

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity!!)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
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

        //contextRegister.unregisterReceiver(smsReceiver)
    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}
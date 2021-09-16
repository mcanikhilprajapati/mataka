package com.instantonlinematka.instantonlinematka.view.fragment.welcome

import `in`.aabhasjindal.otptextview.OTPListener
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.OtpFragmentBinding
import com.instantonlinematka.instantonlinematka.model.ForgotPasswordResponse
import com.instantonlinematka.instantonlinematka.model.RegisterResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.utility.autosms.MySMSBroadcastReceiver
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.activity.WelcomeActivity
import com.mukesh.OnOtpCompletionListener
import com.mukesh.OtpView
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi,InflateParams")
class OtpRegisterFragment : Fragment(),  View.OnClickListener, MySMSBroadcastReceiver.OTPReceiveListener  {

    lateinit var binding: OtpFragmentBinding

    lateinit var contextRegister: Context

    lateinit var apiInterface: ApiInterface

    lateinit var sessionPrefs: SessionPrefs

    lateinit var mobileNumber: String
    lateinit var password: String
    lateinit var confirmPassword: String
    lateinit var referral: String

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
        password = arguments!!.getString("password").toString()
        confirmPassword = arguments!!.getString("confirmPassword").toString()
        referral = arguments!!.getString("referral").toString()
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


            val call = apiInterface.verifyOTP(
                mobileNumber, mobileNumber, otp
            )

            call.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {

                    binding.wp7progressBar.visibility = View.GONE
                    val responseData = response.body()!!

                    val isResponse = responseData.response

                    if (isResponse) {


                        makeUserRegisterApiCall()
                    }
                    else {

                        binding.wp7progressBar.visibility = View.GONE

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

                binding.wp7progressBar.visibility = View.GONE

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
                    mDialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    mDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent);
                    mDialog.window!!.setGravity(Gravity.CENTER)

                    mDialog.show()

                    val lblSuccessTitle = mDialog.lblSuccessTitleAnswer
                    val lblSuccessMessage = mDialog.lblSuccessMessageAnswer

                    lblSuccessTitle.text = getString(R.string.instant_online_matka)
                    lblSuccessMessage.text = getString(R.string.register_successful)

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000L)

                        mDialog.dismiss()
                        val intent = Intent(contextRegister, DrawerActivity::class.java)
                        startActivity(intent)
                        (activity as WelcomeActivity).finish()
                    }

                }
                else {

                    binding.wp7progressBar.visibility = View.GONE

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

                binding.wp7progressBar.visibility = View.GONE

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

    override fun onClick(v: View?) {
//        if (v!!.id == binding.validate_button) {
//            Toast.makeText(this, otpView.getText(), Toast.LENGTH_SHORT).show()
//        }
    }
 
}
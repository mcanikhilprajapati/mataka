package com.instantonlinematka.instantonlinematka.view.fragment.welcome

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.LoginFragmentBinding
import com.instantonlinematka.instantonlinematka.model.LoginResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.activity.WelcomeActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class LoginFragment : Fragment() {

    lateinit var binding: LoginFragmentBinding

    lateinit var apiInterface: ApiInterface

    lateinit var sessionPrefs: SessionPrefs

    lateinit var contextLogin: Context

    lateinit var mobileNumber: String
    lateinit var password: String
    lateinit var fcm_token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LoginFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextLogin = inflater.context

        sessionPrefs= SessionPrefs(contextLogin)

        mobileNumber = arguments!!.getString("mobile").toString()

        binding.txtMobileNumber.setText(mobileNumber)

        apiInterface = RetrofitClient.getRetrfitInstance()

        binding.btnLogin.setSafeOnClickListener {

            mobileNumber = binding.txtMobileNumber.text.toString().trim()
            password = binding.txtPassword.text.toString().trim()
            fcm_token = sessionPrefs.getString(Constants.FCM_KEY)

            if (mobileNumber.isEmpty()) {
                binding.txtMobileNumber.error = getString(R.string.enter_mobile_number)
                binding.txtMobileNumber.requestFocus()
            }
            else if (mobileNumber.length != 10) {
                binding.txtMobileNumber.error = getString(R.string.mobie_number_must_be_ten_digit)
                binding.txtMobileNumber.requestFocus()
            }
            else if (password.isEmpty()) {
                binding.txtPassword.error = getString(R.string.please_enter_the_password)
                binding.txtPassword.requestFocus()
            }
            else {
                makeLoginApiCall()
            }
        }

        binding.lblForgotPassword.setSafeOnClickListener {
            val bundle = Bundle()
            bundle.putString("phone", mobileNumber)
            Connectivity.switchWelcome((activity as WelcomeActivity), ForgotPasswordFragment(),
            "FORGOT_PASSWORD", bundle)
        }

        return view
    }

    fun makeLoginApiCall() {

        if (Connectivity.isOnline(contextLogin)) {

            binding.wp7progressBar.showProgressBar()

            val call = apiInterface.login(
                mobileNumber, password, fcm_token, "IOM21"
            )

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    binding.wp7progressBar.hideProgressBar()

                    val responseData = response.body()!!

                    val isResponse = responseData.response

                    if (isResponse) {

                        val values = responseData.data

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
                        val gpay = values.google_pay ?: ""
                        val phonepe = values.phone_pay ?: ""
                        val paytm = values.paytm ?: ""
                        val notification_count = values.notification_count ?: ""

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
                        sessionPrefs.addString(Constants.NOTIFICATION_COUNT, notification_count)
                        sessionPrefs.addString(Constants.GPAY, gpay)
                        sessionPrefs.addString(Constants.PHONEPE, phonepe)
                        sessionPrefs.addString(Constants.PAYTM, paytm)

                        val intent = Intent(contextLogin, DrawerActivity::class.java)
                        startActivity(intent)
                        (activity as WelcomeActivity).finish()
                    }
                    else {

                        binding.wp7progressBar.hideProgressBar()

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(activity!!)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.something_went_wrong))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                    binding.btnLogin.callOnClick()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                    binding.wp7progressBar.hideProgressBar()

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.invalid_password))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
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
                        binding.btnLogin.callOnClick()
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

}
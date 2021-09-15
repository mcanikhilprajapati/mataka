package com.instantonlinematka.instantonlinematka.view.fragment.welcome

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.ForgotPasswrodOneFragmentBinding
import com.instantonlinematka.instantonlinematka.model.ForgotPasswordResponse
import com.instantonlinematka.instantonlinematka.model.WelcomeResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.autosms.AppSignatureHelper
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class ForgotPasswordOneFragment : Fragment() {

    lateinit var MessageID: String
    lateinit var contextForgot: Context

    lateinit var binding: ForgotPasswrodOneFragmentBinding

    lateinit var apiInterface: ApiInterface

    var mobileNumber = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ForgotPasswrodOneFragmentBinding.inflate(inflater, container, false)

        contextForgot = inflater.context

        apiInterface = RetrofitClient.getRetrfitInstance()


        val appSignatureHelper = AppSignatureHelper(contextForgot)
        MessageID = appSignatureHelper.appSignatures.get(0);
        mobileNumber = arguments!!.getString("phone").toString()
        binding.txtMobileNumber.setText(mobileNumber)

        binding.btnProceed.setSafeOnClickListener {

            mobileNumber = binding.txtMobileNumber.text.toString().trim()

            if (mobileNumber.isEmpty()) {
                binding.txtMobileNumber.error = getString(R.string.enter_mobile_number)
                binding.txtMobileNumber.requestFocus()
            }
            else if (mobileNumber.length != 10) {
                binding.txtMobileNumber.error = getString(R.string.mobie_number_must_be_ten_digit)
                binding.txtMobileNumber.requestFocus()
            }
            else {
                makeWelcomeApiCall()
            }
        }

        binding.btnPlayGame.setSafeOnClickListener {

            val items = arrayOf("English", "Hindi", "Telugu", "Kannada")

            val builder = AlertDialog.Builder(contextForgot)
            builder.setTitle(getString(R.string.make_your_selection))
            builder.setItems(items) { dialog, which ->

                if (which == 0) {

                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://youtu.be/we5WLIh_sHU")
                    )
                    startActivity(webIntent)

                } else if (which == 1) {

                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://youtu.be/WNeryxEdpFo")
                    )
                    startActivity(webIntent)

                } else if (which == 2) {

                    Toast.makeText(contextForgot, "Coming Soon!!", Toast.LENGTH_SHORT).show()

                } else if (which == 3) {

                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://youtu.be/gwD7qqFZvXI")
                    )
                    startActivity(webIntent)
                }
            }

            val alert = builder.create()
            alert.show()
        }

        return binding.root
    }

    fun makeWelcomeApiCall() {

        if (Connectivity.isOnline(contextForgot)) {

            binding.wp7progressBar.showProgressBar()

            binding.txtMobileNumber.isEnabled = false


            val call = apiInterface.forgotPassword(
                mobileNumber, MessageID
            )

            call.enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {

                    binding.wp7progressBar.hideProgressBar()
                    val bundle = Bundle()
                        bundle.putString("mobile", mobileNumber)
                        Connectivity.switchWelcome(
                            activity!!, OtpFragment(),
                            "OTP_FRAGMENT", bundle
                        )

//                    val responseData = response.body()!!
//
//                    val isValid = responseData.response
//
//                    if (isValid) {
//                        val bundle = Bundle()
//                        bundle.putString("mobile", mobileNumber)
//                        Connectivity.switchWelcome(
//                            activity!!, LoginFragment(),
//                            "LOGIN_FRAGMENT", bundle
//                        )
//                    } else {
//                        val bundle = Bundle()
//                        bundle.putString("mobile", mobileNumber)
//                        Connectivity.switchWelcome(
//                            activity!!, RegisterFragment(),
//                            "REGISTER_FRAGMENT", bundle
//                        )
//                    }

                }

                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {

                    binding.wp7progressBar.hideProgressBar()

                    binding.txtMobileNumber.isEnabled = true

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                binding.btnProceed.callOnClick()
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
                        binding.btnProceed.callOnClick()
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
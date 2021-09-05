package com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.bankdetails

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.BankDetailsFragmentBinding
import com.instantonlinematka.instantonlinematka.model.BankDetailsResponse
import com.instantonlinematka.instantonlinematka.model.CheckStatusResponse
import com.instantonlinematka.instantonlinematka.model.HashResponse
import com.instantonlinematka.instantonlinematka.model.UserInfoResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitCheckStatus
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class BankDetailsFragment : Fragment() {

    lateinit var binding: BankDetailsFragmentBinding

    lateinit var contextBank: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface
    lateinit var apiInterfaceStatus: ApiInterface

    var bankName: String? = null
    var bankAccountHolderName: String? = null
    var bankAccountNumber: String? = null
    var bankIFSCCode: String? = null
    var gpay: String? = null
    var phonepe: String? = null
    var paytm: String? = null

    var HashKey: String? = null

    var firstTime: Boolean? = null
    var fromData: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BankDetailsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        fromData = arguments!!.getString("from") ?: ""

        contextBank = inflater.context

        sessionPrefs = SessionPrefs(contextBank)

        apiInterface = RetrofitClient.getRetrfitInstance()
        apiInterfaceStatus = RetrofitCheckStatus.getRetrfitInstance()

        getUserInfo()

        binding.btnRetry.setSafeOnClickListener {
            getUserInfo()
        }

        binding.btnSubmit.setSafeOnClickListener {

            bankName = binding.txtBankName.text.toString()
            bankAccountHolderName = binding.txtBankAccountHolderName.text.toString()
            bankAccountNumber = binding.txtBankAccountNumber.text.toString()
            bankIFSCCode = binding.txtBankIFSC.text.toString()
            gpay = binding.txtGPay.text.toString()
            phonepe = binding.txtPhonePe.text.toString()
            paytm = binding.txtPayTM.text.toString()

            if (bankName!!.isEmpty() || bankAccountHolderName!!.isEmpty() ||
                bankAccountNumber!!.isEmpty() || bankIFSCCode!!.isEmpty()
            ) {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.fill_all_bank_details))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            } else {

                if (gpay!!.isNotEmpty() && (gpay!!.length < 10) ||
                    paytm!!.isNotEmpty() && (paytm!!.length < 10) ||
                    phonepe!!.isNotEmpty() && (phonepe!!.length < 10)
                ) {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.enter_ten_digits))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                } else {

                    if (Connectivity.isOnline(contextBank)) {
                        getHashKey()
                    } else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
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
            }
        }

        return view
    }

    fun getUserInfo() {

        if (Connectivity.isOnline(contextBank)) {
            makeUserInfoApiCall()
        } else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
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

    fun makeUserInfoApiCall() {

        binding.wp10progressBar.showProgressBar()

        binding.nestedView.visibility = View.GONE
        binding.linearHome.visibility = View.GONE

        val call = apiInterface.getUserInfo(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object : Callback<UserInfoResponse> {

            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val values = responseData.user

                    val id = values.id ?: ""
                    val name = values.name ?: ""
                    val phone = values.phone ?: ""
                    val email = values.email ?: ""
                    val gender = values.gender ?: ""
                    val dob = values.dob ?: ""
                    val address = values.address ?: ""
                    val referal = values.referal ?: ""
                    val wallet = values.wallet ?: ""
                    val bank_name = values.bank_name ?: ""
                    val bankholder_name = values.bankholder_name ?: ""
                    val account_no = values.account_no ?: ""
                    val ifc_code = values.Ifc_code ?: ""
                    val profile_pic = values.profile_pic ?: ""
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

                    ((activity as DrawerActivity).toolbar_Wallet.setText("₹"+wallet))

                    binding.txtBankName.setText(sessionPrefs.getString(Constants.BANK_NAME))
                    binding.txtBankAccountHolderName.setText(sessionPrefs.getString(Constants.BANK_HOLDER_NAME))
                    binding.txtBankAccountNumber.setText(sessionPrefs.getString(Constants.BANK_ACCOUNT_NUMBER))
                    binding.txtBankIFSC.setText(sessionPrefs.getString(Constants.BANK_IFSC_CODE))
                    binding.txtGPay.setText(sessionPrefs.getString(Constants.GPAY))
                    binding.txtPhonePe.setText(sessionPrefs.getString(Constants.PHONEPE))
                    binding.txtPayTM.setText(sessionPrefs.getString(Constants.PAYTM))

                    binding.nestedView.visibility = View.VISIBLE
                    binding.linearHome.visibility = View.GONE

                    if (firstTime == false && fromData!!.contentEquals("WITHDRAWAL")) {
                        Connectivity.removeDrawerFragment(activity as DrawerActivity)
                    }
                    firstTime = true

                } else {

                    binding.nestedView.visibility = View.GONE
                    binding.linearHome.visibility = View.VISIBLE

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()

                }

                binding.wp10progressBar.hideProgressBar()

            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {

                binding.wp10progressBar.showProgressBar()

                binding.nestedView.visibility = View.GONE
                binding.linearHome.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            getUserInfo()
                        }
                        .build()

                mBottomSheetDialog.show()
            }

        })

    }

    fun getHashKey() {

        binding.lblVerifyingDetails.visibility = View.VISIBLE

        binding.wp10progressBar.showProgressBar()

        binding.nestedView.visibility = View.GONE
        binding.linearHome.visibility = View.GONE

        val jsonArray = JsonArray()

        val jsonObject = JsonObject() // /sub Object

        try {

            jsonObject.addProperty("api_key", "1ff0832d-4928-4db9-ac51-c2e34f3d8b09")
            jsonObject.addProperty("bank_name", bankName!!)
            jsonObject.addProperty("account_name", bankAccountHolderName!!)
            jsonObject.addProperty("account_number", bankAccountNumber!!)
            jsonObject.addProperty("ifsc_code", bankIFSCCode!!)
            jsonArray.add(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val call = apiInterface.getHashKey(
            jsonArray.toString(), "6f5ccf496a142e91814d53b0989fab5772c4133a"
        )

        call.enqueue(object : Callback<HashResponse> {
            override fun onResponse(call: Call<HashResponse>, response: Response<HashResponse>) {

                val data = response.body()!!
                HashKey = data.hash ?: ""

                if (HashKey!!.isNotEmpty()) {
                    makeUpdateBankApiCall()
                } else {
                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                getHashKey()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }
            }

            override fun onFailure(call: Call<HashResponse>, t: Throwable) {}

        });

    }

    fun makeUpdateBankApiCall() {

        val callCheckStatus = apiInterfaceStatus.getAccountCheck(
            "1ff0832d-4928-4db9-ac51-c2e34f3d8b09", bankName!!, bankAccountHolderName!!,
            bankAccountNumber!!, bankIFSCCode!!, HashKey!!
        )

        callCheckStatus.enqueue(object : Callback<CheckStatusResponse> {

            override fun onResponse(
                call: Call<CheckStatusResponse>,
                response: Response<CheckStatusResponse>
            ) {

                val responseData = response.body()!!

                val isResponse = responseData.data

                if (isResponse != null) {

                    if (isResponse.status.contentEquals("SUCCESS")) {

                        binding.lblVerifyingDetails.visibility = View.GONE

                        firstTime = false

                        updateDetails()

                    } else {

                        binding.lblVerifyingDetails.visibility = View.GONE

                        binding.wp10progressBar.hideProgressBar()

                        binding.nestedView.visibility = View.VISIBLE
                        binding.linearHome.visibility = View.GONE

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.invalid_ifsc))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()

                    }

                } else {

                    binding.lblVerifyingDetails.visibility = View.GONE

                    binding.wp10progressBar.hideProgressBar()

                    binding.nestedView.visibility = View.VISIBLE
                    binding.linearHome.visibility = View.GONE

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.invalid_ifsc))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }

            }

            override fun onFailure(call: Call<CheckStatusResponse>, t: Throwable) {

                binding.wp10progressBar.showProgressBar()

                binding.nestedView.visibility = View.GONE
                binding.linearHome.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            getUserInfo()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        })

    }

    fun updateDetails() {

        val call = apiInterface.updateBankDetails(
            sessionPrefs.getString(Constants.USER_ID), bankName!!, bankAccountHolderName!!,
            bankIFSCCode!!, bankAccountNumber!!
        )

        call.enqueue(object : Callback<BankDetailsResponse> {

            override fun onResponse(
                call: Call<BankDetailsResponse>,
                response: Response<BankDetailsResponse>
            ) {

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    if (gpay!!.isNotEmpty() || paytm!!.isNotEmpty() || phonepe!!.isNotEmpty()) {

                        if (Connectivity.isOnline(contextBank)) {
                            makeGPayApiCall()
                        } else {

                            val mBottomSheetDialog: BottomSheetMaterialDialog =
                                BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                                    .setTitle(getString(R.string.uhoh))
                                    .setMessage(getString(R.string.no_internet_found))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                        dialogInterface.dismiss()
                                    }
                                    .build()

                            mBottomSheetDialog.show()
                        }
                    } else {

                        val mDialog = Dialog(contextBank)
                        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        mDialog.setCancelable(false)
                        mDialog.setContentView(R.layout.alert_success)
                        mDialog.window!!.setGravity(Gravity.CENTER)

                        mDialog.show()

                        val lblSuccessTitle = mDialog.lblSuccessTitleAnswer
                        val lblSuccessMessage = mDialog.lblSuccessMessageAnswer

                        lblSuccessTitle.text = getString(R.string.successful)
                        lblSuccessMessage.text = getString(R.string.bank_updated)

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(3000L)

                            mDialog.dismiss()
                            getUserInfo()

                        }

                        binding.wp10progressBar.hideProgressBar()
                    }

                } else {

                    binding.nestedView.visibility = View.GONE
                    binding.linearHome.visibility = View.VISIBLE

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
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

            override fun onFailure(call: Call<BankDetailsResponse>, t: Throwable) {

                binding.wp10progressBar.showProgressBar()

                binding.nestedView.visibility = View.GONE
                binding.linearHome.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            getUserInfo()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        })
    }

    fun makeGPayApiCall() {

        val call = apiInterface.updatePaymentNumber(
            sessionPrefs.getString(Constants.USER_ID), gpay!!, paytm!!, phonepe!!
        )

        call.enqueue(object : Callback<BankDetailsResponse> {

            override fun onResponse(
                call: Call<BankDetailsResponse>,
                response: Response<BankDetailsResponse>
            ) {

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val mDialog = Dialog(contextBank)
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog.setCancelable(false)
                    mDialog.setContentView(R.layout.alert_success)
                    mDialog.window!!.setGravity(Gravity.CENTER)

                    mDialog.show()

                    val lblSuccessTitle = mDialog.lblSuccessTitleAnswer
                    val lblSuccessMessage = mDialog.lblSuccessMessageAnswer

                    lblSuccessTitle.text = getString(R.string.successful)
                    lblSuccessMessage.text = getString(R.string.bank_updated)

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000L)

                        mDialog.dismiss()
                        getUserInfo()

                    }

                } else {

                    binding.nestedView.visibility = View.GONE
                    binding.linearHome.visibility = View.VISIBLE

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()

                }

                binding.wp10progressBar.hideProgressBar()

            }

            override fun onFailure(call: Call<BankDetailsResponse>, t: Throwable) {

                binding.wp10progressBar.showProgressBar()

                binding.nestedView.visibility = View.GONE
                binding.linearHome.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            getUserInfo()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        })
    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

}
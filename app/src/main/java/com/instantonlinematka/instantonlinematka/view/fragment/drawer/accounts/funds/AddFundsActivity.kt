package com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.AddWalletDetailsAdapter
import com.instantonlinematka.instantonlinematka.model.*
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitCheckStatus
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.NotificationActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import com.test.pg.secure.pgsdkv4.PGConstants
import com.test.pg.secure.pgsdkv4.PaymentGatewayPaymentInitializer
import com.test.pg.secure.pgsdkv4.PaymentParams
import kotlinx.android.synthetic.main.add_fund_activity.*
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("RestrictedApi")
class AddFundsActivity : AppCompatActivity() {

    lateinit var contextAddFund: Context

    lateinit var apiInterface: ApiInterface
    lateinit var apiInterfaceStatus: ApiInterface

    lateinit var sessionPrefs: SessionPrefs

    lateinit var paymentList: ArrayList<UserPayment>
    lateinit var responseList: ArrayList<String>

    lateinit var walletAdapter: AddWalletDetailsAdapter

    var Selection = ""
    val PAYMENT_REQUEST_CODE = 12
    var PreviousData = ""

    var VPA = ""
    var PayeeName = "LOKESH"
    var UniqueTransactionID: String? = null
    var UniqueID: String? = null
    var UniqueReferID: String? = null
    var Amount: String? = null

    lateinit var hashList: ArrayList<PaymentHashSetterGetter>

    var HashKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_fund_activity)

        contextAddFund = this@AddFundsActivity

        apiInterface = RetrofitClient.getRetrfitInstance()
        apiInterfaceStatus = RetrofitCheckStatus.getRetrfitInstance()

        sessionPrefs = SessionPrefs(contextAddFund)

        paymentList = ArrayList()
        hashList = ArrayList()
        responseList = ArrayList()

        walletAdapter = AddWalletDetailsAdapter(
            contextAddFund, this@AddFundsActivity,
            paymentList, txtEnterAmount, wp10progressBar, constraintHide, btnHowToAddFund,
            apiInterface, apiInterfaceStatus, sessionPrefs
        )

        recyclerView.apply {
            layoutManager = GridLayoutManager(
                contextAddFund, 1,
                RecyclerView.VERTICAL, false
            )
            adapter = walletAdapter
        }

        btnHowToAddFund.setOnClickListener {

            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://youtu.be/QlNayo1qEnQ")
            )
            startActivity(webIntent)
        }

        btnRequestFiveHundred.setSafeOnClickListener {
            txtEnterAmount.setText("500")
        }

        btnRequestOneThousand.setSafeOnClickListener {
            txtEnterAmount.setText("1000")
        }

        btnRequestOneThousandFiveHundred.setSafeOnClickListener {
            txtEnterAmount.setText("1500")
        }

        /*btnPayTm.setSafeOnClickListener { v ->

            val isAppInstalled = appInstalledOrNot("net.one97.paytm")
            if (isAppInstalled) {
                SelectionPayTm()
            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.app_not_installed))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        }*/

        /*btnPhonePe.setSafeOnClickListener { v ->

            val isAppInstalled = appInstalledOrNot("com.phonepe.app")
            if (isAppInstalled) {
                SelectionPhonePe()
            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.app_not_installed))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        }

        btnGPay.setSafeOnClickListener { v ->

            val isAppInstalled = appInstalledOrNot("com.google.android.apps.nbu.paisa.user")
            if (isAppInstalled) {
                SelectionGPay()
            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.app_not_installed))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        }

        btnBhim.setOnClickListener {

            val isAppInstalled = appInstalledOrNot("in.org.npci.upiapp")
            if (isAppInstalled) {
                SelectingBhim()
            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.app_not_installed))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        }

        btnPG.setSafeOnClickListener { v ->
            SelectionNetBanking()
        }*/

        getWalletDetails()

        circleImageView.setSafeOnClickListener {
            onBackPressed()
        }

        btnNotification.setSafeOnClickListener {
            startActivity(Intent(contextAddFund, NotificationActivity::class.java))
        }
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

    fun getWalletDetails() {
        if (Connectivity.isOnline(contextAddFund)) {
            makeWalletDetailsApiCall()
        } else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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

    fun makeWalletDetailsApiCall() {

        wp10progressBar.visibility = View.VISIBLE

        constraintHide.visibility = View.GONE
        btnHowToAddFund.visibility = View.GONE

        val call = apiInterface.getWalletBalance(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object : Callback<WalletBalanceResponse> {
            override fun onResponse(
                call: Call<WalletBalanceResponse>,
                response: Response<WalletBalanceResponse>
            ) {

                val data = response.body()!!

                val isResponse = data.response

                if (isResponse) {

                    paymentList.clear()

                    wp10progressBar.visibility = View.GONE

                    constraintHide.visibility = View.VISIBLE
                    btnHowToAddFund.visibility = View.GONE //

                    sessionPrefs.addString(Constants.WALLET, data.user.wallet)
                    toolbar_Wallet.setText("₹"+data.user.wallet)

                    for (items in data.payment) {

                        val id = items.id
                        val name = items.name
                        val code = items.code
                        val status = items.status

                        if (status == 3) {
                            paymentList.add(UserPayment(id, name, code, status))
                        }
                    }

                    walletAdapter.notifyDataSetChanged()

                } else {

                    wp10progressBar.visibility = View.GONE

                    constraintHide.visibility = View.GONE
                    btnHowToAddFund.visibility = View.GONE

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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

            override fun onFailure(call: Call<WalletBalanceResponse>, t: Throwable) {

                wp10progressBar.visibility = View.GONE

                constraintHide.visibility = View.VISIBLE
                btnHowToAddFund.visibility = View.GONE //

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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

    fun SelectionPayTm() {
        Amount = txtEnterAmount.text.toString().trim()
        Selection = Constants.PAYTM
        AddBalance(Amount)
    }

    fun SelectionPhonePe() {
        Amount = txtEnterAmount.text.toString().trim()
        Selection = Constants.PHONEPE
        AddBalance(Amount)
    }

    fun SelectionGPay() {
        Amount = txtEnterAmount.text.toString().trim()
        Selection = Constants.GPAY
        AddBalance(Amount)
    }

    fun SelectingBhim() {
        Amount = txtEnterAmount.text.toString().trim()
        Selection = Constants.BHIM
        AddBalance(Amount)
    }

    fun SelectionNetBanking() {
        Amount = txtEnterAmount.text.toString().trim()
        Selection = "TRAKNPAY"
        AddBalance(Amount)
    }

    fun AddBalance(amount: String?) {

        if (amount.equals("", ignoreCase = true)) {
            txtEnterAmount.setError(getString(R.string.enter_an_amount))
            txtEnterAmount.requestFocus()
        } /*else if (amount!!.toInt() < 500) {
            txtEnterAmount.setError(getString(R.string.minimum_deposit))
            txtEnterAmount.requestFocus()
        }*/ else {
            Amount = amount
            Amount = "$Amount.00"
            val splitter = Amount!!.split("\\.".toRegex()).toTypedArray()
            val strAmountLengthAfterDec = splitter[1].length
            if (strAmountLengthAfterDec == 2) {
                if (Connectivity.isOnline(contextAddFund)) {
                    AddFunds()
                } else {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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
                txtEnterAmount.setError(getString(R.string.must_have_digits_after_decimal_point))
                txtEnterAmount.requestFocus()
            }
        }
    }

    fun AddFunds() {

        wp10progressBar.visibility = View.VISIBLE

        constraintHide.visibility = View.GONE
        btnHowToAddFund.visibility = View.GONE

        //UtilityMethods.showToastSucess(getActivity(), "Transaction Successful", true);
        UniqueTransactionID = UUID.randomUUID().toString()
        UniqueTransactionID = UniqueTransactionID!!.substring(0, 7)
        UniqueReferID = UniqueTransactionID + "ReferID"

        val call = apiInterface.addFund(
            sessionPrefs.getString(Constants.USER_ID), Amount!!, UniqueTransactionID!!
        )

        call.enqueue(object : Callback<AddFundsResponse> {

            override fun onResponse(
                call: Call<AddFundsResponse>,
                response: Response<AddFundsResponse>
            ) {

                val data = response.body()!!

                val isResponse = data.response

                PreviousData = data.user

                if (isResponse) {

                    if (Connectivity.isOnline(contextAddFund)) {
                        getHashKey()
                    } else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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

                    wp10progressBar.visibility = View.GONE

                    constraintHide.visibility = View.VISIBLE
                    btnHowToAddFund.visibility = View.GONE //

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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

            override fun onFailure(call: Call<AddFundsResponse>, t: Throwable) {

                wp10progressBar.visibility = View.GONE

                constraintHide.visibility = View.VISIBLE
                btnHowToAddFund.visibility = View.GONE //

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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

    fun getHashKey() {

        hashList.clear()

        var userName = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)
        if (userName.isEmpty()) {
            userName = "instant online matka user"
        }

        hashList.add(
            PaymentHashSetterGetter(
                Constants.PG_API_KEY,
                UniqueTransactionID,
                Amount,
                Constants.PG_CURRENCY,
                Constants.PG_DESCRIPTION,
                userName,
                "android@gmail.com",
                sessionPrefs.getString(Constants.USER_PHONE),
                Constants.PG_CITY,
                Constants.PG_COUNTRY,
                Constants.PG_ZIPCODE,
                Constants.PG_RETURN_URL
            )
        )

        val hashDetails = itemsToConvert(hashList).toString()

        val call = apiInterface.getHashKey(
            hashDetails, "6f5ccf496a142e91814d53b0989fab5772c4133a"
        )

        call.enqueue(object : Callback<HashResponse> {
            override fun onResponse(call: Call<HashResponse>, response: Response<HashResponse>) {

                val data = response.body()!!
                HashKey = data.hash ?: ""

                if (HashKey.isNotEmpty()) {
                    makePaymentURLApiCall()
                } else {
                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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

    fun makePaymentURLApiCall() {

        var userName = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)
        if (userName.isEmpty()) {
            userName = "instant online matka user"
        }

        val call = apiInterfaceStatus.PaymentRequest(
            Constants.PG_API_KEY,
            UniqueTransactionID!!,
            Amount!!,
            Constants.PG_CURRENCY,
            Constants.PG_DESCRIPTION,
            userName,
            "android@gmail.com",
            sessionPrefs.getString(Constants.USER_PHONE),
            Constants.PG_CITY,
            Constants.PG_COUNTRY,
            Constants.PG_ZIPCODE,
            Constants.PG_RETURN_URL,
            HashKey
        )

        call.enqueue(object : Callback<PaymentGatewayResponse> {
            override fun onResponse(
                call: Call<PaymentGatewayResponse>,
                response: Response<PaymentGatewayResponse>
            ) {
                HashKey = ""

                val resData = response.body()!!

                VPA = resData.data!!.vpa!!

                when (Selection) {

                    Constants.PHONEPE -> {
                        InitiateTransaction(Constants.PHONEPE)
                    }

                    Constants.PAYTM -> {
                        InitiateTransaction(Constants.PAYTM)
                    }

                    Constants.GPAY -> {
                        InitiateTransaction(Constants.GPAY)
                    }

                    Constants.BHIM -> {
                        InitiateTransaction(Constants.BHIM)
                    }

                    "TRAKNPAY" -> {
                        InitiatePaymentGateway()

                        /*val intent = Intent(contextAddFund, PaymentGatewayActivity::class.java)
                        intent.putExtra("TransId", UniqueTransactionID)
                        intent.putExtra("ReferTransId", UniqueReferID)
                        intent.putExtra("Amount", Amount)
                        startActivityForResult(intent, UPIREQUESTCODE)*/
                    }
                }

                wp10progressBar.visibility = View.GONE

                constraintHide.visibility = View.VISIBLE
                btnHowToAddFund.visibility = View.GONE //
            }

            override fun onFailure(call: Call<PaymentGatewayResponse>, t: Throwable) {}

        })
    }

    fun InitiateTransaction(Wallet: String) {

        Amount = Amount!!.replaceFirst("^0+(?!$)".toRegex(), "")
        val amountSplit = Amount!!.split("\\.".toRegex()).toTypedArray()

        Amount = amountSplit[0] + "." + amountSplit[1]

        var userName = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)
        if (userName.isEmpty()) {
            userName = "instant online matka user"
        }

        if (Wallet.contentEquals(Constants.GPAY)) {

            val uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", VPA)
                .appendQueryParameter("pn", "In Games Zones")
                .appendQueryParameter("tn", UniqueReferID)
                .appendQueryParameter("am", Amount)
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tr", UniqueTransactionID)
                .appendQueryParameter("tid", UniqueTransactionID)
                .appendQueryParameter("url", "https://www.ingameszones.com/")
                .build()

            val upiPayIntent = Intent(Intent.ACTION_VIEW)
            upiPayIntent.setData(uri)
            upiPayIntent.setPackage("com.google.android.apps.nbu.paisa.user");
            startActivityForResult(upiPayIntent, PAYMENT_REQUEST_CODE)

        } else if (Wallet.contentEquals(Constants.PAYTM)) {

            val uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", VPA)
                .appendQueryParameter("pn", "In Games Zones")
                .appendQueryParameter("tn", UniqueReferID)
                .appendQueryParameter("am", Amount)
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tr", UniqueTransactionID)
                .appendQueryParameter("tid", UniqueTransactionID)
                .appendQueryParameter("url", "https://www.ingameszones.com/")
                .build()

            val upiPayIntent = Intent(Intent.ACTION_VIEW)
            upiPayIntent.data = uri
            upiPayIntent.setPackage("net.one97.paytm");
            startActivityForResult(upiPayIntent, PAYMENT_REQUEST_CODE)

        } else if (Wallet.contentEquals(Constants.PHONEPE)) {

            val uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", VPA)
                .appendQueryParameter("pn", "In Games Zones")
                .appendQueryParameter("tn", UniqueReferID)
                .appendQueryParameter("am", Amount)
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tr", UniqueTransactionID)
                .appendQueryParameter("tid", UniqueTransactionID)
                .appendQueryParameter("url", "https://www.ingameszones.com/")
                .build()

            val upiPayIntent = Intent(Intent.ACTION_VIEW)
            upiPayIntent.data = uri
            upiPayIntent.setPackage("com.phonepe.app");
            startActivityForResult(upiPayIntent, PAYMENT_REQUEST_CODE)

        } else if (Wallet.contentEquals(Constants.BHIM)) {

            val uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", VPA)
                .appendQueryParameter("pn", "In Games Zones")
                .appendQueryParameter("tn", UniqueReferID)
                .appendQueryParameter("am", Amount)
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tr", UniqueTransactionID)
                .appendQueryParameter("tid", UniqueTransactionID)
                .appendQueryParameter("url", "https://www.ingameszones.com/")
                .build()

            val upiPayIntent = Intent(Intent.ACTION_VIEW)
            upiPayIntent.data = uri
            upiPayIntent.setPackage("in.org.npci.upiapp");
            startActivityForResult(upiPayIntent, PAYMENT_REQUEST_CODE)
        }

    }

    fun makeRequestAddFundApiCall() {

        wp10progressBar.visibility = View.VISIBLE

        constraintHide.visibility = View.GONE
        btnHowToAddFund.visibility = View.GONE

        val call = apiInterface.requestToAddFund(
            sessionPrefs.getString(Constants.USER_ID), txtEnterAmount.text.toString().trim(),
            AddWalletDetailsAdapter.UniqueTransactionID!!,
            AddWalletDetailsAdapter.PreviousData, "IOM45"
        )

        call.enqueue(object : Callback<AddFundsResponse> {

            override fun onResponse(
                call: Call<AddFundsResponse>,
                response: Response<AddFundsResponse>
            ) {

                val data = response.body()!!

                val isResponse = data.response

                if (isResponse) {

                    val mDialog = Dialog(contextAddFund)
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog.setCancelable(false)
                    mDialog.setContentView(R.layout.alert_success)
                    mDialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    mDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent);

                    mDialog.window!!.setGravity(Gravity.CENTER)

                    mDialog.show()

                    val lblSuccessTitle = mDialog.lblSuccessTitleAnswer
                    val lblSuccessMessage = mDialog.lblSuccessMessageAnswer

                    lblSuccessTitle.text = getString(R.string.successful)
                    lblSuccessMessage.text = getString(R.string.wallet_updated)

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000L)
                        mDialog.dismiss()
                    }

                    getWalletDetails()

                    wp10progressBar.visibility = View.GONE

                    constraintHide.visibility = View.VISIBLE
                    btnHowToAddFund.visibility = View.GONE //

                } else {

                    wp10progressBar.visibility = View.GONE

                    constraintHide.visibility = View.VISIBLE
                    btnHowToAddFund.visibility = View.GONE //

                    val message = data.message

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }
            }

            override fun onFailure(call: Call<AddFundsResponse>, t: Throwable) {

                wp10progressBar.visibility = View.GONE

                constraintHide.visibility = View.VISIBLE
                btnHowToAddFund.visibility = View.GONE //

                Toast.makeText(contextAddFund, "Here: " + t.message, Toast.LENGTH_LONG).show()

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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

    fun InitiatePaymentGateway() {

        Constants.PG_ORDER_ID = UniqueReferID!!

        var userName = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)
        if (userName.isEmpty()) {
            userName = "instant online matka user"
        }

        val pgPaymentParams = PaymentParams()
        pgPaymentParams.aPiKey = Constants.PG_API_KEY
        pgPaymentParams.amount = Amount!!
        pgPaymentParams.email = Constants.PG_EMAIL
        pgPaymentParams.name = userName
        pgPaymentParams.phone = sessionPrefs.getString(Constants.USER_PHONE)
        pgPaymentParams.orderId = Constants.PG_ORDER_ID
        pgPaymentParams.currency = Constants.PG_CURRENCY
        pgPaymentParams.description = Constants.PG_DESCRIPTION
        pgPaymentParams.city = Constants.PG_CITY
        pgPaymentParams.state = Constants.PG_STATE
        pgPaymentParams.addressLine1 = Constants.PG_ADD_1
        pgPaymentParams.addressLine2 = Constants.PG_ADD_2
        pgPaymentParams.zipCode = Constants.PG_ZIPCODE
        pgPaymentParams.country = Constants.PG_COUNTRY
        pgPaymentParams.setReturnUrl(Constants.PG_RETURN_URL)
        pgPaymentParams.mode = Constants.PG_MODE
        pgPaymentParams.setUdf1(Constants.PG_UDF1)
        pgPaymentParams.setUdf2(Constants.PG_UDF2)
        pgPaymentParams.setUdf3(Constants.PG_UDF3)
        pgPaymentParams.setUdf4(Constants.PG_UDF4)
        pgPaymentParams.setUdf5(Constants.PG_UDF5)
        pgPaymentParams.enableAutoRefund = "n"
        val pgPaymentInitialzer =
            PaymentGatewayPaymentInitializer(pgPaymentParams, this@AddFundsActivity)
        pgPaymentInitialzer.initiatePaymentProcess()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PGConstants.REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                try {
                    val paymentResponse = data!!.getStringExtra(PGConstants.PAYMENT_RESPONSE)

                    val response = JSONObject(paymentResponse!!)

                    val responseCode = response.getInt("response_code")

                    if (!(responseCode == 0)) { // 0 -> Successful
                        println("Transaction Error!")
                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(response.getString("error_desc"))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    } else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                                .setTitle(getString(R.string.successful))
                                .setMessage(response.getString("response_message"))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()

                                    if (Connectivity.isOnline(contextAddFund)) {
                                        //UtilityMethods.showToastSucess(getActivity(), "Transaction Successful", true);
                                        UniqueTransactionID = response.getString("transaction_id")
                                        val Unique = UniqueTransactionID!!.substring(0, 7)
                                        UniqueReferID = Unique + "ReferID"
                                        makeRequestAddFundApiCall()
                                    } else {

                                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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
                                .build()

                        mBottomSheetDialog.show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.transaction_failed_or_cancelled))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        } else if (requestCode == PAYMENT_REQUEST_CODE) {

            if (Connectivity.isOnline(contextAddFund)) {
                //UtilityMethods.showToastSucess(getActivity(), "Transaction Successful", true);
                makeRequestAddFundApiCall()
            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.no_internet_found))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }

            /*if (resultCode == Activity.RESULT_OK) {

                callStatusCheckAPI()
            } else if (resultCode == Activity.RESULT_CANCELED) {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.transaction_failed_or_cancelled))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }*/

            /*if (AddWalletDetailsAdapter.fromGPAY == true) {

                if (resultCode == Activity.RESULT_OK) {

                    responseList.clear()

                    if (data != null) {

                        try {
                            val trxt = data.getStringExtra("response")!!

                            responseList.add(trxt)
                            upiPaymentDataOperation(responseList)
                        } catch (e: Exception) {

                            val mBottomSheetDialog: BottomSheetMaterialDialog =
                                BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                                    .setTitle(getString(R.string.uhoh))
                                    .setMessage(getString(R.string.transaction_failed_or_cancelled))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                        dialogInterface.dismiss()
                                    }
                                    .build()

                            mBottomSheetDialog.show()
                        }
                    } else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.transaction_failed_or_cancelled))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.transaction_failed_or_cancelled))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }
            } else {
                if (resultCode == Activity.RESULT_OK) {

                    callStatusCheckAPI()
                } else if (resultCode == Activity.RESULT_CANCELED) {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.transaction_failed_or_cancelled))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }
            }*/
        }

    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

    //Json converter
    fun itemsToConvert(list: ArrayList<PaymentHashSetterGetter>): JsonArray {

        val jsonArray = JsonArray()

        for (items in list) {

            val jsonObject = JsonObject() // /sub Object

            try {

                jsonObject.addProperty("api_key", items.api_key)
                jsonObject.addProperty("order_id", items.order_id)
                jsonObject.addProperty("amount", items.amount)
                jsonObject.addProperty("currency", items.currency)
                jsonObject.addProperty("description", items.description)
                jsonObject.addProperty("name", items.name)
                jsonObject.addProperty("email", items.email)
                jsonObject.addProperty("phone", items.phone)
                jsonObject.addProperty("city", items.city)
                jsonObject.addProperty("country", items.country)
                jsonObject.addProperty("zip_code", items.zip_code)
                jsonObject.addProperty("return_url", items.return_url)
                jsonArray.add(jsonObject)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        return jsonArray

    }

    fun upiPaymentDataOperation(data: ArrayList<String>) {

        val str: String = data.get(0)

        var paymentCancel = ""

        var status = ""
        var approvalRefNo = ""

        //Toast.makeText(contextAddFund, "" + str, Toast.LENGTH_LONG).show()

        val response = str.split("&".toRegex()).toTypedArray()
        for (i in response.indices) {
            val equalStr = response[i].split("=".toRegex()).toTypedArray()
            if (equalStr.size >= 2) {
                if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {

                    status = equalStr[1].toLowerCase()

                    //Toast.makeText(contextAddFund, "" + status, Toast.LENGTH_LONG).show()

                    if (status.contentEquals("failed") ||
                        status.contentEquals("failure") ||
                        status.contentEquals("fail")
                    ) {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.transaction_failed_or_cancelled))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    } else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                                .setTitle(getString(R.string.successful))
                                .setMessage(getString(R.string.payment_successful_wallet_updated))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()

                                    if (Connectivity.isOnline(contextAddFund)) {
                                        //UtilityMethods.showToastSucess(getActivity(), "Transaction Successful", true);
                                        makeRequestAddFundApiCall()
                                    } else {

                                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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
                                .build()

                        mBottomSheetDialog.show()
                    }
                } else if (equalStr[0].toLowerCase() == "ApprovalRefNo".toLowerCase() ||
                    equalStr[0].toLowerCase() == "txnRef".toLowerCase()
                ) {
                    approvalRefNo = equalStr[1]
                }
            } else {
                paymentCancel = "Payment cancelled by user."
            }
        }
    }


    // Call Status Check API
    fun callStatusCheckAPI() {

        if (Connectivity.isOnline(contextAddFund)) {
            getStatus()
        } else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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

    // Get Status
    fun getStatus() {

        wp10progressBar.visibility = View.VISIBLE

        constraintHide.visibility = View.GONE
        btnHowToAddFund.visibility = View.GONE

        val call = apiInterfaceStatus.getPaymentStatus(
            Constants.PG_API_KEY,
            AddWalletDetailsAdapter.UniqueTransactionID!!,
            AddWalletDetailsAdapter.StatuHash
        )

        call.enqueue(object : Callback<PaymentStatusResponse> {
            override fun onResponse(
                call: Call<PaymentStatusResponse>,
                response: Response<PaymentStatusResponse>
            ) {

                wp10progressBar.visibility = View.GONE

                constraintHide.visibility = View.VISIBLE
                btnHowToAddFund.visibility = View.GONE //

                val responseData = response.body()

                if (responseData?.data == null) {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.transaction_failed_or_cancelled))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }
                else {

                    val Status = responseData.data.get(0).response_message!!

                    if (Status.contentEquals("SUCCESS")) {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                                .setTitle(getString(R.string.successful))
                                .setMessage(getString(R.string.payment_successful_wallet_updated))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()

                                    if (Connectivity.isOnline(contextAddFund)) {
                                        makeRequestAddFundApiCall()
                                    } else {

                                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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
                                .build()

                        mBottomSheetDialog.show()
                    } else {
                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.failure_upi_message))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }

                }
            }

            override fun onFailure(call: Call<PaymentStatusResponse>, t: Throwable) {

                wp10progressBar.visibility = View.GONE

                constraintHide.visibility = View.VISIBLE
                btnHowToAddFund.visibility = View.GONE //

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@AddFundsActivity)
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
}
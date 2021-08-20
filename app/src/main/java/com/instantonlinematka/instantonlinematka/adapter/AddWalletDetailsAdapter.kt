package com.instantonlinematka.instantonlinematka.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.*
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds.AddFundsActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import com.test.pg.secure.pgsdkv4.PaymentGatewayPaymentInitializer
import com.test.pg.secure.pgsdkv4.PaymentParams
import ir.alirezabdn.wp7progress.WP10ProgressBar
import kotlinx.android.synthetic.main.add_fund_activity.view.btnWallet
import kotlinx.android.synthetic.main.add_fund_activity.view.imgWallet
import kotlinx.android.synthetic.main.item_wallet_details.view.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@SuppressLint("RestrictedApi")
class AddWalletDetailsAdapter (
    val context: Context,
    val activity: AddFundsActivity,
    val walletList: ArrayList<UserPayment>,
    val txtWalletAmount: AppCompatEditText,
    val wp10ProgressBar: WP10ProgressBar,
    val constraintHide: ConstraintLayout,
    val btnHowToAddFund: AppCompatButton,
    val apiInterface: ApiInterface,
    val apiInterfaceStatus: ApiInterface,
    val sessionPrefs: SessionPrefs
) : RecyclerView.Adapter<AddWalletDetailsAdapter.WalletDetailsViewHolder>() {

    var Selection = ""
    var Amount = ""
    var PreviousData = ""
    var VPA = ""
    var UniqueReferID: String? = null
    var UniqueTransactionID: String? = null
    val PAYMENT_REQUEST_CODE = 12

    var hashList = ArrayList<PaymentHashSetterGetter>()
    var anotherHashList = ArrayList<AnotherHashSetterGetter>()

    class WalletDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgWallet = itemView.imgWallet
        val lblWallet = itemView.lblPayment
        val btnWallet= itemView.btnWallet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletDetailsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_wallet_details, parent,
            false)
        return WalletDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: WalletDetailsViewHolder, position: Int) {

        val userPayment = walletList.get(position)

        if (userPayment.id == 1) {  // Trak n Pay
            holder.imgWallet.setImageDrawable(ContextCompat.getDrawable(
                context, R.drawable.ic_bhim
            ))
            holder.lblWallet.setText(context.getString(R.string.bhim_upi))
        }
        else if (userPayment.id == 2) {  // Google Pay
            holder.imgWallet.setImageDrawable(ContextCompat.getDrawable(
                context, R.drawable.google_pay_icon
            ))
            holder.lblWallet.setText(context.getString(R.string.g_pay))
        }
        else if (userPayment.id == 3) {  // PayTm
            holder.imgWallet.setImageDrawable(ContextCompat.getDrawable(
                context, R.drawable.paytm_icon
            ))
            holder.lblWallet.setText(context.getString(R.string.paytm))
        }
        else if (userPayment.id == 4) {  // PhonePe
            holder.imgWallet.setImageDrawable(ContextCompat.getDrawable(
                context, R.drawable.phone_pay_icon
            ))
            holder.lblWallet.setText(context.getString(R.string.phone_pe))
        }
        else if (userPayment.id == 5) {  // Bhim
            holder.imgWallet.setImageDrawable(ContextCompat.getDrawable(
                context, R.drawable.ic_bhim
            ))
            holder.lblWallet.setText(context.getString(R.string.bhim_upi))
        }

        holder.btnWallet.setOnClickListener {

            var isAppInstalled = false

            if (userPayment.id == 1) {  // Trak n Pay
                isAppInstalled = appInstalledOrNot("in.org.npci.upiapp")
                Selection = Constants.BHIM
            }
            else if (userPayment.id == 2) {  // Google Pay
                isAppInstalled = appInstalledOrNot("com.google.android.apps.nbu.paisa.user")
                Selection = Constants.GPAY
            }
            else if (userPayment.id == 3) {  // PayTm
                isAppInstalled = appInstalledOrNot("net.one97.paytm")
                Selection = Constants.PAYTM
            }
            else if (userPayment.id == 4) {  // PhonePe
                isAppInstalled = appInstalledOrNot("com.phonepe.app")
                Selection = Constants.PHONEPE
            }
            else if (userPayment.id == 5) {  // Bhim
                isAppInstalled = appInstalledOrNot("in.org.npci.upiapp")
                Selection = Constants.BHIM
            }

            if (isAppInstalled) {
                Amount = txtWalletAmount.text.toString().trim()
                AddBalance(Amount)
            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(context as AddFundsActivity)
                        .setTitle(context.getString(R.string.uhoh))
                        .setMessage(context.getString(R.string.app_not_installed))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        }
    }

    override fun getItemCount(): Int = walletList.size


    // All About Payment
    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo(uri, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

    fun AddBalance(amount: String) {

        if (amount.equals("", ignoreCase = true)) {
            txtWalletAmount.setError(context.getString(R.string.enter_an_amount))
            txtWalletAmount.requestFocus()
        } else if (amount.toInt() < 500) {
            txtWalletAmount.setError(context.getString(R.string.minimum_deposit))
            txtWalletAmount.requestFocus()
        } else {
            Amount = amount
            Amount = "$Amount.00"
            val splitter = Amount.split("\\.".toRegex()).toTypedArray()
            val strAmountLengthAfterDec = splitter[1].length
            if (strAmountLengthAfterDec == 2) {
                if (Connectivity.isOnline(context)) {
                    AddFunds()
                } else {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(context as AddFundsActivity)
                            .setTitle(context.getString(R.string.uhoh))
                            .setMessage(context.getString(R.string.no_internet_found))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }
            } else {
                txtWalletAmount.setError(context.getString(R.string.must_have_digits_after_decimal_point))
                txtWalletAmount.requestFocus()
            }
        }
    }

    fun AddFunds() {

        wp10ProgressBar.showProgressBar()

        constraintHide.visibility = View.GONE
        btnHowToAddFund.visibility = View.GONE

        //UtilityMethods.showToastSucess(getActivity(), "Transaction Successful", true);
        UniqueTransactionID = UUID.randomUUID().toString()
        UniqueTransactionID = UniqueTransactionID!!.substring(0, 7)
        UniqueReferID = UniqueTransactionID + "ReferID"

        AddWalletDetailsAdapter.UniqueTransactionID = UniqueTransactionID

        val call = apiInterface.addFund(
            sessionPrefs.getString(Constants.USER_ID), Amount, UniqueTransactionID!!
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

                    UniqueTransactionID = data.transaction_id
                    AddWalletDetailsAdapter.PreviousData = data.user
                    AddWalletDetailsAdapter.UniqueTransactionID = data.transaction_id
                    VPA = data.payment.vpa

                    when (Selection) {

                        Constants.PHONEPE -> {
                            InitiateTransaction(Constants.PHONEPE)
                            //fromGPAY = false
                        }

                        Constants.PAYTM -> {
                            InitiateTransaction(Constants.PAYTM)
                            //fromGPAY = false
                        }

                        Constants.GPAY -> {
                            InitiateTransaction(Constants.GPAY)
                            //fromGPAY = false
                        }

                        Constants.BHIM -> {
                            InitiateTransaction(Constants.BHIM)
                            //fromGPAY = false
                        }

                        Constants.TRAKNPAY -> {
                            InitiatePaymentGateway()
                            //fromGPAY = false
                            /*val intent = Intent(context, PaymentGatewayActivity::class.java)
                            intent.putExtra("TransId", UniqueTransactionID)
                            intent.putExtra("ReferTransId", UniqueReferID)
                            intent.putExtra("Amount", Amount)
                            startActivityForResult(intent, UPIREQUESTCODE)*/
                        }
                    }

                    wp10ProgressBar.hideProgressBar()

                    constraintHide.visibility = View.VISIBLE
                    btnHowToAddFund.visibility = View.VISIBLE

                    /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                        Selection.contentEquals(Constants.GPAY)) {

                        InitiateTransaction(Constants.GPAY)
                        fromGPAY = true

                        wp10ProgressBar.hideProgressBar()

                        constraintHide.visibility = View.VISIBLE
                        btnHowToAddFund.visibility = View.VISIBLE
                    }
                    else {*/

                        /*if (Connectivity.isOnline(context)) {
                            getHashKey()
                        } else {

                            val mBottomSheetDialog: BottomSheetMaterialDialog =
                                BottomSheetMaterialDialog.Builder(context as AddFundsActivity)
                                    .setTitle(context.getString(R.string.uhoh))
                                    .setMessage(context.getString(R.string.no_internet_found))
                                    .setCancelable(false)
                                    .setPositiveButton(context.getString(R.string.okay)) { dialogInterface, which ->
                                        dialogInterface.dismiss()
                                    }
                                    .build()

                            mBottomSheetDialog.show()
                        }*/
                    //}

                } else {

                    wp10ProgressBar.hideProgressBar()

                    constraintHide.visibility = View.VISIBLE
                    btnHowToAddFund.visibility = View.VISIBLE

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(context as AddFundsActivity)
                            .setTitle(context.getString(R.string.uhoh))
                            .setMessage(context.getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }
            }

            override fun onFailure(call: Call<AddFundsResponse>, t: Throwable) {

                wp10ProgressBar.hideProgressBar()

                constraintHide.visibility = View.VISIBLE
                btnHowToAddFund.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(context as AddFundsActivity)
                        .setTitle(context.getString(R.string.uhoh))
                        .setMessage(context.getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }

        })
    }

    fun getHashKey() {

        HashKey = ""

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
                Constants.USER_PHONE + "@instantonlinematka.com",
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

                /*if (HashKey.isNotEmpty()) {
                    getAnotherHashKey() // makePaymentURLApiCall()
                } else {
                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(context as AddFundsActivity)
                            .setTitle(context.getString(R.string.uhoh))
                            .setMessage(context.getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                getHashKey()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }*/

                if (HashKey.isNotEmpty()) {
                    makePaymentURLApiCall()
                } else {
                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(context as AddFundsActivity)
                            .setTitle(context.getString(R.string.uhoh))
                            .setMessage(context.getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.retry)) { dialogInterface, which ->
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

    fun getAnotherHashKey() {

        StatuHash = ""

        anotherHashList.clear()

        var userName = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)
        if (userName.isEmpty()) {
            userName = "instant online matka user"
        }

        anotherHashList.add(
            AnotherHashSetterGetter(
                Constants.PG_API_KEY,
                UniqueTransactionID
            )
        )

        val hashDetails = anotherItemsToConvert(anotherHashList).toString()

        val call = apiInterface.getHashKey(
            hashDetails, "6f5ccf496a142e91814d53b0989fab5772c4133a"
        )

        call.enqueue(object : Callback<HashResponse> {
            override fun onResponse(call: Call<HashResponse>, response: Response<HashResponse>) {

                val data = response.body()!!
                StatuHash = data.hash ?: ""

                /*if (StatuHash.isNotEmpty()) {
                    makePaymentURLApiCall()
                } else {
                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(context as AddFundsActivity)
                            .setTitle(context.getString(R.string.uhoh))
                            .setMessage(context.getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                getHashKey()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }*/
            }

            override fun onFailure(call: Call<HashResponse>, t: Throwable) {}

        });
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

    //Json converter
    fun anotherItemsToConvert(list: ArrayList<AnotherHashSetterGetter>): JsonArray {

        val jsonArray = JsonArray()

        for (items in list) {

            val jsonObject = JsonObject() // /sub Object

            try {

                jsonObject.addProperty("api_key", items.api_key)
                jsonObject.addProperty("order_id", items.order_id)
                jsonArray.add(jsonObject)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        return jsonArray

    }

    fun makePaymentURLApiCall() {

        var userName = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)
        if (userName.isEmpty()) {
            userName = "instant online matka user"
        }

        val call = apiInterfaceStatus.PaymentRequest(
            Constants.PG_API_KEY,
            UniqueTransactionID!!,
            Amount,
            Constants.PG_CURRENCY,
            Constants.PG_DESCRIPTION,
            userName,
            Constants.USER_PHONE + "@instantonlinematka.com",
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

                val resData = response.body()!!

                Toast.makeText(context, "" + resData, Toast.LENGTH_LONG).show()

                /*VPA = resData.data!!.vpa!!

                when (Selection) {

                    Constants.PHONEPE -> {
                        InitiateTransaction(Constants.PHONEPE)
                        //fromGPAY = false
                    }

                    Constants.PAYTM -> {
                        InitiateTransaction(Constants.PAYTM)
                        //fromGPAY = false
                    }

                    Constants.GPAY -> {
                        InitiateTransaction(Constants.GPAY)
                        //fromGPAY = false
                    }

                    Constants.BHIM -> {
                        InitiateTransaction(Constants.BHIM)
                        //fromGPAY = false
                    }

                    Constants.TRAKNPAY -> {
                        InitiatePaymentGateway()
                        //fromGPAY = false
                        *//*val intent = Intent(contextAddFund, PaymentGatewayActivity::class.java)
                        intent.putExtra("TransId", UniqueTransactionID)
                        intent.putExtra("ReferTransId", UniqueReferID)
                        intent.putExtra("Amount", Amount)
                        startActivityForResult(intent, UPIREQUESTCODE)*//*
                    }
                }

                wp10ProgressBar.hideProgressBar()

                constraintHide.visibility = View.VISIBLE
                btnHowToAddFund.visibility = View.VISIBLE*/
            }

            override fun onFailure(call: Call<PaymentGatewayResponse>, t: Throwable) {}

        })
    }

    fun InitiateTransaction(Wallet: String) {

        Amount = Amount.replaceFirst("^0+(?!$)".toRegex(), "")
        val amountSplit = Amount.split("\\.".toRegex()).toTypedArray()

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
            activity.startActivityForResult(upiPayIntent, PAYMENT_REQUEST_CODE)

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
            activity.startActivityForResult(upiPayIntent, PAYMENT_REQUEST_CODE)

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
            activity.startActivityForResult(upiPayIntent, PAYMENT_REQUEST_CODE)

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
            activity.startActivityForResult(upiPayIntent, PAYMENT_REQUEST_CODE)
        }
    }

    fun InitiatePaymentGateway() {

        Constants.PG_ORDER_ID = UniqueReferID!!

        var userName = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)
        if (userName.isEmpty()) {
            userName = "instant online matka user"
        }

        val pgPaymentParams = PaymentParams()
        pgPaymentParams.aPiKey = Constants.PG_API_KEY
        pgPaymentParams.amount = Amount
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
            PaymentGatewayPaymentInitializer(pgPaymentParams, activity)
        pgPaymentInitialzer.initiatePaymentProcess()
    }

    companion object {
        var PreviousData = ""
        var UniqueTransactionID: String? = null
        //var fromGPAY: Boolean? = false
        var HashKey = ""

        var StatuHash = ""
    }
}
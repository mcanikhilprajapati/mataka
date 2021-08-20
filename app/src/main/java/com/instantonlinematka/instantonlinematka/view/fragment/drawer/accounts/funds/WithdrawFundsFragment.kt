package com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.WithdrawFundsFragmentBinding
import com.instantonlinematka.instantonlinematka.model.RequestWithdrawResponse
import com.instantonlinematka.instantonlinematka.model.WalletBalanceResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.bankdetails.BankDetailsFragment
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.android.synthetic.main.sheet_confirm_withdraw.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class WithdrawFundsFragment : Fragment() {

    lateinit var binding: WithdrawFundsFragmentBinding

    lateinit var contextWithdraw: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var mBottomSheetDialog: BottomSheetDialog

    var strWithdrawAmount: String? = null
    var strCharges: String? = null
    var strTotalWithdrawalAmount: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = WithdrawFundsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextWithdraw = inflater.context

        sessionPrefs = SessionPrefs(contextWithdraw)

        apiInterface = RetrofitClient.getRetrfitInstance()

        updateWallet()

        binding.lblRs.text = sessionPrefs.getString(Constants.WALLET)

        binding.btnWithdrawRequest.setSafeOnClickListener {

            val BankName: String = sessionPrefs.getString(Constants.BANK_NAME)
            val AccountName: String = sessionPrefs.getString(Constants.BANK_ACCOUNT_NUMBER)
            val AccountNumber: String = sessionPrefs.getString(Constants.BANK_ACCOUNT_NUMBER)
            val IFSCCode: String = sessionPrefs.getString(Constants.BANK_IFSC_CODE)

            val Amount = binding.txtEnterAmount.text.toString().trim()

            if (BankName.isEmpty() || AccountName.isEmpty() || AccountNumber.isEmpty() ||
                    IFSCCode.isEmpty()) {
                showNoBankScreen()
            }
            else if (Amount.isEmpty()) {
                binding.txtEnterAmount.error = getString(R.string.please_enter_an_amount_to_withdraw)
                binding.txtEnterAmount.requestFocus()
            }
            else if (Amount.toInt() < 1000) {
                binding.txtEnterAmount.error = getString(R.string.withdrawal_amount_must_be)
                binding.txtEnterAmount.requestFocus()
            }
            else if (Amount.toInt() > (sessionPrefs.getString(Constants.WALLET).toInt())) {
                binding.txtEnterAmount.error = getString(R.string.insufficient_balance)
                binding.txtEnterAmount.requestFocus()
            }
            else {
                showConfirmationScreen(Amount)
            }
        }

        return view
    }

    fun showNoBankScreen() {

        val mBottomSheetDialog: BottomSheetMaterialDialog =
            BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                .setTitle(getString(R.string.uhoh))
                .setMessage(getString(R.string.no_bank_details_found))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { dialogInterface, which ->
                    dialogInterface.dismiss()

                    val bundle = Bundle()
                    bundle.putString("from", "WITHDRAWAL")
                    Connectivity.switchDrawer(
                        activity as DrawerActivity,
                        BankDetailsFragment(),
                        "BANK_DETAILS_FRAGMENT",
                        bundle
                    )

                }
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface, which ->
                    dialogInterface.dismiss()
                }
                .build()

        mBottomSheetDialog.show()
    }

    private fun showConfirmationScreen(Amount: String) {

        mBottomSheetDialog = BottomSheetDialog(activity!!)
        val v: View = LayoutInflater.from(activity).inflate(R.layout.sheet_confirm_withdraw, null)
        mBottomSheetDialog.setContentView(v)
        mBottomSheetDialog.show()
        val AccountName: String = sessionPrefs.getString(Constants.BANK_NAME)
        val AccountNumber: String = sessionPrefs.getString(Constants.BANK_ACCOUNT_NUMBER)
        val IFSCCode: String = sessionPrefs.getString(Constants.BANK_IFSC_CODE)
        strWithdrawAmount = Amount

        // Charge Calculation
        val AfterChargesApplied = Amount.toInt() * 2 / 100
        val FinalCharges = (Amount.toInt() - AfterChargesApplied).toString()
        strCharges = AfterChargesApplied.toString()
        strTotalWithdrawalAmount = FinalCharges

        val lblAccountName: AppCompatTextView = mBottomSheetDialog.lblAccountName
        val lblAccountNumber: AppCompatTextView = mBottomSheetDialog.lblAccountNumber
        val lblWithdrawalAmount: AppCompatTextView = mBottomSheetDialog.lblWithdrawalAmountAnswer
        val lblCharges: AppCompatTextView = mBottomSheetDialog.lblChargesAnswer
        val lblTotalWithdrawalAmount: AppCompatTextView = mBottomSheetDialog.lblTotalWithdrawalAmountAnswer
        val btnProceed: AppCompatButton = mBottomSheetDialog.btnProceed
        val btnEdit: AppCompatImageButton = mBottomSheetDialog.imgbtnEdit

        lblAccountName.setText(AccountName)
        lblAccountNumber.setText(AccountNumber + " · " + IFSCCode)
        lblWithdrawalAmount.text = "₹ $strWithdrawAmount"
        lblCharges.text = "₹ $strCharges"
        lblTotalWithdrawalAmount.text = "₹ $strTotalWithdrawalAmount"

        btnProceed.setSafeOnClickListener { view: View? ->

            if (Connectivity.isOnline(activity as DrawerActivity)) {

                mBottomSheetDialog.dismiss()
                getWithDrawBalance()

            } else {

                mBottomSheetDialog.dismiss()
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

        btnEdit.setSafeOnClickListener {

            mBottomSheetDialog.dismiss()
            val bundle = Bundle()
            bundle.putString("from", "WITHDRAWAL")
            Connectivity.switchDrawer(
                activity as DrawerActivity,
                BankDetailsFragment(),
                "BANK_DETAILS_FRAGMENT",
                bundle
            )

        }
    }

    fun getWithDrawBalance() {

        binding.wp7progressBar.showProgressBar()

        binding.btnWithdrawRequest.isEnabled = false

        val call = apiInterface.requestWithdraw(
            sessionPrefs.getString(Constants.USER_ID), strWithdrawAmount!!
        )

        call.enqueue(object : Callback<RequestWithdrawResponse> {
            override fun onResponse(
                call: Call<RequestWithdrawResponse>,
                response: Response<RequestWithdrawResponse>
            ) {

                val withdrawResponse = response.body()!!

                val message = withdrawResponse.message
                val isResponse = withdrawResponse.response

                if (isResponse) {

                    binding.txtEnterAmount.setText("")

                    val mDialog = Dialog(contextWithdraw)
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog.setCancelable(false)
                    mDialog.setContentView(R.layout.alert_success)
                    mDialog.window!!.setGravity(Gravity.CENTER)

                    mDialog.show()

                    val lblSuccessTitle = mDialog.lblSuccessTitleAnswer
                    val lblSuccessMessage = mDialog.lblSuccessMessageAnswer

                    lblSuccessTitle.text = getString(R.string.successful)
                    lblSuccessMessage.text = message

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000L)

                        mDialog.dismiss()
                    }
                }
                else {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }

                binding.wp7progressBar.hideProgressBar()

                binding.btnWithdrawRequest.isEnabled = true
            }

            override fun onFailure(call: Call<RequestWithdrawResponse>, t: Throwable) {

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
        })
    }

    // Wallet Amount Updater
    fun updateWallet() {

        if (Connectivity.isOnline(contextWithdraw)) {

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
                        sessionPrefs.addString(Constants.WALLET, data.user.wallet)
                        try {
                            val wallet = sessionPrefs.getString(Constants.WALLET)
                            if (wallet.isEmpty()) {
                                (activity as DrawerActivity).toolbar_Wallet.text = "- - -"
                            } else {
                                ((activity as DrawerActivity).toolbar_Wallet.setText(data.user.wallet))
                            }
                        } catch (e: Exception) {
                        }
                    }

                }

                override fun onFailure(call: Call<WalletBalanceResponse>, t: Throwable) {}

            })
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
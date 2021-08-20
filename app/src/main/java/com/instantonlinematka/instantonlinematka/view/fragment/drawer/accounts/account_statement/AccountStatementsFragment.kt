package com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.account_statement

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.AccountStatementAdapter
import com.instantonlinematka.instantonlinematka.databinding.AccountStatementsFragmentBinding
import com.instantonlinematka.instantonlinematka.model.AccountStatementData
import com.instantonlinematka.instantonlinematka.model.AccountStatementResponse
import com.instantonlinematka.instantonlinematka.model.BidData
import com.instantonlinematka.instantonlinematka.model.WalletBalanceResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_drawer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class AccountStatementsFragment : Fragment() {

    lateinit var binding: AccountStatementsFragmentBinding

    lateinit var contextAccStatement: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var accountStatementList: ArrayList<AccountStatementData>

    lateinit var adapter: AccountStatementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountStatementsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextAccStatement = inflater.context

        sessionPrefs = SessionPrefs(contextAccStatement)

        apiInterface = RetrofitClient.getRetrfitInstance()

        accountStatementList = ArrayList()

        adapter = AccountStatementAdapter(contextAccStatement, accountStatementList)

        binding.recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        binding.recyclerView.adapter = adapter

        getAccountStatement()

        updateWallet()

        binding.btnRetry.setSafeOnClickListener {
            getAccountStatement()
        }

        return view
    }

    fun getAccountStatement() {

        if (Connectivity.isOnline(contextAccStatement)) {
            makeAccountStatementApiCall()
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

    fun makeAccountStatementApiCall() {

        binding.wp10progressBar.showProgressBar()

        binding.linearHome.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE

        val call = apiInterface.getAccountStatement(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object : Callback<AccountStatementResponse> {
            override fun onResponse(
                call: Call<AccountStatementResponse>,
                response: Response<AccountStatementResponse>
            ) {

                accountStatementList.clear()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    binding.linearHome.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE

                    val AccStatementResponse = responseData.data

                    for (items in AccStatementResponse) {

                        val Date = items.date ?: ""
                        val Time = items.time ?: ""
                        val Amount = items.amount ?: ""
                        val BidId = items.bid_or_fund_id ?: ""
                        val StatementType = items.statement_type ?: ""

                        var bidData = BidData()

                        if (items.biddata != null) {
                            val GameName = items.biddata.game_type_name ?: ""
                            val WinStatus = items.biddata.win_status ?: ""
                            val CategoryName = items.biddata.category_name ?: ""
                            val IsStarline = items.biddata.is_starline ?: ""
                            val bidNumber = items.biddata.number ?: ""
                            val bidAmount = items.biddata.bid_amount ?: ""
                            val openPanna = items.biddata.open_panna ?: ""
                            val closePanna = items.biddata.close_panna ?: ""
                            val openDigit = items.biddata.open_digit ?: ""
                            val closeDigit = items.biddata.close_digit ?: ""

                            bidData = BidData(GameName, IsStarline, WinStatus, CategoryName,
                                bidAmount, bidNumber, openPanna, closePanna, openDigit, closeDigit)
                        }

                        val data = AccountStatementData(Date, Time, Amount, BidId, StatementType,
                            bidData)

                        accountStatementList.add(data)
                    }

                    adapter.notifyDataSetChanged()

                } else {

                    binding.linearHome.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE

                }

                binding.wp10progressBar.hideProgressBar()
            }

            override fun onFailure(call: Call<AccountStatementResponse>, t: Throwable) {

                binding.wp10progressBar.hideProgressBar()

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setMessage(t.message!!)
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

        if (Connectivity.isOnline(contextAccStatement)) {

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
package com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.bonus

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.BonusAdapter
import com.instantonlinematka.instantonlinematka.databinding.BonusFragmentBinding
import com.instantonlinematka.instantonlinematka.model.BonusData
import com.instantonlinematka.instantonlinematka.model.BonusResponse
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
class BonusFragment : Fragment() {

    lateinit var binding: BonusFragmentBinding

    lateinit var contextBonus: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var bonusList: ArrayList<BonusData>

    lateinit var adapter: BonusAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BonusFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextBonus = inflater.context

        sessionPrefs = SessionPrefs(contextBonus)

        apiInterface = RetrofitClient.getRetrfitInstance()

        bonusList = ArrayList()

        adapter = BonusAdapter(contextBonus, bonusList)

        binding.recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        binding.recyclerView.adapter = adapter

        getBonus()

        updateWallet()

        binding.btnRetry.setSafeOnClickListener {
            getBonus()
        }

        return view
    }

    fun getBonus() {

        if (Connectivity.isOnline(contextBonus)) {
            makeBonusApiCall()
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

    fun makeBonusApiCall() {

        binding.wp10progressBar.showProgressBar()

        binding.linearHome.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE

        val call = apiInterface.getBonus(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object: Callback<BonusResponse>{

            override fun onResponse(call: Call<BonusResponse>, response: Response<BonusResponse>) {

                bonusList.clear()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    binding.linearHome.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE

                    val AccStatementResponse = responseData.data

                    for (items in AccStatementResponse) {

                        val id = items.id ?: ""
                        val date = items.date ?: ""
                        val time = items.time ?: ""
                        val userId = items.user_id ?: ""
                        val statementType = items.statement_type ?: ""
                        val bidId = items.bid_or_fund_id ?: ""
                        val createdDate = items.created_date ?: ""
                        val amount = items.amount ?: ""
                        val gameTypeName = items.game_type_name ?: ""

                        val data = BonusData(id, date, time, userId, statementType, bidId,
                            amount, createdDate, gameTypeName)

                        bonusList.add(data)
                    }

                    adapter.notifyDataSetChanged()

                } else {

                    binding.linearHome.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE

                }

                binding.wp10progressBar.hideProgressBar()
            }

            override fun onFailure(call: Call<BonusResponse>, t: Throwable) {

                binding.wp10progressBar.hideProgressBar()

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

        if (Connectivity.isOnline(contextBonus)) {

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
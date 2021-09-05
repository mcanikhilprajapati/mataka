package com.instantonlinematka.instantonlinematka.view.fragment.drawer.bidding_history

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.BiddingHistoryAdapter
import com.instantonlinematka.instantonlinematka.databinding.BiddingHistoryFragmentBinding
import com.instantonlinematka.instantonlinematka.model.*
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
class BiddingHistoryFragment : Fragment() {

    lateinit var binding: BiddingHistoryFragmentBinding

    lateinit var contextBidding: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var biddingHistoryList: ArrayList<BiddingData>
    lateinit var biddingByGameList: ArrayList<BiddingDataByGame>

    lateinit var adapter: BiddingHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = BiddingHistoryFragmentBinding.inflate(inflater, container, false)
        val view= binding.root

        contextBidding = inflater.context

        sessionPrefs = SessionPrefs(contextBidding)

        apiInterface = RetrofitClient.getRetrfitInstance()

        biddingHistoryList = ArrayList()
        biddingByGameList = ArrayList()

        adapter = BiddingHistoryAdapter(contextBidding, biddingByGameList)

        binding.recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        binding.recyclerView.adapter = adapter

        binding.btnShowHistory.setSafeOnClickListener {

            val biddingHistory = arrayOfNulls<String>(biddingHistoryList.size)
            for (i in biddingHistoryList.indices) {
                biddingHistory[i] = biddingHistoryList.get(i).game_type_name
            }
            val builder = AlertDialog.Builder(
                activity!!
            )
            builder.setTitle(getString(R.string.choose_market_name))
            builder.setItems(
                biddingHistory
            ) { dialog, which ->

                biddingByGameList.clear()
                adapter.notifyDataSetChanged()
                binding.wp10progressBar.showProgressBar()

                binding.lblMarketName.setText(
                    biddingHistoryList.get(which).game_type_name
                )
                getGameHistoryByGame(biddingHistoryList.get(which).game_type_id!!)
            }
            val alert = builder.create()
            alert.show()
        }

        getBiddingHistory()

        updateWallet()

        binding.btnRetry.setSafeOnClickListener {
            getBiddingHistory()
        }

        return view
    }

    fun getBiddingHistory() {

        if (Connectivity.isOnline(contextBidding)) {
            makeBiddingHistoryApiCall()
        }
        else {

            binding.linearHome.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.constraintSelect.visibility = View.GONE

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity!!)
                    .setTitle(getString(R.string.uhoh))
                    .setMessage(getString(R.string.no_internet_found))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        getBiddingHistory()
                    }
                    .setNegativeButton(getString(R.string.okay)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    fun makeBiddingHistoryApiCall() {

        binding.wp10progressBar.showProgressBar()

        val call = apiInterface.getBiddingHistory(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object : Callback<BiddingHistoryResponse> {
            override fun onResponse(
                call: Call<BiddingHistoryResponse>,
                response: Response<BiddingHistoryResponse>
            ) {

                biddingByGameList.clear()

                val biddingResponse = response.body()!!

                val isResponse = biddingResponse.response

                if (isResponse) {

                    binding.linearHome.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.constraintSelect.visibility = View.VISIBLE

                    for (items in biddingResponse.data) {

                        val GameName = items.game_type_name ?: ""
                        val GameId = items.game_type_id ?: ""
                        val UserId = items.user_id ?: ""

                        val responseData = BiddingData(GameName, GameId, UserId)

                        biddingHistoryList.add(responseData)
                    }

                    getGameHistoryByGame("")
                } else {

                    binding.wp10progressBar.hideProgressBar()

                    binding.linearHome.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.constraintSelect.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<BiddingHistoryResponse>, t: Throwable) {

                binding.wp10progressBar.hideProgressBar()

                binding.linearHome.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.constraintSelect.visibility = View.GONE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity!!)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            getBiddingHistory()
                        }
                        .setNegativeButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        })
    }

    fun getGameHistoryByGame(gameId: String) {

        if (Connectivity.isOnline(contextBidding)) {
            makeBiddingByGameApiCall(gameId)
        }
        else {

            binding.linearHome.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.constraintSelect.visibility = View.GONE

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity!!)
                    .setTitle(getString(R.string.uhoh))
                    .setMessage(getString(R.string.no_internet_found))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        getGameHistoryByGame(gameId)
                    }
                    .setNegativeButton(getString(R.string.okay)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    private fun makeBiddingByGameApiCall(gameId: String) {

        val call = apiInterface.getBiddingHistoryByGame(
            sessionPrefs.getString(Constants.USER_ID), gameId
        )

        call.enqueue(object : Callback<BiddingHistoryByGame> {
            override fun onResponse(
                call: Call<BiddingHistoryByGame>,
                response: Response<BiddingHistoryByGame>
            ) {

                biddingByGameList.clear()

                val biddingResponse = response.body()!!

                val isResponse = biddingResponse.response

                if (isResponse) {

                    binding.linearHome.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.constraintSelect.visibility = View.VISIBLE

                    for (items in biddingResponse.data) {

                        val GameName = items.game_type_name ?: ""
                        val CategoryName = items.category_name ?: ""
                        val BidId = items.bid_id ?: ""
                        val BidNumber = items.number ?: ""
                        val Type = items.type ?: ""
                        val BidAmount = items.bid_amount ?: ""
                        val BidedFor = items.bided_for ?: ""
                        val BidedOn = items.bided_on ?: ""
                        val BidedOnTime = items.bided_on_time ?: ""
                        val ClosingBalance = items.closing_balance ?: ""
                        val Status = items.status ?: ""
                        val WonAmount = items.won_amount ?: ""

                        val responseData = BiddingDataByGame(
                            GameName, CategoryName, BidId, BidNumber, Type, BidAmount,
                            BidedFor, BidedOn, BidedOnTime, ClosingBalance, Status, WonAmount
                        )

                        biddingByGameList.add(responseData)
                    }

                    adapter.notifyDataSetChanged()

                } else {

                    binding.linearHome.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.constraintSelect.visibility = View.GONE

                }

                binding.wp10progressBar.hideProgressBar()
            }

            override fun onFailure(call: Call<BiddingHistoryByGame>, t: Throwable) {

                binding.wp10progressBar.hideProgressBar()

                binding.linearHome.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.constraintSelect.visibility = View.GONE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity!!)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            getGameHistoryByGame(gameId)
                        }
                        .setNegativeButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
        })
    }

    // Wallet Amount Updater
    fun updateWallet() {

        if (Connectivity.isOnline(contextBidding)) {

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
                                ((activity as DrawerActivity).toolbar_Wallet.setText("₹"+data.user.wallet))
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
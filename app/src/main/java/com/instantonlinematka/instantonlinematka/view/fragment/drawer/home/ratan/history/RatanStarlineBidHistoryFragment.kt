package com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan.history

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.RatanBidHistoryAdapter
import com.instantonlinematka.instantonlinematka.databinding.RatanStarlineBidHistoryFragmentBinding
import com.instantonlinematka.instantonlinematka.model.RatanBidHistoryData
import com.instantonlinematka.instantonlinematka.model.RatanStarlineBidHistoryResponse
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
class RatanStarlineBidHistoryFragment : Fragment() {

    lateinit var binding: RatanStarlineBidHistoryFragmentBinding

    lateinit var contextBidHistory: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var ratanBidHistoryList: ArrayList<RatanBidHistoryData>

    lateinit var adapter: RatanBidHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RatanStarlineBidHistoryFragmentBinding.inflate(
            inflater, container, false
        )
        val view = binding.root

        contextBidHistory = inflater.context

        sessionPrefs = SessionPrefs(contextBidHistory)

        apiInterface = RetrofitClient.getRetrfitInstance()

        ratanBidHistoryList = ArrayList()

        adapter = RatanBidHistoryAdapter(contextBidHistory, ratanBidHistoryList)

        binding.recyclerView.layoutManager = LinearLayoutManager (
            context, LinearLayoutManager.VERTICAL, false
        )

        binding.recyclerView.adapter = adapter

        getBidHistory()

        updateWallet()

        binding.btnRetry.setSafeOnClickListener {
            getBidHistory()
        }

        return view
    }

    fun getBidHistory() {

        if (Connectivity.isOnline(contextBidHistory)) {
            makeBidHistoryApiCall()
        }
        else {
            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity!!)
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

    fun makeBidHistoryApiCall() {

        binding.wp10progressBar.visibility = View.VISIBLE

        binding.linearHome.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE

        val call = apiInterface.getStarlineBidHistory(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object: Callback<RatanStarlineBidHistoryResponse>{
            override fun onResponse(
                call: Call<RatanStarlineBidHistoryResponse>,
                response: Response<RatanStarlineBidHistoryResponse>
            ) {

                binding.linearHome.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                ratanBidHistoryList.clear()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val dataList = responseData.data

                    for (items in dataList) {

                        val GameId = items.game_id ?: ""
                        val CategoryName = items.category_name ?: ""
                        val BidId = items.bid_id ?: ""
                        val PannaResult = items.panna_result ?: ""
                        val SingleDigitResult = items.single_digit_result ?: ""
                        val BidAmount = items.bid_amount ?: ""
                        val BidedFor = items.bided_for ?: ""
                        val BidedForTime = items.bided_for_time ?: ""
                        val BidedOn = items.bided_on ?: ""
                        val BidedOnTime = items.bided_on_time ?: ""
                        val ClosingBalance = items.closing_balance ?: ""
                        val Status = items.status ?: ""
                        val WonAmount = items.won_amount ?: ""

                        val bidHistoryData = RatanBidHistoryData(GameId, CategoryName, BidId, PannaResult,
                            SingleDigitResult, BidAmount, BidedFor, BidedForTime, BidedOn, BidedOnTime,
                            ClosingBalance, Status, WonAmount
                        )

                        ratanBidHistoryList.add(bidHistoryData)
                    }

                    adapter.notifyDataSetChanged()

                }
                else {

                    binding.linearHome.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }

                binding.wp10progressBar.visibility = View.GONE

            }

            override fun onFailure(call: Call<RatanStarlineBidHistoryResponse>, t: Throwable) {

                binding.wp10progressBar.visibility = View.GONE

                binding.linearHome.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE

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

    // Wallet Amount Updater
    fun updateWallet() {

        if (Connectivity.isOnline(contextBidHistory)) {

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
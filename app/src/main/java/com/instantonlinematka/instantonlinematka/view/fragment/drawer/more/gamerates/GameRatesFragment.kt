package com.instantonlinematka.instantonlinematka.view.fragment.drawer.more.gamerates

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.GameRatesAdapter
import com.instantonlinematka.instantonlinematka.databinding.GameRatesFragmentBinding
import com.instantonlinematka.instantonlinematka.model.GameRatesResponse
import com.instantonlinematka.instantonlinematka.model.RatesData
import com.instantonlinematka.instantonlinematka.model.WalletBalanceResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_drawer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class GameRatesFragment : Fragment() {

    lateinit var binding: GameRatesFragmentBinding

    lateinit var contextGameRates: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var ratesList: ArrayList<RatesData>

    lateinit var adapter: GameRatesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GameRatesFragmentBinding.inflate(
            inflater, container, false
        )
        val view = binding.root

        contextGameRates = inflater.context

        sessionPrefs = SessionPrefs(contextGameRates)

        apiInterface = RetrofitClient.getRetrfitInstance()

        ratesList = ArrayList()

        adapter = GameRatesAdapter(contextGameRates, ratesList)

        binding.recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        binding.recyclerView.adapter = adapter

        getGameRates()

        updateWallet()

        return view
    }

    fun getGameRates() {

        if (Connectivity.isOnline(contextGameRates)) {
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

        binding.wp10progressBar.visibility = View.VISIBLE

        binding.constraintOuter.visibility = View.GONE

        val call = apiInterface.getGameRates(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object: Callback<GameRatesResponse> {

            override fun onResponse(call: Call<GameRatesResponse>, response: Response<GameRatesResponse>) {

                ratesList.clear()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val RatesResponse = responseData.data

                    for (items in RatesResponse) {

                        val id = items.id ?: ""
                        val is_starline = items.is_starline ?: ""
                        val game_cat_id = items.game_cat_id ?: ""
                        val winning_ratio = items.winning_ratio ?: ""
                        val created_date = items.created_date ?: ""
                        val updated_date = items.updated_date ?: ""
                        val category_name = items.category_name ?: ""

                        val data = RatesData(id, is_starline, game_cat_id, winning_ratio,
                            created_date, updated_date, category_name)

                        ratesList.add(data)
                    }

                    adapter.notifyDataSetChanged()

                    binding.constraintOuter.visibility = View.VISIBLE

                } else {

                    binding.constraintOuter.visibility = View.GONE

                }

                binding.wp10progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<GameRatesResponse>, t: Throwable) {

                binding.wp10progressBar.visibility = View.GONE

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

        if (Connectivity.isOnline(contextGameRates)) {

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
}
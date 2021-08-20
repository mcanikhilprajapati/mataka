package com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.RatanStarlineResultAdapter
import com.instantonlinematka.instantonlinematka.databinding.RatanStarlineResultHistoryFragmentBinding
import com.instantonlinematka.instantonlinematka.model.RatanStarlineGameData
import com.instantonlinematka.instantonlinematka.model.RatanStarlineGameResponse
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
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("RestrictedApi")
class RatanStarlineResultHistoryFragment : Fragment() {

    lateinit var binding: RatanStarlineResultHistoryFragmentBinding

    lateinit var contextResultHistory: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var adapter: RatanStarlineResultAdapter

    lateinit var ratanGameList: ArrayList<RatanStarlineGameData>

    lateinit var BirthdateCal: Calendar
    var mSelectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RatanStarlineResultHistoryFragmentBinding.inflate(
            inflater, container, false
        )
        val view = binding.root

        contextResultHistory = inflater.context

        sessionPrefs = SessionPrefs(contextResultHistory)

        apiInterface = RetrofitClient.getRetrfitInstance()

        BirthdateCal = Calendar.getInstance()

        ratanGameList = ArrayList()

        adapter = RatanStarlineResultAdapter(contextResultHistory, ratanGameList)

        binding.recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        binding.recyclerView.adapter = adapter

        updateWallet()

        binding.btnRetry.setSafeOnClickListener {
            getResultHistory()
        }

        binding.btnSelectDate.setSafeOnClickListener {
            selectDate()
        }

        return view
    }

    fun selectDate() {

        val dob_date = Calendar.getInstance()
        val dob_currentDate = Calendar.getInstance()
        val dob_atePickerDialog = DatePickerDialog(
            activity!!, { datePicker, year, monthOfYear, dayOfMonth ->
                dob_date[year, monthOfYear] = dayOfMonth

                BirthdateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                BirthdateCal.set(Calendar.MONTH, monthOfYear)
                BirthdateCal.set(Calendar.YEAR, year)

                val date: Date = BirthdateCal.getTime()
                val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH) // show in this
                formatter.format(date)
                val dt1 = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH) // send in this formate
                mSelectedDate = dt1.format(date)
                binding.lblSelectedDate.text = "Selected Date : ${formatter.format(date)}"
                getResultHistory()
            },
            dob_currentDate[Calendar.YEAR],
            dob_currentDate[Calendar.MONTH], dob_currentDate[Calendar.DATE]
        )
        dob_atePickerDialog.setOnCancelListener {
            // edtAnniversaryDate.setEnabled(true);
        }
        dob_atePickerDialog.datePicker.maxDate = dob_currentDate.timeInMillis
        dob_atePickerDialog.show()

    }

    fun getResultHistory() {

        if (Connectivity.isOnline(contextResultHistory)) {
            makeResultHistoryApiCall()
        } else {
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

    fun makeResultHistoryApiCall() {

        binding.wp10progressBar.showProgressBar()

        binding.linearHome.visibility = View.GONE
        binding.lblPickDate.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE

        val call = apiInterface.getStarlineResultHistory(
            sessionPrefs.getString(Constants.USER_ID), mSelectedDate!!
        )

        call.enqueue(object : Callback<RatanStarlineGameResponse> {
            override fun onResponse(
                call: Call<RatanStarlineGameResponse>,
                response: Response<RatanStarlineGameResponse>
            ) {

                val ratanGameListData = response.body()!!

                val isResponse = ratanGameListData.response

                if (isResponse) {

                    val responseData = ratanGameListData.data

                    for (items in responseData) {

                        val GameId = items.game_id ?: ""
                        val GameDate = items.game_date ?: ""
                        val OpenTime = items.open_time ?: ""
                        val CloseTime = items.close_time ?: ""
                        val IsDisabled = items.is_disabled ?: ""
                        val GameStatus = items.game_status ?: ""
                        val PannaResult = items.panna_result ?: ""
                        val SingleDigitResult = items.single_digit_result ?: ""

                        val gameData = RatanStarlineGameData(GameId, GameDate, OpenTime, CloseTime,
                            IsDisabled, GameStatus, PannaResult, SingleDigitResult)

                        ratanGameList.add(gameData)
                    }

                    adapter.notifyDataSetChanged()

                    binding.linearHome.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE

                }
                else {

                    binding.linearHome.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }

                binding.wp10progressBar.hideProgressBar()
            }

            override fun onFailure(call: Call<RatanStarlineGameResponse>, t: Throwable) {

                binding.wp10progressBar.hideProgressBar()

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

        if (Connectivity.isOnline(contextResultHistory)) {

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
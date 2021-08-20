package com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.RatanStarlineGameAdapter
import com.instantonlinematka.instantonlinematka.databinding.RatanStarlineFragmentBinding
import com.instantonlinematka.instantonlinematka.model.RatanStarlineGameData
import com.instantonlinematka.instantonlinematka.model.RatanStarlineGameResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan.history.RatanStarlineBidHistoryFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan.history.RatanStarlineResultHistoryFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan.history.RatanStarlineTermsAndConditionsFragment
import com.romainpiel.shimmer.Shimmer
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class RatanStarlineGamesFragment : Fragment() {

    lateinit var binding: RatanStarlineFragmentBinding

    lateinit var contextPlayRatan: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var adapter: RatanStarlineGameAdapter

    lateinit var shimmer: Shimmer

    lateinit var ratanGameList: ArrayList<RatanStarlineGameData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = RatanStarlineFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextPlayRatan = inflater.context

        shimmer = Shimmer()
        shimmer.start(binding.lblPlayRatanGames)

        sessionPrefs = SessionPrefs(contextPlayRatan)

        apiInterface = RetrofitClient.getRetrfitInstance()

        ratanGameList = ArrayList()
        Log.i("ratanGameListratanGameListratanGameList", "onCreateView: "+ratanGameList)

        adapter = RatanStarlineGameAdapter(contextPlayRatan, ratanGameList)

        binding.recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        binding.recyclerView.adapter = adapter

        binding.btnRetry.setSafeOnClickListener {
            getRatanStarlineGameList()
        }

        binding.btnRatanStarlineBidHistory.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, RatanStarlineBidHistoryFragment(),
                "RATAN_STARLINE_BID_HISTORY", bundle
            )
        }

        binding.btnRatanStarlineResultHistory.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, RatanStarlineResultHistoryFragment(),
                "RATAN_STARLINE_RESULT_HISTORY", bundle
            )
        }

        binding.btnTermsAndConditions.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, RatanStarlineTermsAndConditionsFragment(),
                "RATAN_STARLINE_TERMS_CONDITIONS_HISTORY", bundle
            )
        }

        getLiveResults()
        return view

    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            if (menuVisible) {
                getLiveResults()
            }
        }
    }

    fun getLiveResults() {

        val timer = object: CountDownTimer(21600000, 180000) {

            override fun onTick(millisUntilFinished: Long) {
                getRatanStarlineGameList()
            }
            override fun onFinish() {}
        }
        timer.start()
    }

    fun getRatanStarlineGameList() {

        if (Connectivity.isOnline(contextPlayRatan)) {
            makeRatanStarlineGameListApiCall()
        }
        else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity!!)
                    .setTitle(getString(R.string.uhoh))
                    .setMessage(getString(R.string.no_internet_found))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        getRatanStarlineGameList()
                    }
                    .setNegativeButton(getString(R.string.okay)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    fun makeRatanStarlineGameListApiCall() {

        binding.wp10progressBar.showProgressBar()

        binding.linearHome.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE

        val call = apiInterface.getRatanStarlineGames(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object : Callback<RatanStarlineGameResponse>{
            override fun onResponse(
                call: Call<RatanStarlineGameResponse>,
                response: Response<RatanStarlineGameResponse>
            ) {

                ratanGameList.clear()

                val ratanGameListData = response.body()!!

                val isResponse = ratanGameListData.response

                if (isResponse) {

                    val responseData = ratanGameListData.data
                    Log.i("_ratanGameListratanGameListratanGameList", "onResponse: "+responseData)

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
                        .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            getRatanStarlineGameList()
                        }
                        .setNegativeButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
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
package com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.market

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.GameListAdapter
import com.instantonlinematka.instantonlinematka.adapter.onTimerCompleteListener
import com.instantonlinematka.instantonlinematka.databinding.MarketFragmentBinding
import com.instantonlinematka.instantonlinematka.model.GameListData
import com.instantonlinematka.instantonlinematka.model.GameListResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds.WithdrawFundsFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan.RatanStarlineGamesFragment
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_drawer.toolbar_Wallet
import kotlinx.android.synthetic.main.market_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class MarketFragment : Fragment(), onTimerCompleteListener {

    lateinit var binding: MarketFragmentBinding



    lateinit var contextMarket: Context

    lateinit var apiInterface: ApiInterface

    lateinit var sessionPrefs: SessionPrefs

    lateinit var gameList: ArrayList<GameListData>

    lateinit var adapter: GameListAdapter
    lateinit var onTimerCompleteListener: onTimerCompleteListener



    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if(this::adapter.isInitialized)
                adapter.cancelAllTimers()
        } else {
            if(this::adapter.isInitialized)
                adapter.cancelAllTimers()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        onTimerCompleteListener =this
        binding = MarketFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextMarket = inflater.context

        sessionPrefs = SessionPrefs(contextMarket)

        val wallet = sessionPrefs.getString(Constants.WALLET)
        Log.i("Dadadadasd", "onCreateView: " + wallet)
        if (wallet.isEmpty()) {
            binding.txtWallet.text = "- - -"
        } else {
            binding.txtWallet.text = wallet+""
        }

        binding.swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorAccent),resources.getColor(R.color.colorAccent),resources.getColor(R.color.colorAccent))
        binding.swipeRefreshLayout.setOnRefreshListener {

            getMarketGameList()

        }

        apiInterface = RetrofitClient.getRetrfitInstance()

        gameList = ArrayList()

        adapter = GameListAdapter(contextMarket, gameList,onTimerCompleteListener)

        binding.recyclerView.layoutManager = LinearLayoutManager (
            context, LinearLayoutManager.VERTICAL, false
        )


        binding.recyclerView.adapter = adapter



//        adapter!!.onTimerComplete?.let {
//            it { int->
//
//                Toast.makeText(context,"Load",Toast.LENGTH_SHORT).show()
//                getMarketGameList()
//
//            }
//        }
        binding.btnRetry.setSafeOnClickListener {
            getMarketGameList()
        }


        binding.llChat.setSafeOnClickListener {
            openWhatsApp()
        }

        binding.llKalyan.setSafeOnClickListener {
            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, RatanStarlineGamesFragment(),
                "RatanStarlineGamesFragment", bundle
            )
        }

        return view
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            if (menuVisible) {
                getMarketGameList()
            }
        }
    }

    private fun openWhatsApp() {
        val smsNumber = "919743971427"
        val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra(
                "jid",
                PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net"
            ) //phone number without "+" prefix
            startActivity(sendIntent)
        } else {
            val uri = Uri.parse("market://details?id=com.whatsapp")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            Toast.makeText(
                activity, "WhatsApp not Installed",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(goToMarket)
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        val pm = activity!!.packageManager
        var app_installed = false
        app_installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    fun getMarketGameList() {

        if (Connectivity.isOnline(contextMarket)) {
            makeMarketGameListApiCall()
        }
        else {
            binding.swipeRefreshLayout.isRefreshing = false

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity!!)
                    .setTitle(getString(R.string.uhoh))
                    .setMessage(getString(R.string.no_internet_found))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        getMarketGameList()
                    }
                    .setNegativeButton(getString(R.string.quit)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        (activity as DrawerActivity).finish()
                    }
                    .build()

            mBottomSheetDialog.show()
        }

    }

    fun makeMarketGameListApiCall() {

        binding.wp10progressBar.showProgressBar()

        binding.linearHome.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.txtTodayMarket.visibility= View.GONE

        val call = apiInterface.getGameList(sessionPrefs.getString(Constants.USER_ID))

        call.enqueue(object : Callback<GameListResponse>{
            override fun onResponse(
                call: Call<GameListResponse>,
                response: Response<GameListResponse>
            ) {
                binding.swipeRefreshLayout.isRefreshing = false
                gameList.clear()

                val dataResponse = response.body()!!

                for (item in dataResponse.data) {

                    val gameId = item.game_id ?: ""
                    val gameTypeId = item.game_type_id ?: ""
                    val openTime = item.open_time ?: ""
                    val closeTime = item.close_time ?: ""
                    val gameTypeName = item.game_type_name ?: ""
                    val gameStatus = item.game_status ?: ""
                    val openResult = item.open_result ?: ""
                    val closeResult = item.close_result ?: ""
                    val centerOpenResult = item.center_open_result ?: ""
                    val centerCloseResult = item.center_close_result ?: ""
                    val isDisabled = item.isDisabled ?: ""

                    val GameData = GameListData(gameId, gameTypeName, openTime, closeTime,
                                    gameTypeId, openResult, closeResult, centerOpenResult,
                                    centerCloseResult, gameStatus, isDisabled)

                    gameList.add(GameData)
                }

                if (gameList.size == 0) {

                    binding.linearHome.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.txtTodayMarket.visibility= View.GONE
                }
                else {

                    adapter = GameListAdapter(contextMarket, gameList,onTimerCompleteListener)

                    binding.linearHome.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.txtTodayMarket.visibility= View.GONE
                }

                binding.wp10progressBar.hideProgressBar()
            }

            override fun onFailure(call: Call<GameListResponse>, t: Throwable) {

                binding.swipeRefreshLayout.isRefreshing = false
                binding.wp10progressBar.hideProgressBar()

                binding.linearHome.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.txtTodayMarket.visibility= View.GONE
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

    override fun onTimerComplete(item: Int) {


               // Toast.makeText(context,"Load",Toast.LENGTH_SHORT).show()
                getMarketGameList()
    }
}
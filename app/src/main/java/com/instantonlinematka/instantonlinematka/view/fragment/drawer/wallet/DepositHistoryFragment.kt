package com.instantonlinematka.instantonlinematka.view.fragment.drawer.wallet

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
import com.instantonlinematka.instantonlinematka.adapter.DepositHistoryListAdapter
import com.instantonlinematka.instantonlinematka.adapter.onTimerCompleteListener
import com.instantonlinematka.instantonlinematka.databinding.DepostiHistoryFragmentBinding
import com.instantonlinematka.instantonlinematka.model.Data
import com.instantonlinematka.instantonlinematka.model.DepositHistoryResponse
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
class DepositHistoryFragment : Fragment() {

    lateinit var binding: DepostiHistoryFragmentBinding



    lateinit var contextMarket: Context

    lateinit var apiInterface: ApiInterface

    lateinit var sessionPrefs: SessionPrefs

    lateinit var gameList: ArrayList<Data>

    lateinit var adapter: DepositHistoryListAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DepostiHistoryFragmentBinding.inflate(inflater, container, false)
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

            getDepostiHistoryList()

        }

        apiInterface = RetrofitClient.getRetrfitInstance()

        gameList = ArrayList()

        adapter = DepositHistoryListAdapter(contextMarket, gameList)

        binding.recyclerView.layoutManager = LinearLayoutManager (
            context, LinearLayoutManager.VERTICAL, false
        )


        binding.recyclerView.adapter = adapter



//        adapter!!.onTimerComplete?.let {
//            it { int->
//
//                Toast.makeText(context,"Load",Toast.LENGTH_SHORT).show()
//                getDepostiHistoryList()
//
//            }
//        }
        binding.btnRetry.setSafeOnClickListener {
            getDepostiHistoryList()
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
                getDepostiHistoryList()
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

    fun getDepostiHistoryList() {

        if (Connectivity.isOnline(contextMarket)) {
            getDepostiHistoryListApiCall()
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
                        getDepostiHistoryList()
                    }
                    .setNegativeButton(getString(R.string.quit)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        (activity as DrawerActivity).finish()
                    }
                    .build()

            mBottomSheetDialog.show()
        }

    }

    fun getDepostiHistoryListApiCall() {

        binding.wp10progressBar.showProgressBar()

        binding.linearHome.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.txtTodayMarket.visibility= View.GONE

        val call = apiInterface.getDepositHistoryList(sessionPrefs.getString(Constants.USER_ID),"add_fund")

        call.enqueue(object : Callback<DepositHistoryResponse>{
            override fun onResponse(
                call: Call<DepositHistoryResponse>,
                response: Response<DepositHistoryResponse>
            ) {
                binding.swipeRefreshLayout.isRefreshing = false
                gameList.clear()

                val dataResponse = response.body()!!

                for (item in dataResponse.data) {

                    val amount = item.amount ?: ""
                    val date = item.date ?: ""
                    val status = item.status ?: ""
                    val time = item.time ?: ""
                    val transaction_id = item.transaction_id ?: ""

                    val historyData =  Data(amount, date, status, time,transaction_id)

                    gameList.add(historyData)
                }

                if (gameList.size == 0) {

                    binding.linearHome.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.txtTodayMarket.visibility= View.GONE
                }
                else {

                    adapter = DepositHistoryListAdapter(contextMarket, gameList)

                    binding.linearHome.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.txtTodayMarket.visibility= View.GONE
                }

                binding.wp10progressBar.hideProgressBar()
            }

            override fun onFailure(call: Call<DepositHistoryResponse>, t: Throwable) {

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

}
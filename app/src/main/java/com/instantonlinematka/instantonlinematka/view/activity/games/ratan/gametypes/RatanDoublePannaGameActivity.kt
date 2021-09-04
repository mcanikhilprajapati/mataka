package com.instantonlinematka.instantonlinematka.view.activity.games.ratan.gametypes

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.RatanGameViewPagerAdapter
import com.instantonlinematka.instantonlinematka.model.BidRequestResponse
import com.instantonlinematka.instantonlinematka.model.PannaGameDataList
import com.instantonlinematka.instantonlinematka.model.PannaList
import com.instantonlinematka.instantonlinematka.model.WalletBalanceResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.NotificationActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import ir.alirezabdn.wp7progress.WP10ProgressBar
import kotlinx.android.synthetic.main.activity_double_panna_game.*
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.*
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.btnNotification
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.btnProceed
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.circleImageView
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.lblMarketNameAnswer
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.lblNumbersAnswer
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.lblStatusAnswer
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.lblTotalPointsAnswer
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.tabLayout
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.toolbar_Wallet
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.toolbar_not_number
import kotlinx.android.synthetic.main.activity_ratan_double_panna_game.viewPager
import kotlinx.android.synthetic.main.alert_submit_game.*
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.android.synthetic.main.sheet_submit_game.*
import kotlinx.android.synthetic.main.sheet_submit_game.bidPointContainer
import kotlinx.android.synthetic.main.sheet_submit_game.btnCancel
import kotlinx.android.synthetic.main.sheet_submit_game.btnSubmitButton
import kotlinx.android.synthetic.main.sheet_submit_game.lblGameNameAnswer
import kotlinx.android.synthetic.main.sheet_submit_game.lblTotalBidAmountAnswer
import kotlinx.android.synthetic.main.sheet_submit_game.lblTotalBidAnswer
import kotlinx.android.synthetic.main.sheet_submit_game.lblWalletAfterDeductionAnswer
import kotlinx.android.synthetic.main.sheet_submit_game.lblWalletBeforeDeductionAnswer
import kotlinx.android.synthetic.main.sheet_submit_game.progressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class RatanDoublePannaGameActivity : AppCompatActivity() {

    lateinit var context: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var pannaGameList: ArrayList<PannaGameDataList>

    lateinit var gameList: ArrayList<PannaList>
    lateinit var panna0: ArrayList<String>
    lateinit var panna1: ArrayList<String>
    lateinit var panna2: ArrayList<String>
    lateinit var panna3: ArrayList<String>
    lateinit var panna4: ArrayList<String>
    lateinit var panna5: ArrayList<String>
    lateinit var panna6: ArrayList<String>
    lateinit var panna7: ArrayList<String>
    lateinit var panna8: ArrayList<String>
    lateinit var panna9: ArrayList<String>

    lateinit var adapter: RatanGameViewPagerAdapter

    var currentTab = 0

    var gameTypeId: String? = null; var gameType: String? = null;
    var gameId: String? = null; var gameMode: String? = null;
    var catId: String? = null;

    var Status: String? = null; var Date: String? = null;
    var Time: String? = null; var Numbers: String? = null;

    var GameName: String? = null; var OpenTime: String? = null;
    var CloseTime: String? = null; var FinalCurrentDate: String? = null;
    var FinalTotaPoints: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ratan_double_panna_game)

        context = this@RatanDoublePannaGameActivity

        sessionPrefs = SessionPrefs(context)

        apiInterface = RetrofitClient.getRetrfitInstance()

        gameId = intent.getStringExtra("gameId")
        gameMode = intent.getStringExtra("mode")
        gameType = intent.getStringExtra("type")
        catId = intent.getStringExtra("catId")
        Status = intent.getStringExtra("status")
        Date = intent.getStringExtra("date")
        Time = intent.getStringExtra("time")
        Numbers = intent.getStringExtra("numbers")

        lblMarketNameAnswer.text = gameMode
        //lblStatusAnswer.text = Status
        lblStatusAnswer.text = "Ratan Starline $gameMode "
        lblDateAnswer.text = Date
        lblTimeAnswer.text = Time
        lblNumbersAnswer.text = Numbers

        pannaGameList = ArrayList()

        gameList = ArrayList()
        panna0 = ArrayList()
        panna1 = ArrayList()
        panna2 = ArrayList()
        panna3 = ArrayList()
        panna4 = ArrayList()
        panna5 = ArrayList()
        panna6 = ArrayList()
        panna7 = ArrayList()
        panna8 = ArrayList()
        panna9 = ArrayList()

        adapter = RatanGameViewPagerAdapter(
            gameList,
            context,
            lblTotalPointsAnswer,
            this@RatanDoublePannaGameActivity,
            tabLayout,
            pannaGameList
        )

        viewPager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewPager)
        currentTab = viewPager.getCurrentItem()

        initializeNumbers()

        updateWallet()

        circleImageView.setSafeOnClickListener {
            onBackPressed()
        }

        btnProceed.setSafeOnClickListener {

            btnProceed.visibility = View.GONE

            val wallet = sessionPrefs.getString(Constants.WALLET)

            FinalTotaPoints = lblTotalPointsAnswer.text.toString().trim().replace("₹ ", "")

            if (pannaGameList.size == 0) {

                btnProceed.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@RatanDoublePannaGameActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.you_must_enter_a_value))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
            else if (wallet.isEmpty() || wallet.toInt() < FinalTotaPoints!!.toInt()) {

                btnProceed.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@RatanDoublePannaGameActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.you_do_not_have_enough_balance))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
            else {

                val mBottomSheetDialog = BottomSheetDialog(context)
                val sheetView: View = layoutInflater.inflate(R.layout.alert_submit_game, null)
                mBottomSheetDialog.setContentView(sheetView)
                mBottomSheetDialog.setCancelable(false)
                mBottomSheetDialog.show()

                val lblGameName = mBottomSheetDialog.lblGameNameAnswer1
                val lblTotalBid = mBottomSheetDialog.lblTotalBidAnswer1
                val lblTotalBidAmount = mBottomSheetDialog.lblTotalBidAmountAnswer1
                val lblWalletBeforeDeduction = mBottomSheetDialog.lblWalletBeforeDeductionAnswer1
                val lblWalletAfterDeduction = mBottomSheetDialog.lblWalletAfterDeductionAnswer1
                val linearContainer = mBottomSheetDialog.bidPointContainer1
                val btnCancel = mBottomSheetDialog.btnCancel1
                val btnSubmitButton = mBottomSheetDialog.btnSubmitButton1
                val wp10Progress = mBottomSheetDialog.progressBar1

                lblGameName.text = lblMarketNameAnswer.text.toString()
                lblTotalBid.text = pannaGameList.size.toString()
                lblTotalBidAmount.text = lblTotalPointsAnswer.text.toString().trim()
                lblWalletBeforeDeduction.text = "₹ ${sessionPrefs.getString(Constants.WALLET)}"
                val FinalDeduction = sessionPrefs.getString(Constants.WALLET).toInt() - FinalTotaPoints!!.toInt()
                lblWalletAfterDeduction.text = "₹ ${FinalDeduction}"

                var i = 0
                for (items in pannaGameList) {
                    val inflateView: View = getLayoutInflater().inflate(
                        R.layout.sub_view_sheet_game,
                        linearContainer,
                        false
                    )
                    val lblIndex = inflateView.findViewById<TextView>(R.id.lblIndexAnswer)
                    val lblDigit = inflateView.findViewById<TextView>(R.id.lblDigitsAnswer)
                    val lblPoints = inflateView.findViewById<TextView>(R.id.lblPointsAnswer)
                    val lblMode = inflateView.findViewById<TextView>(R.id.lblModeAnswer)

                    lblIndex.text = (i + 1).toString()
                    lblDigit.text = items.numbers.toString()
                    lblPoints.text = items.points.toString()
                    lblMode.text = gameMode
                    linearContainer.addView(inflateView)
                    i++
                }

                btnCancel.setSafeOnClickListener {
                    mBottomSheetDialog.dismiss()
                    btnProceed.visibility = View.VISIBLE
                }

                btnSubmitButton.setSafeOnClickListener {

                    if (Connectivity.isOnline(context)) {
                        makeRequest(btnCancel, btnSubmitButton, wp10Progress, mBottomSheetDialog)
                    }
                    else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@RatanDoublePannaGameActivity)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.no_internet_found))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }

                    btnProceed.visibility = View.VISIBLE
                }

            }

        }

        btnNotification.setSafeOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val wallet = sessionPrefs.getString(Constants.WALLET)
        if (wallet.isEmpty()) {
            toolbar_Wallet.text = "- - -"
        } else {
            toolbar_Wallet.text = "₹"+wallet
        }
        val notification_count = sessionPrefs.getString(Constants.NOTIFICATION_COUNT)
        if (notification_count.isEmpty() || notification_count.toInt() == 0) {
            toolbar_not_number.visibility = View.GONE
            toolbar_not_number.text = "0"
        } else {
            toolbar_not_number.visibility = View.VISIBLE
            toolbar_not_number.text = notification_count
        }
    }

    override fun onBackPressed() {
        finish()
    }

    fun initializeNumbers() {

        panna0.add("118")
        panna0.add("226")
        panna0.add("244")
        panna0.add("299")
        panna0.add("334")
        panna0.add("488")
        panna0.add("668")
        panna0.add("677")
        panna0.add("550")
        gameList.add(PannaList("0", panna0))
        panna1.add("119")
        panna1.add("155")
        panna1.add("227")
        panna1.add("335")
        panna1.add("344")
        panna1.add("399")
        panna1.add("588")
        panna1.add("669")
        panna1.add("100")
        gameList.add(PannaList("1", panna1))
        panna2.add("110")
        panna2.add("228")
        panna2.add("255")
        panna2.add("336")
        panna2.add("499")
        panna2.add("660")
        panna2.add("688")
        panna2.add("778")
        panna2.add("200")
        gameList.add(PannaList("2", panna2))
        panna3.add("166")
        panna3.add("229")
        panna3.add("337")
        panna3.add("355")
        panna3.add("445")
        panna3.add("599")
        panna3.add("779")
        panna3.add("788")
        panna3.add("300")
        gameList.add(PannaList("3", panna3))
        panna4.add("112")
        panna4.add("220")
        panna4.add("266")
        panna4.add("338")
        panna4.add("446")
        panna4.add("455")
        panna4.add("699")
        panna4.add("770")
        panna4.add("400")
        gameList.add(PannaList("4", panna4))
        panna5.add("113")
        panna5.add("122")
        panna5.add("177")
        panna5.add("339")
        panna5.add("366")
        panna5.add("447")
        panna5.add("799")
        panna5.add("889")
        panna5.add("500")
        gameList.add(PannaList("5", panna5))
        panna6.add("114")
        panna6.add("277")
        panna6.add("330")
        panna6.add("448")
        panna6.add("466")
        panna6.add("556")
        panna6.add("880")
        panna6.add("899")
        panna6.add("600")
        gameList.add(PannaList("6", panna6))
        panna7.add("115")
        panna7.add("133")
        panna7.add("188")
        panna7.add("223")
        panna7.add("377")
        panna7.add("449")
        panna7.add("557")
        panna7.add("566")
        panna7.add("700")
        gameList.add(PannaList("7", panna7))
        panna8.add("116")
        panna8.add("224")
        panna8.add("233")
        panna8.add("288")
        panna8.add("440")
        panna8.add("477")
        panna8.add("558")
        panna8.add("990")
        panna8.add("800")
        gameList.add(PannaList("8", panna8))
        panna9.add("117")
        panna9.add("144")
        panna9.add("199")
        panna9.add("225")
        panna9.add("388")
        panna9.add("559")
        panna9.add("577")
        panna9.add("667")
        panna9.add("900")
        gameList.add(PannaList("9", panna9))

        adapter.notifyDataSetChanged()
    }

    fun makeRequest(cancelButton: AppCompatButton, submitButton: AppCompatButton,
                    wp10Progress: WP10ProgressBar, mBottomSheetDialog: BottomSheetDialog) {

        submitButton.visibility = View.GONE
        cancelButton.visibility = View.GONE

        wp10Progress.showProgressBar()

        val pointsParams = itemsToConvert(pannaGameList).toString()

        val call = apiInterface.ratanBidRequest(
            sessionPrefs.getString(Constants.USER_ID), gameId!!, catId!!, pointsParams
        )

        call.enqueue(object : Callback<BidRequestResponse> {

            override fun onResponse(
                call: Call<BidRequestResponse>,
                response: Response<BidRequestResponse>
            ) {

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val WalletAmount = responseData.walletamount
                    sessionPrefs.addString(Constants.WALLET, WalletAmount)

                    val mDialog = Dialog(context)
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog.setCancelable(false)
                    mDialog.setContentView(R.layout.alert_success)
                    mDialog.window!!.setGravity(Gravity.CENTER)

                    mDialog.show()

                    val lblSuccessTitle = mDialog.lblSuccessTitleAnswer
                    val lblSuccessMessage = mDialog.lblSuccessMessageAnswer

                    lblSuccessTitle.text = getString(R.string.successful)
                    lblSuccessMessage.text = getString(R.string.bidding_successful)

                    mBottomSheetDialog.dismiss()

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000L)

                        mDialog.dismiss()
                        finish()
                    }
                } else {

                    val status = responseData.status
                    if (status.contentEquals("5")) {
                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@RatanDoublePannaGameActivity)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(responseData.data)
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    finish()
                                    setResult(5)
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }
                    else {
                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@RatanDoublePannaGameActivity)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(responseData.data)
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }
                }

                wp10Progress.hideProgressBar()

                submitButton.visibility = View.VISIBLE
                cancelButton.visibility = View.VISIBLE

            }

            override fun onFailure(call: Call<BidRequestResponse>, t: Throwable) {

                wp10Progress.hideProgressBar()

                submitButton.visibility = View.VISIBLE
                cancelButton.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@RatanDoublePannaGameActivity)
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

    //Json converter
    fun itemsToConvert(list: ArrayList<PannaGameDataList>): JsonArray {

        val jsonArray = JsonArray()

        for (items in list) {

            val jsonObject = JsonObject() // /sub Object

            try {

                jsonObject.addProperty("panna_result", items.numbers)
                jsonObject.addProperty("single_digit_result", "")
                jsonObject.addProperty("amount", items.points)
                jsonArray.add(jsonObject)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        return jsonArray
    }

    // Wallet Amount Updater
    fun updateWallet() {

        if (Connectivity.isOnline(context)) {

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
                        val wallet = sessionPrefs.getString(Constants.WALLET)
                        if (wallet.isEmpty()) {
                            toolbar_Wallet.text = "- - -"
                        } else {
                            toolbar_Wallet.text = "₹"+wallet
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
package com.instantonlinematka.instantonlinematka.view.activity.games.market.gametypes

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
import com.instantonlinematka.instantonlinematka.adapter.GameViewPagerAdapter
import com.instantonlinematka.instantonlinematka.model.*
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.*
import com.instantonlinematka.instantonlinematka.view.activity.NotificationActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import ir.alirezabdn.wp7progress.WP10ProgressBar
import kotlinx.android.synthetic.main.activity_double_panna_game.*
import kotlinx.android.synthetic.main.activity_triple_panna_game.*
import kotlinx.android.synthetic.main.activity_triple_panna_game.btnNotification
import kotlinx.android.synthetic.main.activity_triple_panna_game.btnProceed
import kotlinx.android.synthetic.main.activity_triple_panna_game.circleImageView
import kotlinx.android.synthetic.main.activity_triple_panna_game.constraintSingleGame
import kotlinx.android.synthetic.main.activity_triple_panna_game.lblCloseTimeAnswer
import kotlinx.android.synthetic.main.activity_triple_panna_game.lblDate
import kotlinx.android.synthetic.main.activity_triple_panna_game.lblGameType
import kotlinx.android.synthetic.main.activity_triple_panna_game.lblMarketNameAnswer
import kotlinx.android.synthetic.main.activity_triple_panna_game.lblNumbersAnswer
import kotlinx.android.synthetic.main.activity_triple_panna_game.lblOpenTimeAnswer
import kotlinx.android.synthetic.main.activity_triple_panna_game.lblStatusAnswer
import kotlinx.android.synthetic.main.activity_triple_panna_game.lblTotalPointsAnswer
import kotlinx.android.synthetic.main.activity_triple_panna_game.linearHome
import kotlinx.android.synthetic.main.activity_triple_panna_game.tabLayout
import kotlinx.android.synthetic.main.activity_triple_panna_game.toolbar_Wallet
import kotlinx.android.synthetic.main.activity_triple_panna_game.toolbar_not_number
import kotlinx.android.synthetic.main.activity_triple_panna_game.viewPager
import kotlinx.android.synthetic.main.activity_triple_panna_game.wp10progressBar
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("RestrictedApi")
class TriplePannaGameActivity : AppCompatActivity() {

    lateinit var context: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var pannaGameList: ArrayList<PannaGameDataList>

    lateinit var gameList: ArrayList<PannaList>
    lateinit var panna0: ArrayList<String>

    lateinit var adapter: GameViewPagerAdapter

    var currentTab = 0

    var gameTypeId: String? = null; var gameType: String? = null;
    var gameId: String? = null; var gameMode: String? = null;
    var catId: String? = null;

    var GameName: String? = null; var Status: String? = null
    var OpenTime: String? = null; var CloseTime: String? = null
    var Numbers: String? = null; var CurrentTime: String? = null
    var FinalCurrentDate: String? = null; var FinalTotaPoints: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_triple_panna_game)

        context = this@TriplePannaGameActivity

        sessionPrefs = SessionPrefs(context)

        apiInterface = RetrofitClient.getRetrfitInstance()

        gameTypeId = intent.getStringExtra("gameTypeId")
        gameMode = intent.getStringExtra("mode")
        gameType = intent.getStringExtra("type")
        gameId = intent.getStringExtra("gameId")
        catId = intent.getStringExtra("catId")

        pannaGameList = ArrayList()

        gameList = ArrayList()
        panna0 = ArrayList()

        adapter = GameViewPagerAdapter(
            gameList,
            context,
            lblTotalPointsAnswer,
            this@TriplePannaGameActivity,
            tabLayout,
            pannaGameList
        )

        viewPager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewPager)
        currentTab = viewPager.getCurrentItem()

        initializeNumbers()

        getGameData()

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
                    BottomSheetMaterialDialog.Builder(this@TriplePannaGameActivity)
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
                    BottomSheetMaterialDialog.Builder(this@TriplePannaGameActivity)
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
                            BottomSheetMaterialDialog.Builder(this@TriplePannaGameActivity)
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
            toolbar_Wallet.text = wallet
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

        panna0.add("000")
        panna0.add("777")
        panna0.add("444")
        panna0.add("111")
        panna0.add("888")
        panna0.add("222")
        panna0.add("999")
        panna0.add("666")
        panna0.add("333")
        panna0.add("555")
        gameList.add(PannaList("0", panna0))

        adapter.notifyDataSetChanged()
    }

    fun getGameData() {

        if (Connectivity.isOnline(context)) {
            makeGameDataApiCall()
        }
        else {
            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@TriplePannaGameActivity)
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

    fun makeGameDataApiCall() {

        wp10progressBar.showProgressBar()

        linearHome.visibility = View.GONE
        constraintSingleGame.visibility = View.GONE

        val call = apiInterface.getGameData(
            sessionPrefs.getString(Constants.USER_ID), gameId.toString()
        )

        call.enqueue(object : Callback<GameDataResponse> {
            override fun onResponse(
                call: Call<GameDataResponse>,
                response: Response<GameDataResponse>
            ) {

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    linearHome.visibility = View.GONE
                    constraintSingleGame.visibility = View.VISIBLE

                    GameName = responseData.data.game_type_name
                    Status = responseData.data.game_status
                    OpenTime = responseData.data.open_time
                    CloseTime = responseData.data.close_time
                    Numbers = responseData.data.oldgameresult
                    CurrentTime = responseData.data.time
                    FinalCurrentDate = responseData.data.date

                    try {
                        val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        val inputDate = sdfInput.parse(FinalCurrentDate!!)
                        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                        try {
                            val currentDate = sdf.format(inputDate!!)
                            lblDate.text = currentDate
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    lblMarketNameAnswer.text = GameName
                    toolbar_title_game_name3.text = "$GameName $gameType"
                    lblOpenTimeAnswer.text = ConvertTime.ConvertTimeToPM(OpenTime!!)
                    lblCloseTimeAnswer.text = ConvertTime.ConvertTimeToPM(CloseTime!!)
                    lblNumbersAnswer.text = Numbers
                    //lblStatusAnswer.text = "$gameMode - - - $gameType"
                    lblStatusAnswer.text = "$GameName  $gameMode "
                    lblGameType.text = gameType

                } else {

                    linearHome.visibility = View.VISIBLE
                    constraintSingleGame.visibility = View.GONE
                }

                wp10progressBar.hideProgressBar()

            }

            override fun onFailure(call: Call<GameDataResponse>, t: Throwable) {

                wp10progressBar.hideProgressBar()

                linearHome.visibility = View.VISIBLE
                constraintSingleGame.visibility = View.GONE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(context as TriplePannaGameActivity)
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

    fun makeRequest(cancelButton: AppCompatButton, submitButton: AppCompatButton,
                    wp10Progress: WP10ProgressBar, mBottomSheetDialog: BottomSheetDialog) {

        submitButton.visibility = View.GONE
        cancelButton.visibility = View.GONE

        wp10Progress.showProgressBar()

        val UniqueSessionID = UUID.randomUUID().toString()

        val pointsParams = itemsToConvert(pannaGameList).toString()

        val call = apiInterface.bidRequest(
            sessionPrefs.getString(Constants.USER_ID),
            gameTypeId!!, UniqueSessionID, FinalCurrentDate!!, catId!!, gameType!!.toLowerCase(
                Locale.ENGLISH
            ), pointsParams
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
                            BottomSheetMaterialDialog.Builder(context as TriplePannaGameActivity)
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
                            BottomSheetMaterialDialog.Builder(context as TriplePannaGameActivity)
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
                    BottomSheetMaterialDialog.Builder(context as TriplePannaGameActivity)
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

                jsonObject.addProperty("number", items.numbers)
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
                            toolbar_Wallet.text = wallet
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
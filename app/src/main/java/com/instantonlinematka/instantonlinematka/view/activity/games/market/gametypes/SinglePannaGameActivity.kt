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
import kotlinx.android.synthetic.main.activity_single_game.*
import kotlinx.android.synthetic.main.activity_single_panna_game.*
import kotlinx.android.synthetic.main.activity_single_panna_game.btnNotification
import kotlinx.android.synthetic.main.activity_single_panna_game.btnProceed
import kotlinx.android.synthetic.main.activity_single_panna_game.circleImageView
import kotlinx.android.synthetic.main.activity_single_panna_game.constraintSingleGame
import kotlinx.android.synthetic.main.activity_single_panna_game.lblCloseTimeAnswer
import kotlinx.android.synthetic.main.activity_single_panna_game.lblDate
import kotlinx.android.synthetic.main.activity_single_panna_game.lblGameType
import kotlinx.android.synthetic.main.activity_single_panna_game.lblMarketNameAnswer
import kotlinx.android.synthetic.main.activity_single_panna_game.lblNumbersAnswer
import kotlinx.android.synthetic.main.activity_single_panna_game.lblOpenTimeAnswer
import kotlinx.android.synthetic.main.activity_single_panna_game.lblStatusAnswer
import kotlinx.android.synthetic.main.activity_single_panna_game.lblTotalPointsAnswer
import kotlinx.android.synthetic.main.activity_single_panna_game.linearHome
import kotlinx.android.synthetic.main.activity_single_panna_game.toolbar_Wallet
import kotlinx.android.synthetic.main.activity_single_panna_game.toolbar_not_number
import kotlinx.android.synthetic.main.activity_single_panna_game.wp10progressBar
import kotlinx.android.synthetic.main.alert_submit_game.*
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.android.synthetic.main.sheet_submit_game.*
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
class SinglePannaGameActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_single_panna_game)

        context = this@SinglePannaGameActivity

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
        panna1 = ArrayList()
        panna2 = ArrayList()
        panna3 = ArrayList()
        panna4 = ArrayList()
        panna5 = ArrayList()
        panna6 = ArrayList()
        panna7 = ArrayList()
        panna8 = ArrayList()
        panna9 = ArrayList()

        adapter = GameViewPagerAdapter(
            gameList,
            context,
            lblTotalPointsAnswer,
            this@SinglePannaGameActivity,
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
                    BottomSheetMaterialDialog.Builder(this@SinglePannaGameActivity)
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
                    BottomSheetMaterialDialog.Builder(this@SinglePannaGameActivity)
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
                            BottomSheetMaterialDialog.Builder(this@SinglePannaGameActivity)
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

        panna0.add("127")
        panna0.add("136")
        panna0.add("145")
        panna0.add("190")
        panna0.add("235")
        panna0.add("280")
        panna0.add("370")
        panna0.add("389")
        panna0.add("460")
        panna0.add("479")
        panna0.add("569")
        panna0.add("578")
        gameList.add(PannaList("0", panna0))
        panna1.add("137")
        panna1.add("128")
        panna1.add("146")
        panna1.add("236")
        panna1.add("245")
        panna1.add("290")
        panna1.add("380")
        panna1.add("470")
        panna1.add("489")
        panna1.add("560")
        panna1.add("678")
        panna1.add("579")
        gameList.add(PannaList("1", panna1))
        panna2.add("129")
        panna2.add("138")
        panna2.add("147")
        panna2.add("156")
        panna2.add("237")
        panna2.add("246")
        panna2.add("345")
        panna2.add("390")
        panna2.add("480")
        panna2.add("570")
        panna2.add("589")
        panna2.add("679")
        gameList.add(PannaList("2", panna2))
        panna3.add("120")
        panna3.add("139")
        panna3.add("148")
        panna3.add("157")
        panna3.add("238")
        panna3.add("247")
        panna3.add("256")
        panna3.add("346")
        panna3.add("490")
        panna3.add("580")
        panna3.add("670")
        panna3.add("689")
        gameList.add(PannaList("3", panna3))
        panna4.add("130")
        panna4.add("149")
        panna4.add("158")
        panna4.add("167")
        panna4.add("239")
        panna4.add("248")
        panna4.add("257")
        panna4.add("347")
        panna4.add("356")
        panna4.add("590")
        panna4.add("680")
        panna4.add("789")
        gameList.add(PannaList("4", panna4))
        panna5.add("140")
        panna5.add("159")
        panna5.add("168")
        panna5.add("230")
        panna5.add("249")
        panna5.add("258")
        panna5.add("267")
        panna5.add("348")
        panna5.add("357")
        panna5.add("456")
        panna5.add("690")
        panna5.add("780")
        gameList.add(PannaList("5", panna5))
        panna6.add("123")
        panna6.add("150")
        panna6.add("169")
        panna6.add("178")
        panna6.add("240")
        panna6.add("259")
        panna6.add("268")
        panna6.add("349")
        panna6.add("358")
        panna6.add("367")
        panna6.add("457")
        panna6.add("790")
        gameList.add(PannaList("6", panna6))
        panna7.add("124")
        panna7.add("160")
        panna7.add("179")
        panna7.add("250")
        panna7.add("269")
        panna7.add("278")
        panna7.add("340")
        panna7.add("359")
        panna7.add("368")
        panna7.add("458")
        panna7.add("467")
        panna7.add("890")
        gameList.add(PannaList("7", panna7))
        panna8.add("125")
        panna8.add("134")
        panna8.add("170")
        panna8.add("189")
        panna8.add("260")
        panna8.add("279")
        panna8.add("350")
        panna8.add("369")
        panna8.add("378")
        panna8.add("459")
        panna8.add("468")
        panna8.add("567")
        gameList.add(PannaList("8", panna8))
        panna9.add("126")
        panna9.add("135")
        panna9.add("180")
        panna9.add("234")
        panna9.add("270")
        panna9.add("289")
        panna9.add("360")
        panna9.add("379")
        panna9.add("450")
        panna9.add("469")
        panna9.add("478")
        panna9.add("568")
        gameList.add(PannaList("9", panna9))

        adapter.notifyDataSetChanged()
    }

    fun getGameData() {

        if (Connectivity.isOnline(context)) {
            makeGameDataApiCall()
        }
        else {
            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@SinglePannaGameActivity)
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
                    toolbar_title_game_name1.text = "$GameName $gameType"
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
                    BottomSheetMaterialDialog.Builder(context as SinglePannaGameActivity)
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

        wp10Progress.showProgressBar()

        submitButton.visibility = View.GONE
        cancelButton.visibility = View.GONE

        val UniqueSessionID = UUID.randomUUID().toString()

        val pointsParams = itemsToConvert(pannaGameList).toString()

        val call = apiInterface.bidRequest(
            sessionPrefs.getString(Constants.USER_ID),
            gameTypeId!!, UniqueSessionID, FinalCurrentDate!!, catId!!, gameType!!.toLowerCase(
                Locale.ENGLISH), pointsParams
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
                            BottomSheetMaterialDialog.Builder(context as SinglePannaGameActivity)
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
                            BottomSheetMaterialDialog.Builder(context as SinglePannaGameActivity)
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
                    BottomSheetMaterialDialog.Builder(context as SinglePannaGameActivity)
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
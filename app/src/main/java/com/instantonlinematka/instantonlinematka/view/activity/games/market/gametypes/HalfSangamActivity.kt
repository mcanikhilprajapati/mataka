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
import com.instantonlinematka.instantonlinematka.adapter.halfsangam.PannaAdapter
import com.instantonlinematka.instantonlinematka.model.*
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.*
import com.instantonlinematka.instantonlinematka.view.activity.NotificationActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_half_sangam.*
import kotlinx.android.synthetic.main.activity_half_sangam.btnNotification
import kotlinx.android.synthetic.main.activity_half_sangam.btnProceed
import kotlinx.android.synthetic.main.activity_half_sangam.circleImageView
import kotlinx.android.synthetic.main.activity_half_sangam.constraintSingleGame
import kotlinx.android.synthetic.main.activity_half_sangam.lblCloseTimeAnswer
import kotlinx.android.synthetic.main.activity_half_sangam.lblDate
import kotlinx.android.synthetic.main.activity_half_sangam.lblMarketNameAnswer
import kotlinx.android.synthetic.main.activity_half_sangam.lblNumbersAnswer
import kotlinx.android.synthetic.main.activity_half_sangam.lblOpenTimeAnswer
import kotlinx.android.synthetic.main.activity_half_sangam.lblStatusAnswer
import kotlinx.android.synthetic.main.activity_half_sangam.lblTotalPointsAnswer
import kotlinx.android.synthetic.main.activity_half_sangam.linearHome
import kotlinx.android.synthetic.main.activity_half_sangam.toolbar_Wallet
import kotlinx.android.synthetic.main.activity_half_sangam.toolbar_not_number
import kotlinx.android.synthetic.main.activity_half_sangam.wp10progressBar
import kotlinx.android.synthetic.main.activity_triple_panna_game.*
import kotlinx.android.synthetic.main.alert_submit_game.*
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.android.synthetic.main.sangam_view_sheet_game.view.*
import kotlinx.android.synthetic.main.sheet_submit_game.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("RestrictedApi")
class HalfSangamActivity : AppCompatActivity() {

    lateinit var context: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var sangamGameList: ArrayList<SangamPointGameData>

    lateinit var pannaAdapter: PannaAdapter

    lateinit var pannaList: ArrayList<SangamGameData>

    lateinit var AllPannaNumber: ArrayList<SangamGameData>

    lateinit var pannaData: ArrayList<SangamGameData>

    var pannaModel: SangamGameData? = null

    var gameTypeId: String? = null; var gameType: String? = null;
    var gameId: String? = null; var gameMode: String? = null;
    var catId: String? = null;

    var GameName: String? = null; var Status: String? = null
    var OpenTime: String? = null; var CloseTime: String? = null
    var Numbers: String? = null; var CurrentTime: String? = null
    var FinalCurrentDate: String? = null; var FinalTotaPoints: String? = null

    var WhichPanna: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_half_sangam)

        context = this@HalfSangamActivity

        sessionPrefs = SessionPrefs(context)

        apiInterface = RetrofitClient.getRetrfitInstance()

        gameTypeId = intent.getStringExtra("gameTypeId")
        gameMode = intent.getStringExtra("mode")
        gameType = intent.getStringExtra("type")
        gameId = intent.getStringExtra("gameId")
        catId = intent.getStringExtra("catId")

        lblDigit.text = getString(R.string.open_digit)
        lblPanna.text = getString(R.string.close_panna)
        txtEnterDigits.hint = getString(R.string.enter_digit)
        txtEnterPanna.hint = getString(R.string.enter_panna)

        sangamGameList = ArrayList()

        pannaList = ArrayList()
        pannaData = ArrayList()
        AllPannaNumber = ArrayList()

        WhichPanna = getString(R.string.close_panna)

        initializeNumbers()

        getGameData()

        updateWallet()

        circleImageView.setSafeOnClickListener {
            onBackPressed()
        }

        btnChangeDigits.setSafeOnClickListener {

            val panna = lblPanna.text.toString().trim()

            if (panna.contentEquals(context.getString(R.string.open_panna))) {
                lblPanna.text = getString(R.string.close_panna)
                lblDigit.text = getString(R.string.open_digit)
                txtEnterPanna.hint = getString(R.string.enter_panna)
                txtEnterDigits.hint = getString(R.string.enter_digit)
            }
            else {
                lblPanna.text = getString(R.string.open_panna)
                lblDigit.text = getString(R.string.close_digit)
                txtEnterPanna.hint = getString(R.string.enter_panna)
                txtEnterDigits.hint = getString(R.string.enter_digit)
            }

            txtEnterPanna.setText("")
            txtEnterDigits.setText("")
            txtEnterPoints.setText("")

            sangamGameList.clear()

            LinerLayoutContiner.removeAllViews()
        }

        btnAdd.setSafeOnClickListener {

            val PannaGame = lblPanna.text.toString().trim()

            val Panna = txtEnterPanna.text.toString()
            val Digit = txtEnterDigits.text.toString()
            val Points= txtEnterPoints.text.toString()

            if (Panna.isEmpty() || Digit.isEmpty() || Points.isEmpty()) {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@HalfSangamActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.you_must_enter_a_add))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
            else if (Points.toInt() < 10) {
                txtEnterPoints.setError(context.getString(R.string.minimum_value_must_be_10))
                txtEnterPoints.requestFocus()
            }
            else {

                var pannaItem = false

                for (items in 0 until AllPannaNumber.size) {

                    if (Panna.contentEquals(AllPannaNumber.get(items).points)) {

                        pannaItem = true
                    }
                }

                if (pannaItem) {

                    var SangamGame: SangamPointGameData? = null

                    if (PannaGame.contentEquals(WhichPanna!!)) {

                        SangamGame = SangamPointGameData(
                            "", Digit, Points, "",
                            Panna, Digit, ""
                        )
                    }
                    else {
                        SangamGame = SangamPointGameData(
                            "", Digit, Points, Panna, "",
                            "", Digit
                        )
                    }

                    sangamGameList.add(SangamGame)

                    txtEnterPanna.setText("")
                    txtEnterDigits.setText("")
                    txtEnterPoints.setText("")
                }
                else {
                    txtEnterPanna.error = getString(R.string.enter_panna_number_from_list)
                    txtEnterPanna.requestFocus()
                }
            }

            if (sangamGameList.size > 0) {
                LinerLayoutContiner.removeAllViews()

                for (i in 0 until sangamGameList.size) {

                    val inflateView: View = getLayoutInflater()
                        .inflate(R.layout.sangam_view_sheet_game, LinerLayoutContiner, false)
                    val SangamAnswer = inflateView.lblSangamAnswer
                    val PointsAnswer = inflateView.lblPointsAnswer
                    val TypeAnswer = inflateView.lblTypeAnswer
                    val GameMode = inflateView.btnMode

                    if (PannaGame.contentEquals(WhichPanna!!)) {
                        SangamAnswer.text = "${sangamGameList.get(i).open_digit} - " +
                                "${sangamGameList.get(i).close_panna}"
                    }
                    else {
                        SangamAnswer.text = "${sangamGameList.get(i).open_panna} - " +
                                "${sangamGameList.get(i).close_digit}"
                    }

                    PointsAnswer.text = sangamGameList.get(i).point
                    TypeAnswer.text = gameMode

                    val finalI = i
                    GameMode.setSafeOnClickListener {

                        if (sangamGameList.size == finalI) {
                            LinerLayoutContiner.removeAllViews()
                            sangamGameList.clear()
                        }
                        else {
                            LinerLayoutContiner.removeViewAt(finalI)
                            sangamGameList.removeAt(finalI)
                        }

                        var totalPoints = 0

                        if (sangamGameList.isEmpty()) {
                            lblTotalPointsAnswer.text = "- - -"
                        }
                        else {
                            for (items in sangamGameList) {
                                totalPoints = totalPoints + items.point.toInt()
                                lblTotalPointsAnswer.text = "₹ ${totalPoints}"
                                FinalTotaPoints = totalPoints.toString()
                            }
                        }
                    }

                    var totalPoints = 0

                    if (sangamGameList.isEmpty()) {
                        lblTotalPointsAnswer.text = "- - -"
                    }
                    else {
                        for (items in sangamGameList) {
                            totalPoints = totalPoints + items.point.toInt()
                            lblTotalPointsAnswer.text = "₹ ${totalPoints}"
                            FinalTotaPoints = totalPoints.toString()
                        }
                    }

                    LinerLayoutContiner.addView(inflateView)
                }
            }
        }

        btnProceed.setSafeOnClickListener {

            btnProceed.visibility = View.GONE

            val wallet = sessionPrefs.getString(Constants.WALLET)

            if (sangamGameList.isEmpty()) {

                btnProceed.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@HalfSangamActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.add_game_before_submit))
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
                    BottomSheetMaterialDialog.Builder(this@HalfSangamActivity)
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
                lblTotalBid.text = sangamGameList.size.toString()
                lblTotalBidAmount.text = lblTotalPointsAnswer.text.toString().trim()
                lblWalletBeforeDeduction.text = "₹ ${sessionPrefs.getString(Constants.WALLET)}"
                val FinalDeduction = sessionPrefs.getString(Constants.WALLET).toInt() - FinalTotaPoints!!.toInt()
                lblWalletAfterDeduction.text = "₹ ${FinalDeduction}"

                var i = 0
                for (items in sangamGameList) {
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
                    lblDigit.text = items.digit.toString()
                    lblPoints.text = items.point.toString()
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
                        makeRequest(btnCancel, btnSubmitButton, progressBar1, mBottomSheetDialog)
                    }
                    else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(this@HalfSangamActivity)
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

    fun getGameData() {

        if (Connectivity.isOnline(context)) {
            makeGameDataApiCall()
        }
        else {
            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@HalfSangamActivity)
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

        wp10progressBar.visibility = View.VISIBLE

        linearHome.visibility = View.GONE
        constraintSingleGame.visibility = View.GONE

        val call = apiInterface.getGameData(
            sessionPrefs.getString(Constants.USER_ID), gameTypeId!!
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
                    toolbar_title_game_name4.text = "$GameName $gameType"
                    lblOpenTimeAnswer.text = ConvertTime.ConvertTimeToPM(OpenTime!!)
                    lblCloseTimeAnswer.text = ConvertTime.ConvertTimeToPM(CloseTime!!)
                    lblNumbersAnswer.text = Numbers
                    //lblStatusAnswer.text = "$gameMode"
                    lblStatusAnswer.text = "$GameName  $gameMode "

                } else {

                    linearHome.visibility = View.VISIBLE
                    constraintSingleGame.visibility = View.GONE
                }

                wp10progressBar.visibility = View.GONE

            }

            override fun onFailure(call: Call<GameDataResponse>, t: Throwable) {

                wp10progressBar.visibility = View.GONE

                linearHome.visibility = View.VISIBLE
                constraintSingleGame.visibility = View.GONE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(context as HalfSangamActivity)
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

    fun initializeNumbers() {

        pannaList.add(SangamGameData("0", "127"))
        pannaList.add(SangamGameData("0", "136"))
        pannaList.add(SangamGameData("0", "145"))
        pannaList.add(SangamGameData("0", "190"))
        pannaList.add(SangamGameData("0", "235"))
        pannaList.add(SangamGameData("0", "280"))
        pannaList.add(SangamGameData("0", "370"))
        pannaList.add(SangamGameData("0", "389"))
        pannaList.add(SangamGameData("0", "460"))
        pannaList.add(SangamGameData("0", "479"))
        pannaList.add(SangamGameData("0", "569"))
        pannaList.add(SangamGameData("0", "578"))
        pannaList.add(SangamGameData("0", "118"))
        pannaList.add(SangamGameData("0", "226"))
        pannaList.add(SangamGameData("0", "244"))
        pannaList.add(SangamGameData("0", "299"))
        pannaList.add(SangamGameData("0", "334"))
        pannaList.add(SangamGameData("0", "488"))
        pannaList.add(SangamGameData("0", "668"))
        pannaList.add(SangamGameData("0", "677"))
        pannaList.add(SangamGameData("0", "550"))
        pannaList.add(SangamGameData("0", "000"))

        pannaList.add(SangamGameData("1", "137"))
        pannaList.add(SangamGameData("1", "128"))
        pannaList.add(SangamGameData("1", "146"))
        pannaList.add(SangamGameData("1", "236"))
        pannaList.add(SangamGameData("1", "245"))
        pannaList.add(SangamGameData("1", "290"))
        pannaList.add(SangamGameData("1", "380"))
        pannaList.add(SangamGameData("1", "470"))
        pannaList.add(SangamGameData("1", "489"))
        pannaList.add(SangamGameData("1", "560"))
        pannaList.add(SangamGameData("1", "678"))
        pannaList.add(SangamGameData("1", "579"))
        pannaList.add(SangamGameData("1", "119"))
        pannaList.add(SangamGameData("1", "155"))
        pannaList.add(SangamGameData("1", "227"))
        pannaList.add(SangamGameData("1", "335"))
        pannaList.add(SangamGameData("1", "344"))
        pannaList.add(SangamGameData("1", "399"))
        pannaList.add(SangamGameData("1", "588"))
        pannaList.add(SangamGameData("1", "669"))
        pannaList.add(SangamGameData("1", "100"))
        pannaList.add(SangamGameData("1", "777"))

        pannaList.add(SangamGameData("2", "129"))
        pannaList.add(SangamGameData("2", "138"))
        pannaList.add(SangamGameData("2", "147"))
        pannaList.add(SangamGameData("2", "156"))
        pannaList.add(SangamGameData("2", "237"))
        pannaList.add(SangamGameData("2", "246"))
        pannaList.add(SangamGameData("2", "345"))
        pannaList.add(SangamGameData("2", "390"))
        pannaList.add(SangamGameData("2", "480"))
        pannaList.add(SangamGameData("2", "570"))
        pannaList.add(SangamGameData("2", "589"))
        pannaList.add(SangamGameData("2", "679"))
        pannaList.add(SangamGameData("2", "110"))
        pannaList.add(SangamGameData("2", "228"))
        pannaList.add(SangamGameData("2", "255"))
        pannaList.add(SangamGameData("2", "336"))
        pannaList.add(SangamGameData("2", "499"))
        pannaList.add(SangamGameData("2", "660"))
        pannaList.add(SangamGameData("2", "688"))
        pannaList.add(SangamGameData("2", "778"))
        pannaList.add(SangamGameData("2", "200"))
        pannaList.add(SangamGameData("2", "444"))

        pannaList.add(SangamGameData("3", "120"))
        pannaList.add(SangamGameData("3", "139"))
        pannaList.add(SangamGameData("3", "148"))
        pannaList.add(SangamGameData("3", "157"))
        pannaList.add(SangamGameData("3", "238"))
        pannaList.add(SangamGameData("3", "247"))
        pannaList.add(SangamGameData("3", "256"))
        pannaList.add(SangamGameData("3", "346"))
        pannaList.add(SangamGameData("3", "490"))
        pannaList.add(SangamGameData("3", "580"))
        pannaList.add(SangamGameData("3", "670"))
        pannaList.add(SangamGameData("3", "689"))
        pannaList.add(SangamGameData("3", "166"))
        pannaList.add(SangamGameData("3", "229"))
        pannaList.add(SangamGameData("3", "337"))
        pannaList.add(SangamGameData("3", "355"))
        pannaList.add(SangamGameData("3", "445"))
        pannaList.add(SangamGameData("3", "599"))
        pannaList.add(SangamGameData("3", "779"))
        pannaList.add(SangamGameData("3", "788"))
        pannaList.add(SangamGameData("3", "300"))
        pannaList.add(SangamGameData("3", "111"))

        pannaList.add(SangamGameData("4", "130"))
        pannaList.add(SangamGameData("4", "149"))
        pannaList.add(SangamGameData("4", "158"))
        pannaList.add(SangamGameData("4", "167"))
        pannaList.add(SangamGameData("4", "239"))
        pannaList.add(SangamGameData("4", "248"))
        pannaList.add(SangamGameData("4", "257"))
        pannaList.add(SangamGameData("4", "347"))
        pannaList.add(SangamGameData("4", "356"))
        pannaList.add(SangamGameData("4", "590"))
        pannaList.add(SangamGameData("4", "680"))
        pannaList.add(SangamGameData("4", "789"))
        pannaList.add(SangamGameData("4", "112"))
        pannaList.add(SangamGameData("4", "220"))
        pannaList.add(SangamGameData("4", "266"))
        pannaList.add(SangamGameData("4", "338"))
        pannaList.add(SangamGameData("4", "446"))
        pannaList.add(SangamGameData("4", "455"))
        pannaList.add(SangamGameData("4", "699"))
        pannaList.add(SangamGameData("4", "770"))
        pannaList.add(SangamGameData("4", "400"))
        pannaList.add(SangamGameData("4", "888"))

        pannaList.add(SangamGameData("5", "140"))
        pannaList.add(SangamGameData("5", "159"))
        pannaList.add(SangamGameData("5", "168"))
        pannaList.add(SangamGameData("5", "230"))
        pannaList.add(SangamGameData("5", "249"))
        pannaList.add(SangamGameData("5", "258"))
        pannaList.add(SangamGameData("5", "267"))
        pannaList.add(SangamGameData("5", "348"))
        pannaList.add(SangamGameData("5", "357"))
        pannaList.add(SangamGameData("5", "456"))
        pannaList.add(SangamGameData("5", "690"))
        pannaList.add(SangamGameData("5", "780"))
        pannaList.add(SangamGameData("5", "113"))
        pannaList.add(SangamGameData("5", "122"))
        pannaList.add(SangamGameData("5", "177"))
        pannaList.add(SangamGameData("5", "339"))
        pannaList.add(SangamGameData("5", "366"))
        pannaList.add(SangamGameData("5", "447"))
        pannaList.add(SangamGameData("5", "799"))
        pannaList.add(SangamGameData("5", "889"))
        pannaList.add(SangamGameData("5", "500"))
        pannaList.add(SangamGameData("5", "555"))

        pannaList.add(SangamGameData("6", "123"))
        pannaList.add(SangamGameData("6", "150"))
        pannaList.add(SangamGameData("6", "169"))
        pannaList.add(SangamGameData("6", "178"))
        pannaList.add(SangamGameData("6", "240"))
        pannaList.add(SangamGameData("6", "259"))
        pannaList.add(SangamGameData("6", "268"))
        pannaList.add(SangamGameData("6", "349"))
        pannaList.add(SangamGameData("6", "358"))
        pannaList.add(SangamGameData("6", "367"))
        pannaList.add(SangamGameData("6", "457"))
        pannaList.add(SangamGameData("6", "790"))
        pannaList.add(SangamGameData("6", "114"))
        pannaList.add(SangamGameData("6", "277"))
        pannaList.add(SangamGameData("6", "330"))
        pannaList.add(SangamGameData("6", "448"))
        pannaList.add(SangamGameData("6", "466"))
        pannaList.add(SangamGameData("6", "556"))
        pannaList.add(SangamGameData("6", "880"))
        pannaList.add(SangamGameData("6", "899"))
        pannaList.add(SangamGameData("6", "600"))
        pannaList.add(SangamGameData("6", "222"))

        pannaList.add(SangamGameData("7", "124"))
        pannaList.add(SangamGameData("7", "160"))
        pannaList.add(SangamGameData("7", "179"))
        pannaList.add(SangamGameData("7", "250"))
        pannaList.add(SangamGameData("7", "269"))
        pannaList.add(SangamGameData("7", "278"))
        pannaList.add(SangamGameData("7", "340"))
        pannaList.add(SangamGameData("7", "359"))
        pannaList.add(SangamGameData("7", "368"))
        pannaList.add(SangamGameData("7", "458"))
        pannaList.add(SangamGameData("7", "467"))
        pannaList.add(SangamGameData("7", "890"))
        pannaList.add(SangamGameData("7", "115"))
        pannaList.add(SangamGameData("7", "133"))
        pannaList.add(SangamGameData("7", "188"))
        pannaList.add(SangamGameData("7", "223"))
        pannaList.add(SangamGameData("7", "377"))
        pannaList.add(SangamGameData("7", "449"))
        pannaList.add(SangamGameData("7", "557"))
        pannaList.add(SangamGameData("7", "566"))
        pannaList.add(SangamGameData("7", "700"))
        pannaList.add(SangamGameData("7", "999"))

        pannaList.add(SangamGameData("8", "125"))
        pannaList.add(SangamGameData("8", "134"))
        pannaList.add(SangamGameData("8", "170"))
        pannaList.add(SangamGameData("8", "189"))
        pannaList.add(SangamGameData("8", "260"))
        pannaList.add(SangamGameData("8", "279"))
        pannaList.add(SangamGameData("8", "350"))
        pannaList.add(SangamGameData("8", "369"))
        pannaList.add(SangamGameData("8", "378"))
        pannaList.add(SangamGameData("8", "459"))
        pannaList.add(SangamGameData("8", "468"))
        pannaList.add(SangamGameData("8", "567"))
        pannaList.add(SangamGameData("8", "116"))
        pannaList.add(SangamGameData("8", "224"))
        pannaList.add(SangamGameData("8", "233"))
        pannaList.add(SangamGameData("8", "288"))
        pannaList.add(SangamGameData("8", "440"))
        pannaList.add(SangamGameData("8", "477"))
        pannaList.add(SangamGameData("8", "558"))
        pannaList.add(SangamGameData("8", "990"))
        pannaList.add(SangamGameData("8", "800"))
        pannaList.add(SangamGameData("8", "666"))

        pannaList.add(SangamGameData("9", "126"))
        pannaList.add(SangamGameData("9", "135"))
        pannaList.add(SangamGameData("9", "180"))
        pannaList.add(SangamGameData("9", "234"))
        pannaList.add(SangamGameData("9", "270"))
        pannaList.add(SangamGameData("9", "289"))
        pannaList.add(SangamGameData("9", "360"))
        pannaList.add(SangamGameData("9", "379"))
        pannaList.add(SangamGameData("9", "450"))
        pannaList.add(SangamGameData("9", "469"))
        pannaList.add(SangamGameData("9", "478"))
        pannaList.add(SangamGameData("9", "568"))
        pannaList.add(SangamGameData("9", "117"))
        pannaList.add(SangamGameData("9", "144"))
        pannaList.add(SangamGameData("9", "199"))
        pannaList.add(SangamGameData("9", "225"))
        pannaList.add(SangamGameData("9", "388"))
        pannaList.add(SangamGameData("9", "559"))
        pannaList.add(SangamGameData("9", "577"))
        pannaList.add(SangamGameData("9", "667"))
        pannaList.add(SangamGameData("9", "900"))
        pannaList.add(SangamGameData("9", "333"))

        AllPannaNumber.addAll(pannaList)

        pannaAdapter = PannaAdapter(pannaList, context, this@HalfSangamActivity)
        txtEnterPanna.setAdapter(pannaAdapter)
    }

    fun makeRequest(cancelButton: AppCompatButton, submitButton: AppCompatButton,
                    wp10Progress: GifImageView, mBottomSheetDialog: BottomSheetDialog) {

        wp10Progress.visibility = View.VISIBLE

        submitButton.visibility = View.GONE
        cancelButton.visibility = View.GONE

        val UniqueSessionID = UUID.randomUUID().toString()

        val pointsParams = itemsToConvert(sangamGameList).toString()

        val call = apiInterface.bidSangamRequest(
            sessionPrefs.getString(Constants.USER_ID),
            gameTypeId!!, UniqueSessionID, FinalCurrentDate!!, catId!!, pointsParams
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
                            BottomSheetMaterialDialog.Builder(context as HalfSangamActivity)
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
                            BottomSheetMaterialDialog.Builder(context as HalfSangamActivity)
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

                wp10Progress.visibility = View.GONE

                submitButton.visibility = View.VISIBLE
                cancelButton.visibility = View.VISIBLE

            }

            override fun onFailure(call: Call<BidRequestResponse>, t: Throwable) {

                wp10Progress.visibility = View.GONE

                submitButton.visibility = View.VISIBLE
                cancelButton.visibility = View.VISIBLE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(context as HalfSangamActivity)
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
    fun itemsToConvert(list: ArrayList<SangamPointGameData>): JsonArray {

        val jsonArray = JsonArray()

        for (items in list) {

            val jsonObject = JsonObject() // /sub Object

            try {

                jsonObject.addProperty("open_panna", items.open_panna)
                jsonObject.addProperty("close_panna", items.close_panna)
                jsonObject.addProperty("open_digit", items.open_digit)
                jsonObject.addProperty("close_digit", items.close_digit)
                jsonObject.addProperty("amount", items.point)
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
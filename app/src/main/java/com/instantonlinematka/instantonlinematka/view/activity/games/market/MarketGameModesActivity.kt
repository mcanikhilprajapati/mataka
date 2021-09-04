package com.instantonlinematka.instantonlinematka.view.activity.games.market

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.GameModesAdapter
import com.instantonlinematka.instantonlinematka.model.*
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.*
import com.instantonlinematka.instantonlinematka.view.activity.NotificationActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_game_modes.*
import kotlinx.android.synthetic.main.activity_game_modes.btnNotification
import kotlinx.android.synthetic.main.activity_game_modes.circleImageView
import kotlinx.android.synthetic.main.activity_game_modes.toolbar_Wallet
import kotlinx.android.synthetic.main.activity_game_modes.toolbar_not_number
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("RestrictedApi")
class MarketGameModesActivity : AppCompatActivity() {

    lateinit var context: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var gamesList: ArrayList<GameModes>
    lateinit var categoryList: ArrayList<GameCategoryData>

    lateinit var adapter: GameModesAdapter

    var Status: String? = null;
    var GameName: String? = null;
    var GameNumbers: String? = null;
    var OpenBids: String? = null;
    var CloseBids: String? = null;
    var GameId: String? = null;
    var GameCatId: String? = null;
    var GameTypeId: String? = null
    var CurrentTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_modes)

        context = this@MarketGameModesActivity

        sessionPrefs = SessionPrefs(context)

        apiInterface = RetrofitClient.getRetrfitInstance()

        gamesList = ArrayList()
        categoryList = ArrayList()

        adapter = GameModesAdapter(context, gamesList)

        /*Status = intent.getStringExtra("status")
        GameName = intent.getStringExtra("game_name")
        GameNumbers = intent.getStringExtra("game_number")
        OpenBids = intent.getStringExtra("open_bid")
        CloseBids = intent.getStringExtra("close_bid")*/
        GameTypeId = intent.getStringExtra("gameTypeId")

        lblStatusAnswer.text = "- - -" // Status
        lblMarketNameAnswer.text = "- - -" // GameName
        lblNumbersAnswer.text = "- - -" // GameNumbers
        lblOpenTimeAnswer.text = "- - -" // ConvertTime.ConvertTimeToPM(OpenBids!!)
        lblCloseTimeAnswer.text = "- - -" //ConvertTime.ConvertTimeToPM(CloseBids!!)

        recyclerView.layoutManager = LinearLayoutManager (
            context, LinearLayoutManager.VERTICAL, false
        )

        recyclerView.adapter = adapter

        getGameCategory()
        updateWallet()

        circleImageView.setSafeOnClickListener {
            onBackPressed()
        }

        btnNotification.setSafeOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }

        val name = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)

//        if (name.isEmpty()) {
//            toolbar_title1.visibility = View.GONE
//        } else {
//            toolbar_title1.text = name
//            toolbar_title1.visibility = View.VISIBLE
//        }
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

    fun getGameCategory() {

        if (Connectivity.isOnline(context)) {
            makeGameCategoryApiCall()
        } else {
            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@MarketGameModesActivity)
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

    fun makeGameCategoryApiCall() {

        wp10progressBar.showProgressBar()

        recyclerView.visibility = View.GONE

        val call = apiInterface.getGameCategory(
            sessionPrefs.getString(Constants.USER_ID), GameTypeId!!
        )

        call.enqueue(object : Callback<GameCategoryResponse> {
            override fun onResponse(
                call: Call<GameCategoryResponse>,
                response: Response<GameCategoryResponse>
            ) {
                categoryList.clear()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val categoryData = responseData.data

                    for (items in categoryData) {

                        val id = items.id
                        val catId = items.category_id
                        val gameId = items.game_id
                        val catName = items.category_name

                        val data = GameCategoryData(id, gameId, catId, catName)

                        categoryList.add(data)
                    }
                } else {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@MarketGameModesActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }

                getPreviousGameInfo()
            }

            override fun onFailure(call: Call<GameCategoryResponse>, t: Throwable) {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@MarketGameModesActivity)
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


    fun getPreviousGameInfo() {

        if (Connectivity.isOnline(context)) {
            makePreviousGameApiCall()
        } else {
            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@MarketGameModesActivity)
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

    fun makePreviousGameApiCall() {

        val call = apiInterface.getPreviousGameInfo(
            sessionPrefs.getString(Constants.USER_ID), GameTypeId!!
        )

        call.enqueue(object : Callback<PreviousGameInfoResponse> {
            override fun onResponse(
                call: Call<PreviousGameInfoResponse>,
                response: Response<PreviousGameInfoResponse>
            ) {

                val responseData = response.body()!!

                Status = responseData.data.game_status ?: "- - -"
                GameName = responseData.data.game_type_name ?: "- - -"
                GameNumbers = responseData.data.oldgameresult ?: "- - -"
                OpenBids = responseData.data.open_time
                CloseBids = responseData.data.close_time
                CurrentTime = responseData.data.time ?: "- - -"

                toolbar_title1.text = GameName
                toolbar_title1.visibility = View.VISIBLE

                if (Status!!.contentEquals("0")) {
                    lblStatusAnswer.text = context.getString(R.string.bidding_is_closed_for_today)
                }
                else if (Status!!.contentEquals("1")) {
                    lblStatusAnswer.text = context.getString(R.string.bidding_is_running_for_today)
                }
                else if (Status!!.contentEquals("6")) {
                    lblStatusAnswer.text = context.getString(R.string.bidding_is_running_for_close_bids)
                }
                else {
                    lblStatusAnswer.text = Status!!
                }

                lblMarketNameAnswer.text = GameName
                lblNumbersAnswer.text = GameNumbers
                lblOpenTimeAnswer.text = ConvertTime.ConvertTimeToPM(OpenBids!!)
                lblCloseTimeAnswer.text = ConvertTime.ConvertTimeToPM(CloseBids!!)

                var CurrentDateObject = Date()
                var ExpiryDateObject = Date()

                try {
                    val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                    try {
                        CurrentDateObject = sdf.parse(CurrentTime!!)!!
                        ExpiryDateObject = sdf.parse(OpenBids!!)!!
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                gamesList.clear()

                if (CurrentDateObject.after(ExpiryDateObject)) {

                    // Exipired
                    for (items in categoryList) {

                        if (items.category_name!!.contentEquals(getString(R.string.single_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice),
                                    getString(R.string.single_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    true, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.jodi_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice_jodi),
                                    getString(R.string.jodi_cap),
                                    ContextCompat.getColor(context, R.color.LightGrey),
                                    true, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.single_panna_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice_single_panna),
                                    getString(R.string.single_panna_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    true, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.double_panna_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice_double_panna),
                                    getString(R.string.double_panna_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    true, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.triple_panna_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice_panna_multiple),
                                    getString(R.string.triple_panna_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    true, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.half_sangam_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.dice_spade_half
                                    ),
                                    getString(R.string.half_sangam_cap),
                                    ContextCompat.getColor(context, R.color.LightGrey),
                                    true, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.full_sangam_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.dice_spade
                                    ),
                                    getString(R.string.full_sangam_cap),
                                    ContextCompat.getColor(context, R.color.LightGrey),
                                    true, items.category_id, GameTypeId
                                )
                            )
                        }
                    }

                } else {
                    // Available
                    for (items in categoryList) {

                        if (items.category_name!!.contentEquals(getString(R.string.single_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice),
                                    getString(R.string.single_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    false, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.jodi_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice_jodi),
                                    getString(R.string.jodi_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    false, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.single_panna_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice_single_panna),
                                    getString(R.string.single_panna_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    false, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.double_panna_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice_double_panna),
                                    getString(R.string.double_panna_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    false, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.triple_panna_cap))) {

                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(context, R.drawable.dice_panna_multiple),
                                    getString(R.string.triple_panna_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    false, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.half_sangam_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.dice_spade_half
                                    ),
                                    getString(R.string.half_sangam_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    false, items.category_id, GameTypeId
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.full_sangam_cap))) {
                            gamesList.add(
                                GameModes(
                                    items.game_id,
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.dice_spade
                                    ),
                                    getString(R.string.full_sangam_cap),
                                    ContextCompat.getColor(context, R.color.white),
                                    false, items.category_id, GameTypeId
                                )
                            )
                        }

                    }

                }

                adapter.notifyDataSetChanged()

                wp10progressBar.hideProgressBar()

                recyclerView.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<PreviousGameInfoResponse>, t: Throwable) {}
        })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {

            lblStatusAnswer.text = "- - -" // Status
            lblMarketNameAnswer.text = "- - -" // GameName
            lblNumbersAnswer.text = "- - -" // GameNumbers
            lblOpenTimeAnswer.text = "- - -" // ConvertTime.ConvertTimeToPM(OpenBids!!)
            lblCloseTimeAnswer.text = "- - -" //ConvertTime.ConvertTimeToPM(CloseBids!!)

            getGameCategory()
            updateWallet()
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
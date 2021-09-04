package com.instantonlinematka.instantonlinematka.view.activity.games.ratan

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.RatanGameModesAdapter
import com.instantonlinematka.instantonlinematka.model.GameModes
import com.instantonlinematka.instantonlinematka.model.RatanGameCategoryData
import com.instantonlinematka.instantonlinematka.model.RatanGameCategoryResponse
import com.instantonlinematka.instantonlinematka.model.WalletBalanceResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.*
import com.instantonlinematka.instantonlinematka.view.activity.NotificationActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_ratan_game_modes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class RatanGameModesActivity : AppCompatActivity() {

    lateinit var context: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var gamesList: ArrayList<GameModes>
    lateinit var categoryList: ArrayList<RatanGameCategoryData>

    lateinit var adapter: RatanGameModesAdapter

    var Status: String? = null;
    var GameName: String? = null;
    var GameNumbers: String? = null;
    var OpenBids: String? = null;
    var GameId: String? = null;
    var GameTypeId: String? = null
    var GameDate: String? = null
    var GameTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ratan_game_modes)

        context = this@RatanGameModesActivity

        sessionPrefs = SessionPrefs(context)

        apiInterface = RetrofitClient.getRetrfitInstance()

        gamesList = ArrayList()
        categoryList = ArrayList()

        adapter = RatanGameModesAdapter(context, gamesList)

        Status = intent.getStringExtra("status")
        GameId = intent.getStringExtra("gameId")
        GameDate = intent.getStringExtra("gameDate")
        GameTime = intent.getStringExtra("gameTime")
        GameNumbers = intent.getStringExtra("gameNumbers")

        lblStatusAnswer.text = Status
        lblNumbersAnswer.text = GameNumbers
        lblOpenTimeAnswer.text = GameDate
        lblCloseTimeAnswer.text = ConvertTime.ConvertTimeToPM(GameTime!!)

        recyclerView.layoutManager = GridLayoutManager(
            context, 1,
            RecyclerView.VERTICAL, false
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
                BottomSheetMaterialDialog.Builder(this@RatanGameModesActivity)
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

        val call = apiInterface.getRatanGameCategory(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object : Callback<RatanGameCategoryResponse> {
            override fun onResponse(
                call: Call<RatanGameCategoryResponse>,
                response: Response<RatanGameCategoryResponse>
            ) {

                categoryList.clear()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val categoryData = responseData.data

                    for (items in categoryData) {

                        val catId = items.cat_id
                        val catName = items.category_name

                        val data = RatanGameCategoryData(catId, catName)

                        categoryList.add(data)
                    }

                    gamesList.clear()

                    // Available
                    for (items in categoryList) {

                        if (items.category_name!!.contentEquals(getString(R.string.single_cap))) {
                            gamesList.add(
                                GameModes(
                                    GameId,
                                    ContextCompat.getDrawable(context, R.drawable.dice),
                                    getString(R.string.single_cap),
                                    ContextCompat.getColor(context, R.color.White),
                                    false, items.cat_id, "",
                                    lblStatusAnswer.text.toString().trim(),
                                    lblOpenTimeAnswer.text.toString().trim(),
                                    lblCloseTimeAnswer.text.toString().trim(),
                                    lblNumbersAnswer.text.toString().trim()
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.single_panna_cap))) {
                            gamesList.add(
                                GameModes(
                                    GameId,
                                    ContextCompat.getDrawable(context, R.drawable.dice_single_panna),
                                    getString(R.string.single_panna_cap),
                                    ContextCompat.getColor(context, R.color.White),
                                    false, items.cat_id, "",
                                    lblStatusAnswer.text.toString().trim(),
                                    lblOpenTimeAnswer.text.toString().trim(),
                                    lblCloseTimeAnswer.text.toString().trim(),
                                    lblNumbersAnswer.text.toString().trim()
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.double_panna_cap))) {
                            gamesList.add(
                                GameModes(
                                    GameId,
                                    ContextCompat.getDrawable(context, R.drawable.dice_double_panna),
                                    getString(R.string.double_panna_cap),
                                    ContextCompat.getColor(context, R.color.White),
                                    false, items.cat_id, "",
                                    lblStatusAnswer.text.toString().trim(),
                                    lblOpenTimeAnswer.text.toString().trim(),
                                    lblCloseTimeAnswer.text.toString().trim(),
                                    lblNumbersAnswer.text.toString().trim()
                                )
                            )
                        } else if (items.category_name.contentEquals(getString(R.string.triple_panna_cap))) {
                            gamesList.add(
                                GameModes(
                                    GameId,
                                    ContextCompat.getDrawable(context, R.drawable.dice_panna_multiple),
                                    getString(R.string.triple_panna_cap),
                                    ContextCompat.getColor(context, R.color.White),
                                    false, items.cat_id, "",
                                    lblStatusAnswer.text.toString().trim(),
                                    lblOpenTimeAnswer.text.toString().trim(),
                                    lblCloseTimeAnswer.text.toString().trim(),
                                    lblNumbersAnswer.text.toString().trim()
                                )
                            )
                        }
                    }

                    adapter.notifyDataSetChanged()

                    wp10progressBar.hideProgressBar()

                    recyclerView.visibility = View.VISIBLE

                } else {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@RatanGameModesActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }
            }

            override fun onFailure(call: Call<RatanGameCategoryResponse>, t: Throwable) {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@RatanGameModesActivity)
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
        if (requestCode == 2) {

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
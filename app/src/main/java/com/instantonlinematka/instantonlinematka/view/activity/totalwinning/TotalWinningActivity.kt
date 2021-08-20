package com.instantonlinematka.instantonlinematka.view.activity.totalwinning

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.GraphData
import com.instantonlinematka.instantonlinematka.model.TotalWinningAmountResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_total_winning.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TotalWinningActivity : AppCompatActivity() {

    lateinit var contextWinning: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var winningList: ArrayList<GraphData>

    var barDataSet: BarDataSet? = null
    lateinit var barEntryArrayList: ArrayList<BarEntry>
    lateinit var labels: ArrayList<String>
    lateinit var barData: BarData
    lateinit var chartColors: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_winning)

        /*try {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController!!.hide(WindowInsets.Type.statusBars())
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
            setContentView(R.layout.activity_total_winning)
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        contextWinning = this@TotalWinningActivity

        sessionPrefs = SessionPrefs(contextWinning)

        apiInterface = RetrofitClient.getRetrfitInstance()

        winningList = ArrayList()

        barEntryArrayList = ArrayList()
        labels = ArrayList()
        chartColors = ArrayList<Int>()
        barData = BarData()

        lblType.text = getString(R.string.last_seven_days)
        getWinning("7")

        btnRetry.setSafeOnClickListener {
            getWinning("7")
        }

        btnChooseType.setSafeOnClickListener {

            val typeDataArray = arrayOf(
                contextWinning.getString(R.string.last_seven_days),
                contextWinning.getString(R.string.last_month_winning),
                contextWinning.getString(R.string.last_three_month_winning),
                contextWinning.getString(R.string.last_six_month_winning)
            )
            val builder = AlertDialog.Builder(contextWinning)
            builder.setTitle(getString(R.string.make_your_selection))
            builder.setItems(
                typeDataArray
            ) { dialog, which ->

                barEntryArrayList.clear()
                labels.clear()

                lblType.setText(
                    typeDataArray.get(which)
                )

                when (typeDataArray.get(which)) {

                    contextWinning.getString(R.string.last_seven_days) -> {
                        lblType.text = getString(R.string.last_seven_days)
                        getWinning("7")
                    }

                    contextWinning.getString(R.string.last_month_winning) -> {
                        lblType.text = getString(R.string.last_month_winning)
                        getWinning("1")
                    }

                    contextWinning.getString(R.string.last_three_month_winning) -> {
                        lblType.text = getString(R.string.last_three_month_winning)
                        getWinning("3")
                    }

                    contextWinning.getString(R.string.last_six_month_winning) -> {
                        lblType.text = getString(R.string.last_six_month_winning)
                        getWinning("6")
                    }

                }

            }
            val alert = builder.create()
            alert.show()
        }
    }

    @SuppressLint("RestrictedApi")
    fun getWinning(type: String) {

        if (Connectivity.isOnline(contextWinning)) {
            makeWinningApiCall(type)
        } else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@TotalWinningActivity)
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

    fun makeWinningApiCall(type: String) {

        wp10progressBar.showProgressBar()

        linearHome.visibility = View.GONE
        constraint.visibility = View.GONE

        val call = apiInterface.getWinningStats(
            sessionPrefs.getString(Constants.USER_ID), type
        )

        call.enqueue(object : Callback<TotalWinningAmountResponse> {

            override fun onResponse(
                call: Call<TotalWinningAmountResponse>,
                response: Response<TotalWinningAmountResponse>
            ) {

                winningList.clear()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    linearHome.visibility = View.GONE
                    constraint.visibility = View.VISIBLE

                    lblTotalWinning.text = "Total Winning Amount: ₹ ${responseData.data.total}"

                    val GraphResponse = responseData.data.graph

                    for (items in GraphResponse) {

                        val total = items.total ?: 0
                        val date = items.date ?: ""

                        val data = GraphData(total, date)

                        winningList.add(data)
                    }

                    populateGraph(winningList)

                } else {

                    linearHome.visibility = View.VISIBLE
                    constraint.visibility = View.GONE
                }

                wp10progressBar.hideProgressBar()
            }

            @SuppressLint("RestrictedApi")
            override fun onFailure(call: Call<TotalWinningAmountResponse>, t: Throwable) {

                wp10progressBar.hideProgressBar()

                linearHome.visibility = View.VISIBLE
                constraint.visibility = View.GONE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@TotalWinningActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setMessage(t.message!!)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }

        })
    }

    fun populateGraph(winningList: ArrayList<GraphData>) {

        barDataSet = BarDataSet(barEntryArrayList, getString(R.string.winning_amount_label))

        barChart.animateY(1000);
        barChart.setDescription("")

        if (lblType.text.toString()
                .contentEquals(contextWinning.getString(R.string.last_seven_days))) {
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_blue))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_orange))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_green))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_red))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.DarkRed))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.BlueViolet))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.LightSkyBlue))
        }
        else if (
            lblType.text.toString()
                .contentEquals(contextWinning.getString(R.string.last_month_winning))) {
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_blue))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_orange))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_green))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_red))
        }
        else if (
            lblType.text.toString()
                .contentEquals(contextWinning.getString(R.string.last_three_month_winning))) {
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_blue))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_orange))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_green))
        }
        else if (
            lblType.text.toString()
                .contentEquals(contextWinning.getString(R.string.last_six_month_winning))) {
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_blue))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_orange))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_green))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.view_details_red))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.DarkRed))
            chartColors.add(ContextCompat.getColor(contextWinning, R.color.BlueViolet))
        }

        for (i in 0 until winningList.size) {
            barEntryArrayList.add(BarEntry(winningList.get(i).total!!.toFloat(), i))
            labels.add(i, winningList.get(i).date!!);
        }

        barDataSet!!.setColors(chartColors);

        barData = BarData(labels, barDataSet);

        barChart.setData(barData);

    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}
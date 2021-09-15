package com.instantonlinematka.instantonlinematka.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.adapter.NotificationAdapter
import com.instantonlinematka.instantonlinematka.model.NotificationData
import com.instantonlinematka.instantonlinematka.model.NotificationResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_notification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class NotificationActivity : AppCompatActivity() {

    lateinit var context: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var notificationList: ArrayList<NotificationData>

    lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        context = this@NotificationActivity

        sessionPrefs = SessionPrefs(context)

        apiInterface = RetrofitClient.getRetrfitInstance()

        notificationList = ArrayList()

        adapter = NotificationAdapter(context, notificationList)

        recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        recyclerView.adapter = adapter

        getNotification()

        btnRetry.setSafeOnClickListener {
            getNotification()
        }
    }

    fun getNotification() {

        if (Connectivity.isOnline(context)) {
            makeNotificationApiCall()
        } else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@NotificationActivity)
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

    fun makeNotificationApiCall() {

        wp10progressBar.visibility = View.VISIBLE

        linearHome.visibility = View.GONE
        recyclerView.visibility = View.GONE

        val call = apiInterface.getNotification(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {

                notificationList.clear()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    linearHome.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE

                    val NotificationResponse = responseData.data

                    for (items in NotificationResponse) {

                        val nid = items.nid
                        val user_id= items.user_id
                        val title= items.title
                        val created_date= items.created_date
                        val type= items.type
                        val amount= items.amount
                        val msg= items.msg

                        val data = NotificationData(nid, user_id, title, created_date, type,
                        amount, msg)

                        notificationList.add(data)
                    }

                    adapter.notifyDataSetChanged()

                    sessionPrefs.addString(Constants.NOTIFICATION_COUNT, "0")

                } else {

                    linearHome.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE

                }

                wp10progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {

                wp10progressBar.visibility = View.GONE

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@NotificationActivity)
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

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}
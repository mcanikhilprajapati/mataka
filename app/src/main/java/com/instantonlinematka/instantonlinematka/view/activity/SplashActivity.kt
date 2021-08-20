package com.instantonlinematka.instantonlinematka.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.TimeData
import com.instantonlinematka.instantonlinematka.model.UserInfoResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("RestrictedApi")
class SplashActivity : AppCompatActivity() {

    lateinit var context: Context

    val MY_PERMISSIONS_REQUEST_CODE = 123

    lateinit var apiInterface: ApiInterface

    lateinit var sessionPrefs: SessionPrefs

    lateinit var CurrentDate: String
    lateinit var ExpiryDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash)

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

        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        context = this@SplashActivity

        getWindow().setStatusBarColor(
            ContextCompat.getColor(context ,R.color.colorPrimary))
        sessionPrefs = SessionPrefs(context)

        if (Connectivity.isOnline(context)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkPermissions()) {
                    ProceedifPermitted()
                } else requestPermission()

            } else {
                ProceedifPermitted()
            }
        } else {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this)
                    .setTitle(getString(R.string.uhoh))
                    .setMessage(getString(R.string.no_internet_found))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        finish()
                    }
                    .build()

            mBottomSheetDialog.show()
        }

    }

    fun checkPermissions(): Boolean {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false

    }

    fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            MY_PERMISSIONS_REQUEST_CODE
        )
    }

    @SuppressLint("RestrictedApi")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED))
            ) {

                ProceedifPermitted()

            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this)
                        .setTitle(getString(R.string.please_confirm))
                        .setMessage(getString(R.string.please_grant_permission))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.grant)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            requestPermission()
                        }
                        .build()

                mBottomSheetDialog.show()

            }
        }
    }

    fun ProceedifPermitted() {

        wp7progressBar.showProgressBar()

        //getInternetTime()
        // After Removing Internet cut from here
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000L)

            if (sessionPrefs.getString(Constants.FCM_KEY).isEmpty()) {

                var firebaseToken = ""
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            return@addOnCompleteListener
                        }
                        firebaseToken = it.result!!.token
                        sessionPrefs.addString(Constants.FCM_KEY, firebaseToken)

                        wp7progressBar.hideProgressBar()

                        val intent = Intent(context, WelcomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
            } else {

                if (sessionPrefs.getString(Constants.USER_ID).isEmpty()) {

                    wp7progressBar.hideProgressBar()

                    val intent = Intent(context, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    getUserInfo()
                }
            }

        }

    }

    fun getInternetTime() {

        if (Connectivity.isOnline(context)) {
            makeInternetTimeApiCall()
        } else {
            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this@SplashActivity)
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

    fun makeInternetTimeApiCall() {

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://worldtimeapi.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api: ApiInterface = retrofit.create(ApiInterface::class.java)

        val call = api.getInternetTime()

        call.enqueue(object : Callback<TimeData> {
            override fun onResponse(call: Call<TimeData>, response: Response<TimeData>) {

                val data = response.body()!!

                val unixTime = data.unixtime

                val date = Date(unixTime!!.toInt() * 1000L)

                val finalDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

                CurrentDate = finalDate.format(date)

                ExpiryDate = "2020-10-22"

                val expiredDate = finalDate.parse(ExpiryDate)

                if (date.after(expiredDate)) {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@SplashActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage("App Has Expired. Contact The Developer")
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                wp7progressBar.hideProgressBar()
                            }
                            .build()

                    mBottomSheetDialog.show()

                } else {

                    // After Removing Internet cut from here
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000L)

                        if (sessionPrefs.getString(Constants.FCM_KEY).isEmpty()) {

                            var firebaseToken = ""
                            FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener {
                                    if (!it.isSuccessful) {
                                        return@addOnCompleteListener
                                    }
                                    firebaseToken = it.result!!.token
                                    sessionPrefs.addString(Constants.FCM_KEY, firebaseToken)

                                    wp7progressBar.hideProgressBar()

                                    val intent = Intent(context, WelcomeActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                }
                        } else {

                            if (sessionPrefs.getString(Constants.USER_ID).isEmpty()) {

                                wp7progressBar.hideProgressBar()

                                val intent = Intent(context, WelcomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                getUserInfo()
                            }
                        }

                    }
                }
            }

            override fun onFailure(call: Call<TimeData>, t: Throwable) {}
        })
    }

    fun getUserInfo() {

        if (Connectivity.isOnline(context)) {
            makeUserInfoApiCall()
        } else {
            if (wp7progressBar.isShown) {
                wp7progressBar.hideProgressBar()
            }

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(this)
                    .setTitle(getString(R.string.uhoh))
                    .setMessage(getString(R.string.no_internet_found))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        getUserInfo()
                    }
                    .build()

            mBottomSheetDialog.show()
        }
    }

    fun makeUserInfoApiCall() {

        apiInterface = RetrofitClient.getRetrfitInstance()

        val call = apiInterface.getUserInfo(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object : Callback<UserInfoResponse> {

            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {

                wp7progressBar.hideProgressBar()

                val responseData = response.body()!!

                val isResponse = responseData.response

                if (isResponse) {

                    val values = responseData.user

                    val id = values.id ?: "- - -"
                    val name = values.name ?: "- - -"
                    val phone = values.phone ?: "- - -"
                    val email = values.email ?: "- - -"
                    val gender = values.gender ?: "- - -"
                    val dob = values.dob ?: "- - -"
                    val address = values.address ?: "- - -"
                    val referal = values.referal ?: "- - -"
                    val wallet = values.wallet ?: "- - -"
                    val bank_name = values.bank_name ?: "- - -"
                    val bankholder_name = values.bankholder_name ?: "- - -"
                    val account_no = values.account_no ?: "- - -"
                    val ifc_code = values.Ifc_code ?: "- - -"
                    val profile_pic = values.profile_pic ?: "- - -"
                    val gpay = values.google_pay ?: ""
                    val phonepe = values.phone_pay ?: ""
                    val paytm = values.paytm ?: ""
                    val notification_count = values.notification_count ?: ""

                    sessionPrefs.addString(Constants.USER_ID, id)
                    sessionPrefs.addString(Constants.USER_NAME, name)
                    sessionPrefs.addString(Constants.USER_PHONE, phone)
                    sessionPrefs.addString(Constants.USER_EMAIL, email)
                    sessionPrefs.addString(Constants.USER_GENDER, gender)
                    sessionPrefs.addString(Constants.USER_DOB, dob)
                    sessionPrefs.addString(Constants.USER_ADDRESS, address)
                    sessionPrefs.addString(Constants.REFERRAL, referal)
                    sessionPrefs.addString(Constants.WALLET, wallet)
                    sessionPrefs.addString(Constants.BANK_NAME, bank_name)
                    sessionPrefs.addString(Constants.BANK_HOLDER_NAME, bankholder_name)
                    sessionPrefs.addString(Constants.BANK_ACCOUNT_NUMBER, account_no)
                    sessionPrefs.addString(Constants.BANK_IFSC_CODE, ifc_code)
                    sessionPrefs.addString(Constants.USER_PROF_PIC, profile_pic)
                    sessionPrefs.addString(Constants.NOTIFICATION_COUNT, notification_count)
                    sessionPrefs.addString(Constants.GPAY, gpay)
                    sessionPrefs.addString(Constants.PHONEPE, phonepe)
                    sessionPrefs.addString(Constants.PAYTM, paytm)

                    val intent = Intent(context, DrawerActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    wp7progressBar.hideProgressBar()

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(this@SplashActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                                getUserInfo()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }

            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {

                wp7progressBar.hideProgressBar()

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(this@SplashActivity)
                        .setTitle(getString(R.string.uhoh))
                        .setMessage(getString(R.string.something_went_wrong))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.retry)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                            getUserInfo()
                        }
                        .build()

                mBottomSheetDialog.show()
            }

        })

    }
}
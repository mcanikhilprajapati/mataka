package com.instantonlinematka.instantonlinematka.view.fragment.drawer.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instantonlinematka.instantonlinematka.BuildConfig
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.HomeFragmentBinding
import com.instantonlinematka.instantonlinematka.model.SliderData
import com.instantonlinematka.instantonlinematka.model.SliderResponse
import com.instantonlinematka.instantonlinematka.model.WalletBalanceResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.market.MarketFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan.RatanStarlineGamesFragment
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.romainpiel.shimmer.Shimmer
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("RestrictedApi")
class HomeFragment: Fragment() {

    lateinit var binding: HomeFragmentBinding

    lateinit var contextHome: Context

    lateinit var shimmer: Shimmer

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    lateinit var sliderList: ArrayList<SliderData>

    lateinit var list: MutableList<CarouselItem>

    var mBottomSheetDialog: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = HomeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextHome = inflater.context

        shimmer = Shimmer()

        apiInterface = RetrofitClient.getRetrfitInstance()

        sliderList = ArrayList()

        sessionPrefs = SessionPrefs(contextHome)

        list = mutableListOf()

        getSliderImages()

        updateWallet()

        val adapter = FragmentPagerItemAdapter(
            this.childFragmentManager, FragmentPagerItems.with(activity)
                .add("", MarketFragment::class.java)
//                .add(R.string.ratan_starline_games, RatanStarlineGamesFragment::class.java)
                .create()
        )

        val viewPager = view.findViewById(R.id.viewpager) as ViewPager
        viewPager.adapter = adapter

        val viewPagerTab = view.findViewById(R.id.viewpagertab) as SmartTabLayout
        viewPagerTab.setViewPager(viewPager)

        return view
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        CoroutineScope(Main).launch {
            delay(10)
            if (menuVisible) {
                getSliderImages()
            }
        }
    }

    fun getSliderImages() {

        if (Connectivity.isOnline(contextHome)) {

            val call = apiInterface.getSliderImages(
                sessionPrefs.getString(Constants.USER_ID)
            )

            call.enqueue(object : Callback<SliderResponse> {
                @RequiresApi(Build.VERSION_CODES.R)
                override fun onResponse(
                    call: Call<SliderResponse>,
                    response: Response<SliderResponse>
                ) {

                    sliderList.clear()

                    val responseData = response.body()!!

                    val versionCode = responseData.version_code.toInt()

                    var appVersionCode = -1
                    try {
                        val packageInfo = (activity as DrawerActivity)
                            .getPackageManager()
                            .getPackageInfo(activity!!.packageName, 0)
                        appVersionCode = packageInfo.versionCode

                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }

                    // Showing Update Dialog
                    if (versionCode > appVersionCode) {
                        showUpdateDialog()
                    }

                    for (items in responseData.data) {

                        val sliderId = items.slider_id
                        val sliderImage = Constants.IMAGE_SLIDER_BASE_URL + items.slider_image
                        val sliderName = items.slider_name
                        val status = items.status

                        val BannerData = SliderData(sliderId, sliderName, sliderImage, status)

                        sliderList.add(BannerData)

                        list.add(CarouselItem(sliderImage))
                    }

//                    binding.carousel.addData(list)

                }

                override fun onFailure(call: Call<SliderResponse>, t: Throwable) {}
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun showUpdateDialog() {

        mBottomSheetDialog = BottomSheetDialog(
            activity as DrawerActivity
        )
        val sheetView: View =
            (activity as DrawerActivity).layoutInflater.inflate(
                R.layout.update_available_dialog,
                null
            )
        mBottomSheetDialog!!.setContentView(sheetView)
        mBottomSheetDialog!!.show()
        mBottomSheetDialog!!.setCancelable(false)

        val btnNoThanks: AppCompatButton = sheetView.findViewById(R.id.btnNoThanks)
        val btnUpdates: AppCompatButton = sheetView.findViewById(R.id.btnUpdate)

        btnNoThanks.setSafeOnClickListener { mBottomSheetDialog!!.dismiss() }

        btnUpdates.setSafeOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    val task = DownloadTask(contextHome)
                    task.execute("https://www.instantonlinematka.com/instantonlinematka.apk");
                    mBottomSheetDialog!!.dismiss()
                } else {
                    val permissionIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivityForResult(permissionIntent, 100)
                }
            }
            else {
                val task = DownloadTask(contextHome)
                task.execute("https://www.instantonlinematka.com/instantonlinematka.apk");
                mBottomSheetDialog!!.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = DownloadTask(contextHome)
            task.execute("https://www.instantonlinematka.com/instantonlinematka.apk");
            mBottomSheetDialog!!.dismiss()
        }
    }

    companion object {

        class DownloadTask(val context: Context) : AsyncTask<String, Int, String?>() {

            var mProgressDialog: ProgressDialog? = null

            override fun onPreExecute() {

                // Create progress dialog
                mProgressDialog = ProgressDialog(context)
                // Set your progress dialog Title
                mProgressDialog!!.setTitle("Downloading Update")
                // Set your progress dialog Message
                mProgressDialog!!.isIndeterminate = false
                mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                mProgressDialog!!.setCancelable(false)
                // Show progress dialog
                mProgressDialog!!.show()
            }

            override fun doInBackground(vararg params: String?): String {

                try {
                    val url = URL(params.get(0))
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.doOutput = false
                    connection.connect()

                    // Detect the file lenghth
                    val fileLength = connection.contentLength
                    val PATH = Environment.getExternalStorageDirectory().toString()
                    val file = File(PATH, "/Matka/Download")
                    if (!file.exists()) {
                        try {
                            val a = file.mkdirs()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        var b: String
                    }
                    val DirPath = file.absolutePath
                    val outputFile = File(file, "update.apk")
                    if (outputFile.exists()) {
                        outputFile.delete()
                        outputFile.createNewFile()
                    } else {
                        outputFile.createNewFile()
                    }
                    val fos = FileOutputStream(outputFile)
                    val iss = connection.inputStream
                    var total: Long = 0
                    val buffer = ByteArray(1024)
                    var len1 = 0
                    while (iss.read(buffer).also { len1 = it } != -1) {
                        total += len1.toLong()
                        publishProgress((total * 100 / fileLength).toInt())
                        fos.write(buffer, 0, len1)
                    }
                    fos.close()
                    iss.close()

                } catch (e: Exception) {
                    mProgressDialog!!.dismiss()
                }

                return "Finish"

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onPostExecute(result: String?) {

                mProgressDialog!!.dismiss();

                val toInstall = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + "/Matka/Download/update.apk"
                )
                val intent: Intent
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val apkUri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        toInstall
                    )
                    intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                    intent.data = apkUri
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    val apkUri = Uri.fromFile(toInstall)
                    intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }

            override fun onProgressUpdate(vararg progress: Int?) {
                // Update the progress dialog
                mProgressDialog!!.progress = progress[0]!!
            }
        }
    }

    // Wallet Amount Updater
    fun updateWallet() {

        if (Connectivity.isOnline(contextHome)) {

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
                        try {
                            val wallet = sessionPrefs.getString(Constants.WALLET)
                            if (wallet.isEmpty()) {
                                (activity as DrawerActivity).toolbar_Wallet.text = "- - -"
                            } else {
                                ((activity as DrawerActivity).toolbar_Wallet.setText(data.user.wallet))
                            }
                        } catch (e: Exception) {
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
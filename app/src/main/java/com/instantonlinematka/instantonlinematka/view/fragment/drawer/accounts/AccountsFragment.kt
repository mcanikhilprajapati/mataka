package com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.AccountsFragmentBinding
import com.instantonlinematka.instantonlinematka.model.UserInfoResponse
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.activity.totalwinning.TotalWinningActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.account_statement.AccountStatementsFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.bankdetails.BankDetailsFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.bonus.BonusFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds.AddFundsActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds.WithdrawFundsFragment
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.alert_success.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

@SuppressLint("RestrictedApi")
class AccountsFragment : Fragment() {

    lateinit var binding: AccountsFragmentBinding

    lateinit var contextAccount: Context

    lateinit var sessionPrefs: SessionPrefs

    lateinit var apiInterface: ApiInterface

    val MY_PERMISSIONS_REQUEST_CODE = 123

    private val GALLERY = 1
    private val CAMERA = 2

    private val IMAGE_DIRECTORY = "/demoimage"

    private lateinit var ProfileImageFilePath: String
    private lateinit var ProfilePicFile: File

    private lateinit var pictureDialog: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = AccountsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextAccount = inflater.context

        sessionPrefs = SessionPrefs(contextAccount)

        apiInterface = RetrofitClient.getRetrfitInstance()

        getUserInfo()

        binding.fab.setSafeOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermissions()) {
                    ProceedifPermitted()
                }
                else {
                    requestPermission()
                }
            } else {
                ProceedifPermitted()
            }
        }

        binding.btnAddFunds.setSafeOnClickListener {

            val intent = Intent(contextAccount, AddFundsActivity::class.java)
            startActivity(intent)
        }

        binding.btnWithdrawFunds.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, WithdrawFundsFragment(),
                "WITHDRAW_FUNDS_FRAGMENT", bundle
            )
        }

//        binding.btnShowBonuses.setSafeOnClickListener {
//
//            val bundle = Bundle()
//            Connectivity.switchDrawer(
//                activity as DrawerActivity, BonusFragment(),
//                "BONUS_FRAGMENT", bundle
//            )
//        }

        binding.btnShowAccountStatements.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, AccountStatementsFragment(),
                "ACCOUNT_STATEMENT_FRAGMENT", bundle
            )
        }

//        binding.btnShowWinningGraph.setSafeOnClickListener {
//
//            val intent = Intent(contextAccount, TotalWinningActivity::class.java)
//            startActivity(intent)
//        }

        binding.btnShowBankDetails.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, BankDetailsFragment(),
                "BANK_DETAILS_FRAGMENT", bundle
            )
        }

        return view
    }

    fun getUserInfo() {

        if (Connectivity.isOnline(contextAccount)) {
            makeUserInfoApiCall()
        }
        else {

            if (binding.wp10progressBar.isShown) {
                binding.wp10progressBar.hideProgressBar()
            }

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
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

    fun makeUserInfoApiCall() {

        binding.wp10progressBar.showProgressBar()

        apiInterface = RetrofitClient.getRetrfitInstance()

        val call = apiInterface.getUserInfo(
            sessionPrefs.getString(Constants.USER_ID)
        )

        call.enqueue(object : Callback<UserInfoResponse> {

            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {

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

                    try {
                        if (wallet.isEmpty()) {
                            ((activity as DrawerActivity).toolbar_Wallet).text = "- - -"
                        } else {
                            ((activity as DrawerActivity).toolbar_Wallet.setText(wallet))
                        }
                    } catch (e: Exception) {
                    }

                    Glide.with(contextAccount)
                        .load(Constants.IMAGE_BASE_URL + sessionPrefs.getString(Constants.USER_PROF_PIC))
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.ic_loading_image)
                        .error(R.drawable.no_image_found)
                        .into(binding.profileImage)

                    if (bankholder_name.isEmpty()) binding.lblProfileName.text = "- - -"
                    else binding.lblProfileName.text = bankholder_name

                    binding.lblProfilePhone.text = "+91 - $phone"

                } else {

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()
                }

                binding.wp10progressBar.hideProgressBar()

            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {

                binding.wp10progressBar.hideProgressBar()

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
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

    fun checkPermissions() : Boolean {

        if (ActivityCompat.checkSelfPermission(
                contextAccount,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                contextAccount,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false

    }

    fun requestPermission() {

        ActivityCompat.requestPermissions(
            activity as DrawerActivity,
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
                                grantResults[1] == PackageManager.PERMISSION_GRANTED))) {

                ProceedifPermitted()

            } else {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
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

        pictureDialog = AlertDialog.Builder(contextAccount)
        pictureDialog.setTitle(getString(R.string.choose_an_option))
        val pictureDialogItems = arrayOf(
            getString(R.string.select_from_gallary),
            getString(R.string.capture_photo)
        )
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> {
                    dialog.dismiss()
                    choosePhotoFromGallary()
                }
                1 -> {
                    dialog.dismiss()
                    takePhotoFromCamera()
                }
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
        pictureDialog
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data

                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)

                    ProfileImageFilePath = saveImage(bitmap)

                    ProfilePicFile = File(ProfileImageFilePath)

                    UploadFileToServer(ProfilePicFile)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(contextAccount, "Failed! " + e.message, Toast.LENGTH_LONG).show()
                }

            }

        } else if (requestCode == CAMERA) {

            if (data != null) {

                val thumbnail = data.extras!!.get("data") as Bitmap

                ProfileImageFilePath = saveImage(thumbnail)

                ProfilePicFile = File(ProfileImageFilePath)

                UploadFileToServer(ProfilePicFile)
            }

        }
    }

    fun saveImage(myBitmap: Bitmap): String {

        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY
        )
        // have the object build the directory structure, if needed.
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg")
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                contextAccount,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return ""
    }

    // Upload File To Server
    fun UploadFileToServer(file: File) {

        if (Connectivity.isOnline(contextAccount)) {

            binding.wp10progressBar.showProgressBar()

            val imageFile: RequestBody =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file);

            val body = MultipartBody.Part.createFormData("pro_pic", file.getName(), imageFile);

            val UserId =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), sessionPrefs.getString(Constants.USER_ID))

            val call = apiInterface.UploadProfileImage(
                body, UserId
            )

            call.enqueue(object : Callback<UserInfoResponse> {
                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {

                    binding.wp10progressBar.hideProgressBar()

                    val mBottomSheetDialog: BottomSheetMaterialDialog =
                        BottomSheetMaterialDialog.Builder(activity!!)
                            .setTitle(getString(R.string.uhoh))
                            .setMessage(getString(R.string.something_went_wrong))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .build()

                    mBottomSheetDialog.show()

                }

                override fun onResponse(
                    call: Call<UserInfoResponse>,
                    response: Response<UserInfoResponse>
                ) {

                    val responseData = response.body()!!

                    val isResponse = responseData.response

                    if (isResponse) {

                        if (ProfilePicFile != null)
                            ProfilePicFile.delete();

                        val mDialog = Dialog(contextAccount)
                        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        mDialog.setCancelable(false)
                        mDialog.setContentView(R.layout.alert_success)
                        mDialog.window!!.setGravity(Gravity.CENTER)

                        mDialog.show()

                        val lblSuccessTitle = mDialog.lblSuccessTitleAnswer
                        val lblSuccessMessage = mDialog.lblSuccessMessageAnswer

                        lblSuccessTitle.text = getString(R.string.instant_online_matka)
                        lblSuccessMessage.text = getString(R.string.profile_picture_updated)

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(3000L)

                            mDialog.dismiss()
                            getUserInfo()
                        }

                    } else {

                        val mBottomSheetDialog: BottomSheetMaterialDialog =
                            BottomSheetMaterialDialog.Builder(activity!!)
                                .setTitle(getString(R.string.uhoh))
                                .setMessage(getString(R.string.something_went_wrong))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->
                                    dialogInterface.dismiss()
                                }
                                .build()

                        mBottomSheetDialog.show()
                    }

                    binding.wp10progressBar.hideProgressBar()

                }

            })
        } else {

            binding.wp10progressBar.hideProgressBar()

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity!!)
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

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}
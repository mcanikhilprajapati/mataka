package com.instantonlinematka.instantonlinematka.retrofit

import com.instantonlinematka.instantonlinematka.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    // Internet Time
    @GET("ip")
    fun getInternetTime() : Call<TimeData>

    // Get User's Information
    @POST("GetUserInfo")
    @FormUrlEncoded
    fun getUserInfo(

        @Field("user_id") UserId: String

    ): Call<UserInfoResponse>

    // Validate Mobile
    @POST("validatePhone")
    @FormUrlEncoded
    fun welcome(

        @Field("phone") MobileNumber: String

    ): Call<WelcomeResponse>

    // Login
    @POST("user_login")
    @FormUrlEncoded
    fun login(

        @Field("username") MobileNumber: String,
        @Field("password") Password: String,
        @Field("fcm_token") FCM_TOKEN: String,
        @Field("version_code") VersionCode: String

    ): Call<LoginResponse>

    // Send OTP
    @POST("SendOtp")
    @FormUrlEncoded
    fun sendOTP(

        @Field("username") MobileUsername: String,
        @Field("mobile") MobileNumber: String,
        @Field("code") Code: String

    ): Call<RegisterResponse>

    // Verify OTP
    @POST("verifyOtp")
    @FormUrlEncoded
    fun verifyOTP(

        @Field("email") MobileUsername: String,
        @Field("mobile") MobileNumber: String,
        @Field("otp") OTP: String

    ): Call<RegisterResponse>

    // Register
    @POST("user_registration")
    @FormUrlEncoded
    fun register(

        @Field("username") MobileUsername: String,
        @Field("phone") MobileNumber: String,
        @Field("password") Password: String,
        @Field("repassword") ConfirmPassword: String,
        @Field("use_referal") ReferralCode: String,
        @Field("fcm_token") FCM_TOKEN: String

    ): Call<RegisterResponse>

    // Forgot Password
    @POST("forget_password")
    @FormUrlEncoded
    fun forgotPassword(

        @Field("mobile") MobileNumber: String,
        @Field("code") MessageId: String

    ): Call<ForgotPasswordResponse>

    // Verify Forgot Password OTP
    @POST("verifyPasswordOtp")
    @FormUrlEncoded
    fun verifyForgotPassword(

        @Field("mobile") MobileNumber: String,
        @Field("otp") OTP: String

    ): Call<ForgotPasswordResponse>

    // Reset Password
    @POST("reset_password")
    @FormUrlEncoded
    fun resetPassword(

        @Field("mobile") MobileNumber: String,
        @Field("password") Password: String,
        @Field("repassword") ConfirmPassword: String

    ): Call<ForgotPasswordResponse>

    // Reset Password
    @POST("gameList")
    @FormUrlEncoded
    fun getGameList(

        @Field("user_id") UserId: String

    ): Call<GameListResponse>

    // Slider Images
    @POST("getAdsSliderAds")
    @FormUrlEncoded
    fun getSliderImages(

        @Field("user_id") UserId: String

    ): Call<SliderResponse>

    // Bidding History
    @POST("MyBid_history")
    @FormUrlEncoded
    fun getBiddingHistory(

        @Field("user_id") UserId: String

    ): Call<BiddingHistoryResponse>

    // Bidding History
    @POST("MyBid_history_bygame")
    @FormUrlEncoded
    fun getBiddingHistoryByGame(

        @Field("user_id") UserId: String,
        @Field("game_type_id") GameId: String

    ): Call<BiddingHistoryByGame>

    // Ratan Starline Game List
    @POST("StarlineGameList")
    @FormUrlEncoded
    fun getRatanStarlineGames(

        @Field("user_id") UserId: String

    ): Call<RatanStarlineGameResponse>

    // Upload Profile Image
    @Multipart
    @POST("UpdateProfile")
    fun UploadProfileImage(

        @Part files: MultipartBody.Part,
        @Part("user_id") UserId: RequestBody

    ): Call<UserInfoResponse>

    // Add Funds
    @POST("StarlineGameList")
    @FormUrlEncoded
    fun addFunds(

        @Field("user_id") UserId: String

    ): Call<AddFundsResponse>

    // Request Withdraw Funds
    @POST("sentRequestToWithdrawalFund")
    @FormUrlEncoded
    fun requestWithdraw(

        @Field("user_id") UserId: String,
        @Field("amount") Amount: String

    ): Call<RequestWithdrawResponse>

    // Account Statement
    @POST("Myaccount_statment")
    @FormUrlEncoded
    fun getAccountStatement(

        @Field("user_id") UserId: String

    ): Call<AccountStatementResponse>

    // Bonus
    @POST("bonusPoints")
    @FormUrlEncoded
    fun getBonus(

        @Field("user_id") UserId: String

    ): Call<BonusResponse>

    // Ratan Starline Bid History
    @POST("starlineBid_history")
    @FormUrlEncoded
    fun getStarlineBidHistory(

        @Field("user_id") UserId: String

    ): Call<RatanStarlineBidHistoryResponse>

    // Ratan Starline Result History
    @POST("starlineResultsByDate")
    @FormUrlEncoded
    fun getStarlineResultHistory(

        @Field("user_id") UserId: String,
        @Field("date") Date: String

    ): Call<RatanStarlineGameResponse>

    // Get Game Data
    @POST("PriviusGameAndToday")
    @FormUrlEncoded
    fun getGameData(

        @Field("user_id") UserId: String,
        @Field("game_type_id") GameId: String

    ) : Call<GameDataResponse>

    // Get Game Category
    @POST("gameCategory")
    @FormUrlEncoded
    fun getGameCategory(

        @Field("user_id") UserId: String,
        @Field("game_id") GameId: String

    ) : Call<GameCategoryResponse>

    // Beta Bid Request
    @POST("beta_Bid_request")
    @FormUrlEncoded
    fun bidRequest(

        @Field("user_id") UserId: String,
        @Field("game_type_id") GameTypeId: String,
        @Field("bid_session") BidSession: String,
        @Field("game_date") GameDate: String,
        @Field("cat_id") CatId: String,
        @Field("type") Type: String,
        @Field("array") Points: String

    ) : Call<BidRequestResponse>

    // Beta Sangam Bid Request
    @POST("beta_Sangam_Bid_request")
    @FormUrlEncoded
    fun bidSangamRequest(

        @Field("user_id") UserId: String,
        @Field("game_type_id") GameTypeId: String,
        @Field("bid_session") BidSession: String,
        @Field("game_date") GameDate: String,
        @Field("cat_id") CatId: String,
        @Field("array") Points: String

    ) : Call<BidRequestResponse>

    // Winning Stats Request
    @POST("WinningStats")
    @FormUrlEncoded
    fun getWinningStats(

        @Field("user_id") UserId: String,
        @Field("type") Type: String

    ) : Call<TotalWinningAmountResponse>

    // Game Rates Request
    @POST("gameRate")
    @FormUrlEncoded
    fun getGameRates(

        @Field("user_id") UserId: String

    ) : Call<GameRatesResponse>

    // Update Bank Request
    @POST("UpdateBankDetails")
    @FormUrlEncoded
    fun updateBankDetails(

        @Field("user_id") UserId: String,
        @Field("bank_name") BankName: String,
        @Field("bankholder_name") BankHolderName: String,
        @Field("ifc_code") BankIFSCCode: String,
        @Field("account_no") BankAccountNumber: String

    ) : Call<BankDetailsResponse>

    // Update Bank Request
    @POST("UpdatepaymentNumber")
    @FormUrlEncoded
    fun updatePaymentNumber(

        @Field("user_id") UserId: String,
        @Field("google_pay") GooglePay: String,
        @Field("paytm") PayTM: String,
        @Field("phone_pay") PhonePe: String

    ) : Call<BankDetailsResponse>

    // Get Ratan Game Category
    @POST("starlineCategory")
    @FormUrlEncoded
    fun getRatanGameCategory(

        @Field("user_id") UserId: String

    ) : Call<RatanGameCategoryResponse>

    // Beta Ratan Bid Request
    @POST("beta_starline_Bid_request")
    @FormUrlEncoded
    fun ratanBidRequest(

        @Field("user_id") UserId: String,
        @Field("game_id") GameId: String,
        @Field("cat_id") CatId: String,
        @Field("array") Points: String

    ) : Call<BidRequestResponse>

    // Beta Add Fund
    @POST("beta_addFund")
    @FormUrlEncoded
    fun addFund(

        @Field("user_id") UserId: String,
        @Field("amount") Amount: String,
        @Field("transaction_id") TransactionId: String

    ) : Call<AddFundsResponse>

    // Beta Request To Add Fund
    @POST("beta_sentRequestToAddFundt")
    @FormUrlEncoded
    fun requestToAddFund (

        @Field("user_id") UserId: String,
        @Field("amount") Amount: String,
        @Field("transaction_id") TransactionId: String,
        @Field("payment_id") PaymentId: String,
        @Field("version_code") VersionCode: String,

    ) : Call<AddFundsResponse>

    // Account Statement
    @POST("usernotification")
    @FormUrlEncoded
    fun getNotification(

        @Field("user_id") UserId: String

    ): Call<NotificationResponse>

    // Bank Validation
    @POST("generateHashKey")
    @FormUrlEncoded
    fun getHashKey(

        @Field("parameters") Parametres: String,
        @Field("salt") SaltKey: String

    ): Call<HashResponse>

    // Bank Validation
    @POST("v2/fundtransfer/validateaccount")
    @FormUrlEncoded
    fun getAccountCheck(

        @Field("api_key") ApiKey: String,
        @Field("bank_name") BankName: String,
        @Field("account_name") AccountName: String,
        @Field("account_number") AccountNumber: String,
        @Field("ifsc_code") IFSCcode: String,
        @Field("hash") HaskKey: String

    ): Call<CheckStatusResponse>

    // Get Wallet Balance
    @POST("getWalletBalance")
    @FormUrlEncoded
    fun getWalletBalance(

        @Field("user_id") UserId: String

    ) : Call<WalletBalanceResponse>

    // Get Previous Game Info
    @POST("PriviusGameAndToday")
    @FormUrlEncoded
    fun getPreviousGameInfo(

        @Field("user_id") UserId: String,
        @Field("game_type_id") GameTypeId: String

    ) : Call<PreviousGameInfoResponse>

    @POST("v2/getpaymentrequesturl")
    @FormUrlEncoded
    fun PaymentRequest(

        @Field("api_key") api_key: String,
        @Field("order_id") order_id: String,
        @Field("amount") amount: String,
        @Field("currency") currency: String,
        @Field("description") description: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("city") city: String,
        @Field("country") country: String,
        @Field("zip_code") zip_code: String,
        @Field("return_url") return_url: String,
        @Field("hash") hash: String

    ): Call<PaymentGatewayResponse>

    @POST("v2/paymentstatus")
    @FormUrlEncoded
    fun getPaymentStatus(
        @Field("api_key") api_key: String,
        @Field("order_id") order_id: String,
        @Field("hash") hash: String
    ): Call<PaymentStatusResponse>

}
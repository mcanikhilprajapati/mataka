package com.instantonlinematka.instantonlinematka.utility

object Constants {

    const val IMAGE_BASE_URL = "https://www.ingameszones.com/upload/"
    //const val IMAGE_SLIDER_BASE_URL = "https://www.ingameszones.com/photo/"
    const val IMAGE_SLIDER_BASE_URL = "https://www.instantonlinematka.com/photo/"

    const val TAG_HOME = "HOME_FRAGMENT"
    const val TAG_BIDDING_HISTORY = "BIDDING_HISTORY_FRAGMENT"
    const val TAG_ACCOUNTS = "ACCOUNTS_FRAGMENT"
    const val TAG_MORE = "MORE_FRAGMENT"

    const val FCM_KEY = "fcm_token"
    const val USER_ID = "user_id"
    const val USER_NAME = "name"
    const val USER_PHONE = "phone"
    const val USER_EMAIL = "email"
    const val USER_GENDER = "gender"
    const val USER_DOB = "dob"
    const val USER_ADDRESS = "address"
    const val REFERRAL = "referal"
    const val WALLET = "wallet"
    const val BANK_NAME = "bank_name"
    const val BANK_HOLDER_NAME = "bankholder_name"
    const val BANK_ACCOUNT_NUMBER = "account_no"
    const val BANK_IFSC_CODE = "Ifc_code"
    const val USER_PROF_PIC = "profile_pic"
    const val TRAKNPAY = "traknpay"
    const val GPAY = "gpay"
    const val PHONEPE = "phonepe"
    const val PAYTM = "paytm"
    const val BHIM = "bhim"
    const val NOTIFICATION_COUNT = "notification_count"

    // PAYMENT GATEWAY
    //API_KEY is given by the Payment Gateway. Please Copy Paste Here.
    val PG_API_KEY: String = "1ff0832d-4928-4db9-ac51-c2e34f3d8b09"

    //URL to Accept Payment Response Afdashboardter Payment. This needs to be done at the client's web server.
    const val PG_RETURN_URL = "https://www.ingameszones.com/api/success"

    //Enter the Mode of Payment Here . Allowed Values are "LIVE" or "TEST".
    const val PG_MODE = "LIVE"

    //PG_CURRENCY is given by the Payment Gateway. Only "INR" Allowed.
    const val PG_CURRENCY = "INR"

    //PG_COUNTRY is given by the Payment Gateway. Only "IND" Allowed.
    const val PG_COUNTRY = "IND"

    const val PG_AMOUNT = "2"
    const val PG_EMAIL = "instantonlinematka@gmail.com"
    const val PG_NAME = "LOKESH"
    const val PG_PHONE = "9731885622"
    var PG_ORDER_ID = ""
    const val PG_DESCRIPTION = "This is for Online Matka Payment"
    const val PG_CITY = "Bengaluru"
    const val PG_STATE = "Karnataka"
    const val PG_ADD_1 = "Marathalli"
    const val PG_ADD_2 = "Second Cross"
    const val PG_ZIPCODE = "560001"
    const val PG_UDF1 = ""
    const val PG_UDF2 = ""
    const val PG_UDF3 = ""
    const val PG_UDF4 = ""
    const val PG_UDF5 = ""
}
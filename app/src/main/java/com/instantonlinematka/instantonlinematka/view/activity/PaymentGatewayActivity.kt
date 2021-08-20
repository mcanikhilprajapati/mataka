package com.instantonlinematka.instantonlinematka.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.test.pg.secure.pgsdkv4.PaymentGatewayPaymentInitializer
import com.test.pg.secure.pgsdkv4.PaymentParams
import kotlinx.android.synthetic.main.activity_payment_gateway.*
import java.util.*

class PaymentGatewayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_gateway)

        wp10progressBar.showProgressBar()

        val rnd = Random()
        val n = 100000 + rnd.nextInt(900000)
        Constants.PG_ORDER_ID = "Live" + Integer.toString(n)

        val pgPaymentParams = PaymentParams()
        pgPaymentParams.aPiKey = Constants.PG_API_KEY
        pgPaymentParams.amount = Constants.PG_AMOUNT
        pgPaymentParams.email = Constants.PG_EMAIL
        pgPaymentParams.name = Constants.PG_NAME
        pgPaymentParams.phone = Constants.PG_PHONE
        pgPaymentParams.orderId = Constants.PG_ORDER_ID
        pgPaymentParams.currency = Constants.PG_CURRENCY
        pgPaymentParams.description = Constants.PG_DESCRIPTION
        pgPaymentParams.city = Constants.PG_CITY
        pgPaymentParams.state = Constants.PG_STATE
        pgPaymentParams.addressLine1 = Constants.PG_ADD_1
        pgPaymentParams.addressLine2 = Constants.PG_ADD_2
        pgPaymentParams.zipCode = Constants.PG_ZIPCODE
        pgPaymentParams.country = Constants.PG_COUNTRY
        pgPaymentParams.setReturnUrl(Constants.PG_RETURN_URL)
        pgPaymentParams.mode = Constants.PG_MODE
        pgPaymentParams.setUdf1(Constants.PG_UDF1)
        pgPaymentParams.setUdf2(Constants.PG_UDF2)
        pgPaymentParams.setUdf3(Constants.PG_UDF3)
        pgPaymentParams.setUdf4(Constants.PG_UDF4)
        pgPaymentParams.setUdf5(Constants.PG_UDF5)
        pgPaymentParams.enableAutoRefund = "n"
        val pgPaymentInitialzer =
            PaymentGatewayPaymentInitializer(pgPaymentParams, this@PaymentGatewayActivity)
        pgPaymentInitialzer.initiatePaymentProcess()
    }

    override fun onPause() {
        super.onPause()
    }
}

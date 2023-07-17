package com.example.chama

import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.chama.Model.AccessToken
import com.example.chama.Model.STKPush
import butterknife.BindView
import butterknife.ButterKnife
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

import com.example.chama.R
import com.example.chama.Services.DarajaApiClient
import com.example.chama.Constants.BUSINESS_SHORT_CODE
import com.example.chama.Constants.CALLBACKURL
import com.example.chama.Constants.PARTYB
import com.example.chama.Constants.PASSKEY
import com.example.chama.Constants.TRANSACTION_TYPE
import com.example.chama.Utils.Utils


class ContributeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mApiClient: DarajaApiClient
    private lateinit var mProgressDialog: ProgressDialog



    lateinit var mAmount: EditText

    lateinit var mPhone: EditText

    lateinit var mPay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute)
        ButterKnife.bind(this)

        mProgressDialog = ProgressDialog(this)
        mApiClient = DarajaApiClient()
        mApiClient.setIsDebug(true) //Set True to enable logging, false to disable.

        mPay.setOnClickListener(this)

        getAccessToken()
    }

    fun getAccessToken() {
        mApiClient.setGetAccessToken(true)
        mApiClient.mpesaService().getAccessToken()?.enqueue(object : Callback<AccessToken?> {
            override fun onResponse(call: Call<AccessToken?>, response: Response<AccessToken?>) {
                if (response.isSuccessful) {
                    mApiClient.setAuthToken(response.body()?.accessToken)
                }
            }

            override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                Timber.e(t)
            }
        })
    }



    override fun onClick(view: View) {
        if (view === mPay) {
            val phoneNumber = mPhone.text.toString()
            val amount = mAmount.text.toString()
            performSTKPush(phoneNumber, amount)
        }
    }

    fun performSTKPush(phoneNumber: String, amount: String) {
        mProgressDialog.setMessage("Processing your request")
        mProgressDialog.setTitle("Please Wait...")
        mProgressDialog.isIndeterminate = true
        mProgressDialog.show()
        val timestamp = Utils.fetchTimestamp()
        val stkPush = STKPush(
            BUSINESS_SHORT_CODE,
            Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
            timestamp,
            TRANSACTION_TYPE,
            amount,
            Utils.sanitizePhoneNumber(phoneNumber),
            PARTYB,
            Utils.sanitizePhoneNumber(phoneNumber),
            CALLBACKURL,
            "MPESA Android Test", //Account reference
            "Testing"  //Transaction description
        )

        mApiClient.setGetAccessToken(false)

        //Sending the data to the Mpesa API, remember to remove the logging when in production.
        mApiClient.mpesaService().sendPush(stkPush)?.enqueue(object : Callback<STKPush?> {
            override fun onResponse(call: Call<STKPush?>, response: Response<STKPush?>) {
                mProgressDialog.dismiss()
                try {
                    if (response.isSuccessful) {
                        val stkPushResponse = response.body()
                        // Handle successful response
                        Timber.d("post submitted to API. $stkPushResponse")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        // Handle error response
                        Timber.e("Response $errorBody")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<STKPush?>, t: Throwable) {
                mProgressDialog.dismiss()
                Timber.e(t)
            }
        })
    }


    override fun onPointerCaptureChanged(hasCapture: Boolean) {
        // Implement this method if needed
    }
}

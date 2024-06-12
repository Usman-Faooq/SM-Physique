package com.buzzwaretech.gymphysique.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.adapters.KartMiniItemAdapter
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ActivityKartDetailBinding
import com.buzzwaretech.gymphysique.models.KartModel
import com.buzzwaretech.gymphysique.models.PostModel
import com.buzzwaretech.gymphysique.stripe.*
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KartDetailActivity : BaseActivity(), KartMiniItemAdapter.OnItemClickListener {

    lateinit var binding : ActivityKartDetailBinding

    lateinit var model : KartModel
    private var errorDialog: AlertDialog? = null

    private lateinit var stripe: Stripe
    private lateinit var paymentSession: PaymentSession
    private var readyToCharge = false
    private var isStripeInitialized = false
    private var customerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKartDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = intent.getParcelableExtra("kartModel")!!

        Log.d("LOGGER", "start:  ${Constants.currentUser.cust_id}")
        createCustomer()
        setStripe()

        setView()
        setListeners()

    }

    private fun createCustomer() {
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("name", Constants.currentUser.name)
            .addFormDataPart("email", Constants.currentUser.email)
            .addFormDataPart("cus_id", "")
            .build()

        StripeController.apiService.createClient(requestBody)
            .enqueue(object : retrofit2.Callback<CustomerResponse>{
                override fun onResponse(call: Call<CustomerResponse>, response: Response<CustomerResponse>) {
                    if (response.isSuccessful){
                        customerId = response.body()!!.cus_id
                        EmpeheralKeyProvider.cusId = customerId.toString()
                        Log.d("LOGGER", "Customer Id Reponse if : ${response.body()!!.cus_id}")
                    }else{
                        Log.d("LOGGER", "Customer Id Response Else Part: ${response.message()}, Code: ${response.code()}" )
                    }
                }

                override fun onFailure(call: Call<CustomerResponse>, t: Throwable) {
                    Log.d("LOGGER", "Customer Id Failure:  ${t.message}")
                }

            })
    }

    private fun setStripe() {
        PaymentConfiguration.init(this, Constants.stripePublishableKey)
        stripe = Stripe(this, Constants.stripePublishableKey)
        CustomerSession.initCustomerSession(this, EmpeheralKeyProvider())
        paymentSession = PaymentSession(
            this, PaymentSessionConfig.Builder()
                .setShippingInfoRequired(false)
                .setShippingMethodsRequired(false)
                .setShippingInfoRequired(false)
                .build()
        )
        isStripeInitialized = true
    }

    private fun setView() {

        binding.kartNameTV.text = model.name
        binding.kartPriceTV.text = "$${model.price}"
        binding.kartDescriptionTV.text = model.description

        Glide.with(this)
            .load(model.images[0])
            .placeholder(R.drawable.holder_post)
            .into(binding.kartIV)

        binding.kartRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.kartRV.adapter = KartMiniItemAdapter(this, model.images, this)

    }

    private fun setListeners() {

        binding.backIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.buyNowTV.setOnClickListener {
            val totalPrice = model.price.toDouble() * 100
            setUpStripe("${totalPrice.toInt()}")
            //val intent = Intent(this, PaymentActivity::class.java)
            //startActivity(intent)
            //overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }
    }

    private fun setUpStripe(price: String) {
        if (!isStripeInitialized) {
            setStripe()
        }

        paymentSession.init(object : PaymentSession.PaymentSessionListener {
            override fun onCommunicatingStateChanged(isCommunicating: Boolean) {}
            override fun onError(errorCode: Int, errorMessage: String) {
                Log.d("LOGGER", "onError: $errorMessage")
                showError(errorMessage ?: "")
            }

            override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                val paymentMethod = data.paymentMethod
                readyToCharge = false
                if (data.isPaymentReadyToCharge) {
                    mDialog.show()
                    readyToCharge = true

                    val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("amount", price)
                        .addFormDataPart("pm_id", paymentMethod!!.id.toString())
                        .addFormDataPart("cus_id", customerId.toString())
                        .build()

                    Log.d("LOGGER", "PM ID : ${paymentMethod.id.toString()}")

                    StripeController.apiService.stripePaymentIntent(requestBody)
                        .enqueue(object : Callback<AccountPaymentReturnData> {
                            override fun onResponse(call: Call<AccountPaymentReturnData>, response: Response<AccountPaymentReturnData>) {
                                if (response.isSuccessful){
                                    val error = response.body()!!.return_data.error
                                    val key = response.body()!!.return_data.key
                                    if (error == 0){
                                        val confirmParams = ConfirmPaymentIntentParams.create(key)
                                        stripe = Stripe(this@KartDetailActivity, Constants.stripePublishableKey)
                                        stripe.confirmPayment(this@KartDetailActivity, confirmParams)
                                        //mDialog.show()
                                        mDialog.dismiss()

                                    }else{
                                        mDialog.dismiss()
                                        showError("$error: $key")
                                    }

                                }else{
                                    mDialog.dismiss()
                                    showError("Response Error: ${response.message()}, Code: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<AccountPaymentReturnData>, t: Throwable) {
                                mDialog.dismiss()
                                Log.d("LOGGER", "Final Response Failure: ${t.message}")
                            }
                        })
                } else {
                    if (paymentMethod != null) {
                        paymentSession.presentPaymentMethodSelection(EmpeheralKeyProvider.cusId)
                        Log.d("LOGGER_", "Ready: ")
                    } else {
                        paymentSession.presentPaymentMethodSelection(EmpeheralKeyProvider.cusId)
                    }
                }
            }
        })
    }

    override fun onItemClick(selectedImage: String) {
        Glide.with(this)
            .load(selectedImage)
            .placeholder(R.drawable.holder_post)
            .into(binding.kartIV)
    }

    fun showSuccessMessage() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun showError(msg: String) {
        if (errorDialog != null && errorDialog!!.isShowing) {
            return
        }
        errorDialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setTitle("Alert")
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        errorDialog!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && !readyToCharge) {
            paymentSession.handlePaymentData(requestCode, resultCode, data)
        }
        if (readyToCharge) stripe.onPaymentResult(requestCode, data, PaymentResultCallback(this@KartDetailActivity))

    }
}
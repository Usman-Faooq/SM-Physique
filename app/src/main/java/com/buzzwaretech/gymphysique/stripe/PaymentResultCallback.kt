package com.buzzwaretech.gymphysique.stripe

import android.util.Log
import com.buzzwaretech.gymphysique.activities.KartDetailActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.StripeIntent
import java.lang.ref.WeakReference
import java.util.Objects

class PaymentResultCallback(activity: KartDetailActivity) : ApiResultCallback<PaymentIntentResult> {
    private val activityRef: WeakReference<KartDetailActivity> = WeakReference(activity)

    override fun onSuccess(result: PaymentIntentResult) {
        val activity: KartDetailActivity? = activityRef.get()
        if (activity == null) {
            return
        }

        val paymentIntent: PaymentIntent? = result.intent
        val status: StripeIntent.Status? = paymentIntent?.status
        Log.d("Status", "onSuccess: Status$status")
        when (status) {
            StripeIntent.Status.RequiresCapture -> activity.showSuccessMessage()
            StripeIntent.Status.Succeeded -> {
                // Payment completed successfully
                val gson: Gson = GsonBuilder().setPrettyPrinting().create()
                activity.showSuccessMessage()
            }
            StripeIntent.Status.RequiresPaymentMethod -> {
                // Payment failed – allow retrying using a different payment method
                activity.showError("Payment failed " + Objects.requireNonNull(paymentIntent.lastPaymentError)!!.message)
                Log.d("Test", "onSuccess: " + paymentIntent.lastPaymentError)
            }
            else -> {

            }
        }
    }

    override fun onError(e: Exception) {
        val activity: KartDetailActivity? = activityRef.get()
        if (activity == null) {
            return
        }
        Log.d("Test", "Errpr: $e")
        activity.showError("Error $e")
    }
}
package com.buzzwaretech.gymphysique.stripe

import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Size
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmpeheralKeyProvider : EphemeralKeyProvider {

    override fun createEphemeralKey(
        @NonNull @Size(min = 4) apiVersion: String,
        @NonNull keyUpdateListener: EphemeralKeyUpdateListener
    ) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("api_version", apiVersion)
            .addFormDataPart("cus_id", cusId)
            .build()

        StripeController.apiService.getEphemeralKey(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody.string()) // Convert response body to string
                        val keyObject = jsonObject.optJSONObject("key")
                        if (keyObject != null) {
                            val id = keyObject.getString("id")
                            keyUpdateListener.onKeyUpdate(keyObject.toString())
                        } else {
                            Log.d("LOGGER", "Error: 'key' object not found in response")
                        }
                    } else {
                        Log.d("LOGGER", "Error: Response body is null")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("LOGGER", "Error: ${e.message}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("LOGGER", "Error Fail : ${t.message}")
                keyUpdateListener.onKeyUpdateFailure(10, "Failed to get Empheral key")
            }
        })
    }


    companion object {
        var cusId = ""
    }
}
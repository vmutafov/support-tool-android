package com.azbouki.supporttool.sdk.live.twilio

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class TwilioTokenService {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun getTwilioVideoToken(onSuccess: (String) -> Unit, onFailure: (Throwable) -> Unit = {}) {
        val identityJson = gson.toJson(TwilioVideoTokenIdentity())
        val request = Request.Builder()
            .url("https://token-service-1704-dev.twil.io/token")
            .post(identityJson.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        onFailure(IOException("Unexpected code $response"))
                    }

                    val bodyAsJson = response.body!!.string()
                    val twilioVideoToken = gson.fromJson(bodyAsJson, TwilioVideoToken::class.java)
                    onSuccess(twilioVideoToken.accessToken)
                }
            }
        })
    }
}

data class TwilioVideoToken(val accessToken: String)
data class TwilioVideoTokenIdentity(val identity: String = "test-device")
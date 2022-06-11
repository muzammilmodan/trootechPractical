package com.trootechdemo.restapi.api

import android.content.Intent
import android.util.Log
import org.json.JSONObject
import retrofit2.Response

//Create API response managed call, Handle response, code and return values managed.
abstract class BaseApiResponse {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiCallback<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return ApiCallback.OnSuccess(body)
                }
            }else {
                if (response.code() == 403) {

                   /*
                    ApiCallback.OnError("Unauthorized request, Please restart app to login again.")*/
                }else if (response.code() == 409) {

                    Log.e("API_ERROR", response.errorBody().toString())
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    var message=jObjError.getJSONObject("error").getString("message")
                    return error("${response.code()} ${message}")
                }else if (response.code() == 404) {

                    Log.e("API_ERROR", response.errorBody().toString())
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    var message=jObjError.getJSONObject("error").getString("message")
                    return error("${response.code()} ${message}")
                } else {
                    Log.e("API_ERROR", response.message())
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    var message=jObjError.getJSONObject("error").getString("message")
                    return error("${response.code()} ${message}")
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): ApiCallback<T> =
        ApiCallback.OnError("Api call failed $errorMessage")

}
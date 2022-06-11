package com.trootechdemo.restapi.api

sealed class ApiCallback<T>(val data: T? = null, val message: String? = null) {
    class OnSuccess<T>(data: T?) : ApiCallback<T>(data)
    class OnError<T>(message: String?, data: T? = null) : ApiCallback<T>(data, message)
    class OnLoading<T> : ApiCallback<T>()
}

/**
 * Common class create handle Network call response
 **/
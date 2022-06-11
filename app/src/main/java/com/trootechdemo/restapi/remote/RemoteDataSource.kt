package com.trootechdemo.restapi.remote

import com.trootechdemo.restapi.api.ApiServices
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: ApiServices) {

    suspend fun callGetCategoryList(api_key_values:String)
    = apiService.getCategoryList(api_key_values)

    suspend fun callGetSubCategoryList(api_key_values:String)
            = apiService.getSubCategoryList(api_key_values)
}
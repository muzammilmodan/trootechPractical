package com.trootechdemo.restapi.api

import com.trootechdemo.model.CategoryListResponse
import com.trootechdemo.model.SubCategoryResponse
import com.trootechdemo.restapi.Apis
import retrofit2.Response
import retrofit2.http.*

/**
 * Stored all network regarding call, using suspend method thru handle API call.
 **/
interface ApiServices {

    @GET(Apis.CATEGORY_LIST)
    suspend fun getCategoryList(@Header(Apis.REQUEST_API_KEY) auth: String?): Response<CategoryListResponse>

    @GET(Apis.SUB_CATEGORY_LIST)
    suspend fun getSubCategoryList(@Header(Apis.REQUEST_API_KEY) auth: String?): Response<SubCategoryResponse>

}
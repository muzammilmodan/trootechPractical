package com.trootechdemo.restapi.repository

import com.trootechdemo.model.CategoryListResponse
import com.trootechdemo.model.SubCategoryResponse
import com.trootechdemo.restapi.api.ApiCallback
import com.trootechdemo.restapi.api.BaseApiResponse
import com.trootechdemo.restapi.remote.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

//Repository connect with ViewModel, So any action thru return values so suspend method provide details in ViewModel.
@ActivityRetainedScoped
class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun callGetCategoryListAPi(api_key:String): Flow<ApiCallback<CategoryListResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.callGetCategoryList(api_key) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun callGetSubCategoryListAPi(api_key:String): Flow<ApiCallback<SubCategoryResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.callGetSubCategoryList(api_key) })
        }.flowOn(Dispatchers.IO)
    }
}


package com.trootechdemo.ui.category


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.trootechdemo.model.CategoryListResponse
import com.trootechdemo.model.SubCategoryResponse
import com.trootechdemo.restapi.api.ApiCallback
import com.trootechdemo.restapi.repository.Repository


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _responseMutableGetCategory: MutableLiveData<ApiCallback<CategoryListResponse>> = MutableLiveData()
    val responseGetCategoryMain: LiveData<ApiCallback<CategoryListResponse>> = _responseMutableGetCategory

    fun fetchCategoryListResponse() = viewModelScope.launch {
        repository.callGetCategoryListAPi("bd_suvlascentralpos").collect { values ->
            _responseMutableGetCategory.value = values
        }
    }

    //Sub Category... Call....
    //Using Live data thru observe api call.. request and response and pass data to UI means View
    private val _responseMutableGetSubCategory: MutableLiveData<ApiCallback<SubCategoryResponse>> = MutableLiveData()
    val responseGetSubCategoryMain: LiveData<ApiCallback<SubCategoryResponse>> = _responseMutableGetSubCategory

    fun fetchSubCategoryListResponse(mainCategoryKey:String) = viewModelScope.launch {
        repository.callGetSubCategoryListAPi(mainCategoryKey).collect { values ->
            _responseMutableGetSubCategory.value = values
        }
    }

}
package com.kloeapp.ui.auth.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kloeapp.model.common.CommonModel
import com.kloeapp.model.request.SignInRequest
import com.kloeapp.model.response.SignInResponse
import com.kloeapp.restapi.api.ApiCallback
import com.kloeapp.restapi.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _responseMutableLogin: MutableLiveData<ApiCallback<CommonModel<SignInResponse>>> = MutableLiveData()
    val responseLoginMain: LiveData<ApiCallback<CommonModel<SignInResponse>>> = _responseMutableLogin

    fun fetchLoginResponse(signInRequest: SignInRequest) =viewModelScope.launch {
            repository.callLoginAPi(signInRequest).collect { values ->
                _responseMutableLogin.value = values
            }
        }





}
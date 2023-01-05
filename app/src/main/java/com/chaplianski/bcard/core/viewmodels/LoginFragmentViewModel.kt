package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.usecases.CheckLoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginFragmentViewModel @Inject constructor(private val checkLoginUseCase: CheckLoginUseCase): ViewModel() {


    var _loginResponse = MutableLiveData<Long>()
    val loginResponse: LiveData<Long> get() = _loginResponse

    fun checkLoginUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = checkLoginUseCase.execute(user)
            _loginResponse.postValue(response)
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }
}
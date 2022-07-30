package com.chaplianski.bcard.presenter.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.usecases.CheckLoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginFragmentViewModel @Inject constructor(private val sentLoginUseCase: CheckLoginUseCase): ViewModel() {


    var _loginResponse = MutableLiveData<String>()
    val loginResponse: LiveData<String> get() = _loginResponse

    fun checkLoginUser(email: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            _loginResponse = sentLoginUseCase.execute(email, password)
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }
}
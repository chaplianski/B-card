package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.usecases.AddUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationFragmentViewModel @Inject constructor(private val addUserUseCase: AddUserUseCase) :
    ViewModel() {

    var _userId = MutableLiveData<Long>()
    val userId: LiveData<Long> get() = _userId


//    fun addUser(email: String, password: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val newUser = User(0, email, password)
//            val id = addUserUseCase.execute(newUser)
//            _userId.postValue(id)
//        }
//    }

    override fun onCleared() {
        viewModelScope.cancel()
    }
}
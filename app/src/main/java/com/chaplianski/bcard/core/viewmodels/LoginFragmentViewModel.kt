package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.usecases.AddUserUseCase
import com.chaplianski.bcard.domain.usecases.CheckLoginUseCase
import com.chaplianski.bcard.domain.usecases.GetSecretQuestionUseCase
import com.chaplianski.bcard.domain.usecases.UpdateUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginFragmentViewModel @Inject constructor(
    private val checkLoginUseCase: CheckLoginUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val getSecretQuestionUseCase: GetSecretQuestionUseCase,
    private val updateUserUseCase: UpdateUserUseCase
    ): ViewModel() {

    private var _userId = MutableLiveData<Long>()
    val userId: LiveData<Long> get() = _userId

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private var _loginResponse = MutableLiveData<Long>()
    val loginResponse: LiveData<Long> get() = _loginResponse

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = addUserUseCase.execute(user)
            _userId.postValue(id)
        }
    }

    fun checkLoginUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = checkLoginUseCase.execute(user)
            _loginResponse.postValue(response)
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    fun getSecretQuestion(login: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = getSecretQuestionUseCase.execute(login)
            _user.postValue(response)
        }
    }

    fun updateUser(newUser: User) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserUseCase.execute(newUser)
        }
    }
}
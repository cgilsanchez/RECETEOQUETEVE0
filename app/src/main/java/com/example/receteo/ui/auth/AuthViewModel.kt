package com.example.receteo.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receteo.data.remote.models.User
import com.example.receteo.data.repository.AuthRepository
import com.example.receteo.data.remote.models.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository

) : ViewModel() {


    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> get() = _userData


    fun getUserData(token: String) {
        viewModelScope.launch {
            val user = authRepository.getUserData(token)
            _userData.postValue(user)
        }
    }

    fun login(identifier: String, password: String, onResult: (UserModel?) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.login(identifier, password)
            onResult(user)
        }
    }

    fun register(username: String, email: String, password: String, onResult: (UserModel?) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.register(username, email, password)
            onResult(user)
        }
    }
}

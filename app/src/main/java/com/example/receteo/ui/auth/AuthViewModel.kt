package com.example.receteo.ui.auth

import android.content.Context
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

    private val _authState = MutableLiveData<Boolean>()
    val authState: LiveData<Boolean> get() = _authState

    fun getUserData(token: String) {
        viewModelScope.launch {
            val user = authRepository.getUserData(token)
            _userData.postValue(user)
        }
    }

    fun login(identifier: String, password: String, context: Context, onResult: (UserModel?) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.login(identifier, password)
            if (user != null) {
                saveToken(context, user.jwt)
                _authState.postValue(true)
            } else {
                _authState.postValue(false)
            }
            onResult(user)
        }
    }

    fun register(username: String, email: String, password: String, context: Context, onResult: (UserModel?) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.register(username, email, password)
            onResult(user)
        }
    }

    fun logout(context: Context) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("jwt").apply()
        _authState.postValue(false)
    }

    private fun saveToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("jwt", token).apply()
    }
}

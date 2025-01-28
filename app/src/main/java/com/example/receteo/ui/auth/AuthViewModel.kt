package com.example.receteo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receteo.data.repository.AuthRepository
import com.example.receteo.data.remote.models.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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

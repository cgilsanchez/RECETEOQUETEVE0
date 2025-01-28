package com.example.receteo.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receteo.data.remote.models.UserModel
import com.example.receteo.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(username, password)
                if (response != null) {
                    _user.postValue(response)
                } else {
                    println("❌ Error en login: Credenciales incorrectas")
                    _user.postValue(null)
                }
            } catch (e: Exception) {
                println("❌ Excepción en login: ${e.message}")
                _user.postValue(null)
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(username, email, password)
                if (response != null) {
                    _user.postValue(response)
                } else {
                    println("❌ Error en registro")
                    _user.postValue(null)
                }
            } catch (e: Exception) {
                println("❌ Excepción en register: ${e.message}")
                _user.postValue(null)
            }
        }
    }
}

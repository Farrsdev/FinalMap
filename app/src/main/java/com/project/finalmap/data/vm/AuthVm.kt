package com.project.finalmap.data.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.project.finalmap.data.models.User
import com.project.finalmap.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthVm : ViewModel(){
    val repo = AuthRepository()
    private val _users = MutableStateFlow<User?>(null)
    val users = _users.asStateFlow()

    var successMsg by mutableStateOf<String?>(null)
        private set

    var errorMsg by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun register(user: User, password:String, onSuccess: () -> Unit){
        isLoading = true
        repo.register(user, password){ success, msg ->
            isLoading = false
            if(success){
                successMsg = msg
                onSuccess()
            } else{
                errorMsg = msg
            }
        }
    }

    fun login(username:String, password:String, onSuccess: () -> Unit){
        isLoading = true
        repo.login(username, password){ success, msg ->
            isLoading = false
            if(success){
                successMsg = msg
                onSuccess()
            } else{
                errorMsg = msg
            }
        }
    }

    fun getCurrentUser(){
        isLoading = true
        repo.getCurrentUser { user, msg ->
            isLoading = false

            if(user != null){
                _users.value = user
                successMsg = msg
            } else{
                errorMsg = msg
            }
        }
    }

    fun updateProfile(nama: String, phone: String, onDone: () -> Unit){
        isLoading = true
        repo.updateProfile(nama, phone){ success, msg ->
            isLoading = false
            if(success){
                _users.value = _users.value?.copy(nama = nama.trim(), phone = phone.trim())
                successMsg = msg
                onDone()
            } else{
                errorMsg = msg
            }
        }
    }

    fun showError(msg: String){
        errorMsg = msg
    }

    fun clearMsg(){
        successMsg = null
        errorMsg = null
    }

    fun logout(){
        repo.logout()
        _users.value = null
    }
}

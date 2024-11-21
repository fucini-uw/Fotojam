package com.cs407.fotojam

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserState(
    val id: Int = 0,
    val name: String = "",
    val passwd: String = ""
)

class UserViewModel : ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState = _userState.asStateFlow()

    // Updates the current user in the viewmodel
    fun setUser(state: UserState) {
        _userState.update {
            state
        }
    }
}

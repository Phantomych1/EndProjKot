package com.example.myapplication.ui.session

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    fun signInAnon(onErr: () -> Unit = {}) {
        if (auth.currentUser != null) {
            _user.value = auth.currentUser
            return
        }
        auth.signInAnonymously()
            .addOnSuccessListener { _user.value = it.user }
            .addOnFailureListener { onErr() }
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
    }
}

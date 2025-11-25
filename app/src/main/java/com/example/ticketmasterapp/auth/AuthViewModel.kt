package com.example.ticketmasterapp.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {

        val cleanEmail = email.trim()

        auth.createUserWithEmailAndPassword(cleanEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    val ex = task.exception
                    val code = (ex as? FirebaseAuthException)?.errorCode
                    val message = ex?.localizedMessage ?: "Unknown error"

                    Log.e("AuthViewModel", "REGISTER FAILED → code=$code, message=$message", ex)
                    onResult(false, "$code: $message")
                }
            }
            .addOnFailureListener { ex ->
                Log.e("AuthViewModel", "REGISTER FAILURE", ex)
                onResult(false, ex.localizedMessage)
            }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {

        val cleanEmail = email.trim()

        auth.signInWithEmailAndPassword(cleanEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    val ex = task.exception
                    val code = (ex as? FirebaseAuthException)?.errorCode
                    val message = ex?.localizedMessage ?: "Unknown error"

                    Log.e("AuthViewModel", "LOGIN FAILED → code=$code, message=$message", ex)
                    onResult(false, "$code: $message")
                }
            }
            .addOnFailureListener { ex ->
                Log.e("AuthViewModel", "LOGIN FAILURE", ex)
                onResult(false, ex.localizedMessage)
            }
    }
}

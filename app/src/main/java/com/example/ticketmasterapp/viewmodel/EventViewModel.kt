package com.example.ticketmasterapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketmasterapp.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.ticketmasterapp.network.RetrofitClient
import com.example.ticketmasterapp.network.MpesaConfig
import com.example.ticketmasterapp.network.MpesaApi
import com.example.ticketmasterapp.models.StkPushRequest
import com.example.ticketmasterapp.models.AccessTokenResponse
import com.example.ticketmasterapp.models.StkPushResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
class EventViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _eventList = MutableStateFlow<List<Event>>(emptyList())
    val eventList = _eventList.asStateFlow()

    // 🔥 Add a new event
    fun addEvent(
        event: Event,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        firestore.collection("events")
            .add(event)
            .addOnSuccessListener {
                println("🔥 EVENT SAVED: ${it.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                println("❌ FAILED TO SAVE EVENT: ${e.message}")
                onFailure()
            }
    }


    // 🔥 Load all events and listen for real-time updates
    fun loadEvents() {
        firestore.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Event::class.java)?.copy(id = doc.id)
                    }
                    _eventList.value = list
                }
            }
    }

    // 🔥 Fetch a single event by ID using suspend function
    suspend fun getEventFromFirestore(eventId: String): Event? {
        return try {
            val doc = firestore.collection("events")
                .document(eventId)
                .get()
                .await()

            doc.toObject(Event::class.java)?.copy(id = doc.id)

        } catch (e: Exception) {
            null
        }
    } // 🔥 Initiate M-Pesa Payment (add this function here)
    fun initiateMpesaPayment(
        phone: String,
        amount: Int,
        eventName: String,
        ticketType: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { // ✅ Run all network calls on IO thread

                try {
                    // 1️⃣ CREATE AUTH STRING
                    val keys = "${MpesaConfig.CONSUMER_KEY}:${MpesaConfig.CONSUMER_SECRET}"
                    val auth = "Basic " + android.util.Base64.encodeToString(
                        keys.toByteArray(),
                        android.util.Base64.NO_WRAP
                    )

                    // 2️⃣ GET TOKEN
                    val tokenCall = RetrofitClient.instance.getAccessToken(auth)
                    val tokenResponse = tokenCall.execute()
                    if (!tokenResponse.isSuccessful) {
                        println("❌ Token error: ${tokenResponse.message()}")
                        return@withContext
                    }

                    val token = tokenResponse.body()?.access_token ?: return@withContext

                    // 3️⃣ CREATE TIMESTAMP & PASSWORD
                    val timestamp = java.text.SimpleDateFormat("yyyyMMddHHmmss").format(java.util.Date())
                    val password = android.util.Base64.encodeToString(
                        "${MpesaConfig.SHORTCODE}${MpesaConfig.PASSKEY}$timestamp".toByteArray(),
                        android.util.Base64.NO_WRAP
                    )

                    // 4️⃣ PREPARE STK PUSH REQUEST
                    val stkRequest = StkPushRequest(
                        BusinessShortCode = MpesaConfig.SHORTCODE,
                        Password = password,
                        Timestamp = timestamp,
                        Amount = amount,
                        PartyA = phone,
                        PartyB = MpesaConfig.SHORTCODE,
                        PhoneNumber = phone,
                        CallBackURL = MpesaConfig.CALLBACK_URL,
                        AccountReference = "$eventName - $ticketType",
                        TransactionDesc = "Ticket purchase"
                    )

                    // 5️⃣ SEND STK PUSH
                    val stkCall = RetrofitClient.instance.stkPush("Bearer $token", stkRequest)
                    val stkResponse = stkCall.execute()

                    if (stkResponse.isSuccessful) {
                        println("✅ STK PUSH SENT: ${stkResponse.body()}")
                    } else {
                        println("❌ STK PUSH FAILED: ${stkResponse.message()}")
                    }

                } catch (e: Exception) {
                    println("❌ M-Pesa STK Push error: ${e.localizedMessage}")
                }
            }
        }
    }

}

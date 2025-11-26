package com.example.ticketmasterapp.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketmasterapp.models.Event
import com.example.ticketmasterapp.models.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.example.ticketmasterapp.network.RetrofitClient
import com.example.ticketmasterapp.network.MpesaConfig
import com.example.ticketmasterapp.models.StkPushRequest
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class EventViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _eventList = MutableStateFlow<List<Event>>(emptyList())
    val eventList = _eventList.asStateFlow()




    // ---------------------------------------------------------
    //  SAVE EVENT
    // ---------------------------------------------------------
    fun addEvent(event: Event, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firestore.collection("events")
            .add(event)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    // ---------------------------------------------------------
    //  LOAD EVENTS LIVE
    // ---------------------------------------------------------
    fun loadEvents() {
        firestore.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    _eventList.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Event::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    // ---------------------------------------------------------
    //  GET SINGLE EVENT
    // ---------------------------------------------------------
    suspend fun getEventFromFirestore(eventId: String): Event? {
        return try {
            val doc = firestore.collection("events").document(eventId).get().await()
            doc.toObject(Event::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // ---------------------------------------------------------
    // 🔥  GENERATE QR CODE (BASE64)
    // ---------------------------------------------------------
    suspend fun generateQrBase64(text: String): String = withContext(Dispatchers.Default) {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 400, 400)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }

        val output = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output)
        android.util.Base64.encodeToString(output.toByteArray(), android.util.Base64.DEFAULT)
    }

    // ---------------------------------------------------------
    // 🔥  SAVE TICKET TO FIRESTORE
    // ---------------------------------------------------------
    fun saveTicketToFirestore(
        event: Event,
        ticketType: String,
        count: Int,
        totalPrice: Int,
        onSaved: () -> Unit
    ) {

        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            val ticketId = UUID.randomUUID().toString()

            val qrText = "TICKET:$ticketId:${event.id}:${ticketType}:${uid}"
            val qrBase64 = generateQrBase64(qrText)

            val ticket = Ticket(
                ticketId = ticketId,
                eventId = event.id!!,
                eventName = event.name,
                ticketType = ticketType,
                count = count,
                totalPrice = totalPrice,
                qrBase64 = qrBase64,
                datePurchased = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            )

            firestore.collection("users")
                .document(uid)
                .collection("tickets")
                .document(ticketId)
                .set(ticket)
                .await()

            onSaved()
        }
    }

    // ---------------------------------------------------------
    // 🔥 LOAD USER TICKETS
    // ---------------------------------------------------------
    private val _userTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val userTickets = _userTickets.asStateFlow()

    fun loadUserTickets() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .collection("tickets")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    _userTickets.value =
                        snapshot.documents.mapNotNull { it.toObject(Ticket::class.java) }
                }
            }
    }


    suspend fun getMyEvents(): List<Event> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        return firestore.collection("events")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Event::class.java)
    }
    // ---------------------------------------------------------
    // 🔥 MPESA PAYMENT
    // ---------------------------------------------------------
    fun initiateMpesaPayment(phone: String, amount: Int, eventName: String, ticketType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val authString = android.util.Base64.encodeToString(
                    "${MpesaConfig.CONSUMER_KEY}:${MpesaConfig.CONSUMER_SECRET}".toByteArray(),
                    android.util.Base64.NO_WRAP
                )

                val tokenResponse = RetrofitClient.instance
                    .getAccessToken("Basic $authString")
                    .execute()

                if (!tokenResponse.isSuccessful) return@launch

                val token = tokenResponse.body()?.access_token ?: return@launch

                val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                val password = android.util.Base64.encodeToString(
                    "${MpesaConfig.SHORTCODE}${MpesaConfig.PASSKEY}$timestamp".toByteArray(),
                    android.util.Base64.NO_WRAP
                )

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

                RetrofitClient.instance.stkPush("Bearer $token", stkRequest).execute()

            } catch (e: Exception) {
                println("MPESA ERROR: ${e.localizedMessage}")
            }
        }
    }
}

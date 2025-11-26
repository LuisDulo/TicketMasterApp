package com.example.ticketmasterapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketmasterapp.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    }
}

package com.example.ticketmasterapp.repository

import com.example.ticketmasterapp.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventRepository {

    private val db = FirebaseFirestore.getInstance()
    private val eventRef = db.collection("events")

    suspend fun addEvent(event: Event) {
        val id = eventRef.document().id
        eventRef.document(id).set(event.copy(id = id)).await()
    }

    suspend fun getEvents(): List<Event> {
        return eventRef.get().await().toObjects(Event::class.java)
    }

    suspend fun getEventById(id: String): Event? {
        return eventRef.document(id).get().await().toObject(Event::class.java)
    }
}

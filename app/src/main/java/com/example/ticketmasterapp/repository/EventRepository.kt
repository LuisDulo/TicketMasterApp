package com.example.ticketmasterapp.repository

import com.example.ticketmasterapp.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventRepository {

    private val eventsRef = FirebaseFirestore.getInstance().collection("events")

    // Save event
    suspend fun saveEvent(event: Event) {
        eventsRef.document(event.id).set(event).await()
    }

    // Get all events
    suspend fun getEvents(): List<Event> {
        return eventsRef.get().await().toObjects(Event::class.java)
    }

    // Get one event
    suspend fun getEvent(eventId: String): Event? {
        return eventsRef.document(eventId).get().await().toObject(Event::class.java)
    }
}

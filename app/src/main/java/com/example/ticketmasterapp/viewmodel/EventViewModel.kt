package com.example.ticketmasterapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketmasterapp.models.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class EventViewModel : ViewModel() {

    // StateFlow to hold the list of events
    private val _eventList = MutableStateFlow<List<Event>>(emptyList())
    val eventList: StateFlow<List<Event>> = _eventList

    /**
     * Save a new event with all details including description and all price tiers.
     */
    fun saveEvent(
        title: String,
        date: String,
        time: String,
        location: String,
        regularPrice: String,
        vipPrice: String,
        vvipPrice: String,
        imageUrl: String,
        description: String
    ) {
        val event = Event(
            id = UUID.randomUUID().toString(),
            name = title,
            category = "Music",
            date = "$date at $time",
            location = location,
            description = description,
            regularPrice = regularPrice.replace("ksh", "").trim().toDoubleOrNull() ?: 0.0,
            vipPrice = vipPrice.replace("ksh", "").trim().toDoubleOrNull() ?: 0.0,
            vvipPrice = vvipPrice.replace("ksh", "").trim().toDoubleOrNull() ?: 0.0,
            imageUrl = imageUrl
        )

        // Add the new event to the list
        viewModelScope.launch {
            _eventList.value = _eventList.value + event
        }
    }

    /**
     * Returns a Flow of a single event by its ID
     */
    fun getEvent(eventId: String) =
        eventList.map { list -> list.find { it.id == eventId } }
}

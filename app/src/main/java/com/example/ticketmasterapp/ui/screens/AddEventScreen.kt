package com.example.ticketmasterapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketmasterapp.models.Event
import com.example.ticketmasterapp.viewmodel.EventViewModel
import java.util.UUID

@Composable
fun AddEventScreen(
    viewModel: EventViewModel,
    onEventAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var regularPrice by remember { mutableStateOf("") }
    var vipPrice by remember { mutableStateOf("") }
    var vvipPrice by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Add Event", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
        OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") })
        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
        OutlinedTextField(value = regularPrice, onValueChange = { regularPrice = it }, label = { Text("Regular Price (ksh)") })
        OutlinedTextField(value = vipPrice, onValueChange = { vipPrice = it }, label = { Text("VIP Price (ksh)") })
        OutlinedTextField(value = vvipPrice, onValueChange = { vvipPrice = it }, label = { Text("VVIP Price (ksh)") })
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.height(100.dp)
        )

        Button(
            onClick = {
                println("🚀 Save button clicked")

                val eventId = UUID.randomUUID().toString()

                val event = Event(
                    id = eventId,
                    name = title,
                    category = "Music",
                    date = "$date at $time",
                    location = location,
                    description = description,
                    regularPrice = regularPrice.toDoubleOrNull() ?: 0.0,
                    vipPrice = vipPrice.toDoubleOrNull() ?: 0.0,
                    vvipPrice = vvipPrice.toDoubleOrNull() ?: 0.0,
                    imageUrl = imageUrl
                )

                viewModel.addEvent(
                    event = event,
                    onSuccess = {
                        println("🎉 Saved successfully")
                        onEventAdded()
                    },
                    onFailure = {
                        println("❌ Save failed")
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Event")
        }

    }
}

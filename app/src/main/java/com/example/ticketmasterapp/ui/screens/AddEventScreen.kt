package com.example.ticketmasterapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketmasterapp.viewmodel.EventViewModel

@Composable
fun AddEventScreen(
    viewModel: EventViewModel,
    onEventAdded: () -> Unit
) {
    // Form fields
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var regularPrice by remember { mutableStateOf("") }
    var vipPrice by remember { mutableStateOf("") }
    var vvipPrice by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Scroll state to make the form scrollable
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState), // Enables scrolling
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Add Event", style = MaterialTheme.typography.headlineMedium)

        // Input fields
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") }
        )
        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time") }
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") }
        )
        OutlinedTextField(
            value = regularPrice,
            onValueChange = { regularPrice = it },
            label = { Text("Regular Price (ksh)") }
        )
        OutlinedTextField(
            value = vipPrice,
            onValueChange = { vipPrice = it },
            label = { Text("VIP Price (ksh)") }
        )
        OutlinedTextField(
            value = vvipPrice,
            onValueChange = { vvipPrice = it },
            label = { Text("VVIP Price (ksh)") }
        )
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL") }
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .height(100.dp)
        )

        // Save button
        Button(
            onClick = {
                viewModel.saveEvent(
                    title, date, time, location,
                    regularPrice, vipPrice, vvipPrice,
                    imageUrl, description
                )
                onEventAdded()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Event")
        }
    }
}

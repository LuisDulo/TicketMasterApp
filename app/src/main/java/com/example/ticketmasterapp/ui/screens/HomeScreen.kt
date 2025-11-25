package com.example.ticketmasterapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ticketmasterapp.models.Event
import com.example.ticketmasterapp.viewmodel.EventViewModel

@Composable
fun HomeScreen(
    navToAddEvent: () -> Unit,
    navToEventDetails: (String) -> Unit,
    viewModel: EventViewModel
) {
    val events = viewModel.eventList.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = navToAddEvent) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->

        Column(Modifier.padding(padding).padding(16.dp)) {

            Text("Browse Events", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(events.value) { event ->
                    EventCard(event) { navToEventDetails(event.id) }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() }
    ) {
        Row {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.name,
                modifier = Modifier.size(100.dp)
            )

            Column(Modifier.padding(8.dp)) {
                Text(event.name, fontWeight = FontWeight.Bold)
                Text(event.location)
                Text("From $${event.regularPrice}")
            }
        }
    }
}

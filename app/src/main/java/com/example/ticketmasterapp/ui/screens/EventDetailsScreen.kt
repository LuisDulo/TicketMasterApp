package com.example.ticketmasterapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ticketmasterapp.models.Event
import com.example.ticketmasterapp.viewmodel.EventViewModel

@Composable
fun EventDetailsScreen(
    eventId: String,
    viewModel: EventViewModel
) {
    // Holds the loaded event
    var event by remember { mutableStateOf<Event?>(null) }

    // Load event from Firestore when screen opens
    LaunchedEffect(eventId) {
        event = viewModel.getEventFromFirestore(eventId)
    }

    event?.let { e ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Event image
            AsyncImage(
                model = e.imageUrl,
                contentDescription = e.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Event name
            Text(
                text = e.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Category
            Text(
                text = "Category: ${e.category}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            // Location & date
            Text("Location: ${e.location}", style = MaterialTheme.typography.bodyLarge)
            Text("Date: ${e.date}", style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(16.dp))

            // Ticket prices
            Text(
                "Ticket Prices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text("Regular: Ksh ${e.regularPrice}")
            Text("VIP: Ksh ${e.vipPrice}")
            Text("VVIP: Ksh ${e.vvipPrice}")

            Spacer(Modifier.height(20.dp))

            // Description
            Text(
                "Event Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(e.description)

            Spacer(Modifier.height(30.dp))

            // Buy Ticket Button
            Button(
                onClick = {
                    // TODO: Implement ticket purchase
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buy Ticket")
            }
        }
    } ?: run {
        // Loading state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            CircularProgressIndicator()
        }
    }
}

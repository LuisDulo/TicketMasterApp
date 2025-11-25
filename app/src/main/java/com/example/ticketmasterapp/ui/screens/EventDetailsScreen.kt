package com.example.ticketmasterapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ticketmasterapp.viewmodel.EventViewModel

@Composable
fun EventDetailsScreen(
    eventId: String,
    viewModel: EventViewModel
) {
    val event = viewModel.getEvent(eventId).collectAsState(initial = null)

    event.value?.let { e ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            AsyncImage(
                model = e.imageUrl,
                contentDescription = e.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(text = e.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = e.location)
            Text(text = e.date)

            Spacer(Modifier.height(12.dp))
            Text(text = e.description)

            Spacer(Modifier.height(30.dp))
            Button(
                onClick = { /* Handle ticket purchase */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buy Ticket")
            }
        }
    }
}

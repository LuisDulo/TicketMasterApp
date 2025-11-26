package com.example.ticketmasterapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketmasterapp.models.Ticket
import com.example.ticketmasterapp.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: EventViewModel,
    onBack: () -> Unit
) {
    // Collect tickets from StateFlow
    val tickets by viewModel.userTickets.collectAsState(initial = emptyList())
    var loading by remember { mutableStateOf(true) }

    // Start listening for tickets
    LaunchedEffect(Unit) {
        viewModel.loadUserTickets()
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                text = "My Tickets",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (tickets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("You haven't purchased any tickets yet.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tickets) { ticket ->
                        TicketCard(ticket)
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Event name
            Text(
                text = ticket.eventName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            // Ticket details
            Text("Ticket Type: ${ticket.ticketType}")
            Text("Quantity: ${ticket.count}")
            Text("Total: Ksh ${ticket.totalPrice}")
            Text("Purchased: ${ticket.datePurchased}")
        }
    }
}

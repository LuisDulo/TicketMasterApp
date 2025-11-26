package com.example.ticketmasterapp.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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

    // Currently selected ticket for QR dialog
    var selectedTicket by remember { mutableStateOf<Ticket?>(null) }

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
                        TicketCard(ticket) {
                            selectedTicket = it
                        }
                    }
                }
            }
        }
    }

    // QR Code Dialog
    if (selectedTicket != null) {
        AlertDialog(
            onDismissRequest = { selectedTicket = null },
            title = { Text("Your Ticket") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Event: ${selectedTicket!!.eventName}")
                    Text("Ticket Type: ${selectedTicket!!.ticketType}")
                    Text("Quantity: ${selectedTicket!!.count}")
                    Spacer(Modifier.height(16.dp))
                    // Decode and show QR code
                    val qrBitmap = Base64.decode(selectedTicket!!.qrBase64, Base64.DEFAULT)
                        .let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTicket = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun TicketCard(ticket: Ticket, onClick: (Ticket) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(ticket) },
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = ticket.eventName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text("Ticket Type: ${ticket.ticketType}")
            Text("Quantity: ${ticket.count}")
            Text("Total: Ksh ${ticket.totalPrice}")
            Text("Purchased: ${ticket.datePurchased}")
        }
    }
}

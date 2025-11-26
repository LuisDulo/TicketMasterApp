package com.example.ticketmasterapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ticketmasterapp.models.Event
import com.example.ticketmasterapp.viewmodel.EventViewModel
import com.example.ticketmasterapp.ui.components.DropdownMenuBox

@Composable
fun EventDetailsScreen(
    eventId: String,
    viewModel: EventViewModel,
    navController: NavController
) {
    var event by remember { mutableStateOf<Event?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    var selectedTicket by remember { mutableStateOf("Regular") }
    var phoneNumber by remember { mutableStateOf("") }
    var ticketCount by remember { mutableStateOf("1") }

    // Load event from Firestore
    LaunchedEffect(eventId) {
        event = viewModel.getEventFromFirestore(eventId)
    }

    event?.let { e ->

        // UI CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Event Image
            AsyncImage(
                model = e.imageUrl,
                contentDescription = e.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Title
            Text(
                text = e.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text("Category: ${e.category}")
            Spacer(Modifier.height(8.dp))

            Text("Location: ${e.location}")
            Text("Date: ${e.date}")

            Spacer(Modifier.height(16.dp))

            // Ticket Prices
            Text("Ticket Prices", fontWeight = FontWeight.Bold)
            Text("Regular: Ksh ${e.regularPrice}")
            Text("VIP: Ksh ${e.vipPrice}")
            Text("VVIP: Ksh ${e.vvipPrice}")

            Spacer(Modifier.height(20.dp))

            // Event Description
            Text("Event Description", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(e.description)

            Spacer(Modifier.height(30.dp))

            // Buy Button
            Button(
                onClick = { showPaymentDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buy Ticket")
            }
        }

        // --------------------------------------------------------------------
        // ✔ PAYMENT POPUP
        // --------------------------------------------------------------------
        if (showPaymentDialog) {
            AlertDialog(
                onDismissRequest = { showPaymentDialog = false },
                title = { Text("M-Pesa Checkout") },
                text = {

                    Column {

                        // Ticket Type
                        Text("Select Ticket Type", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))

                        DropdownMenuBox(
                            selected = selectedTicket,
                            items = listOf("Regular", "VIP", "VVIP"),
                            onSelect = { selectedTicket = it }
                        )

                        Spacer(Modifier.height(12.dp))

                        // Ticket count
                        OutlinedTextField(
                            value = ticketCount,
                            onValueChange = {
                                ticketCount = it.filter { c -> c.isDigit() }
                            },
                            label = { Text("Number of Tickets") },
                            singleLine = true
                        )

                        Spacer(Modifier.height(12.dp))

                        // Phone number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Phone Number (07...)") },
                            singleLine = true
                        )

                        Spacer(Modifier.height(16.dp))

                        // Price Calculation
                        val price = when (selectedTicket) {
                            "Regular" -> e.regularPrice
                            "VIP" -> e.vipPrice
                            else -> e.vvipPrice
                        }

                        val count = ticketCount.toIntOrNull() ?: 1
                        val totalAmount = price * count

                        Text("Price per Ticket: Ksh $price")
                        Text("Tickets: $count")
                        Spacer(Modifier.height(6.dp))

                        Text(
                            "TOTAL: Ksh $totalAmount",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {

                            val price = when (selectedTicket) {
                                "Regular" -> e.regularPrice
                                "VIP" -> e.vipPrice
                                else -> e.vvipPrice
                            }

                            val count = ticketCount.toIntOrNull() ?: 1
                            val total = price * count

                            // Send STK Push
                            viewModel.initiateMpesaPayment(
                                phone = phoneNumber,
                                amount = total.toInt(),
                                eventName = e.name,
                                ticketType = selectedTicket
                            )

                            // Close mpesa window & open "Have you paid?" window
                            showPaymentDialog = false
                            showConfirmDialog = true
                        }
                    ) {
                        Text("Pay Now")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showPaymentDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // --------------------------------------------------------------------
        // ✔ CONFIRM PAYMENT POPUP
        // --------------------------------------------------------------------
        if (showConfirmDialog) {

            val price = when (selectedTicket) {
                "Regular" -> e.regularPrice
                "VIP" -> e.vipPrice
                else -> e.vvipPrice
            }
            val count = ticketCount.toIntOrNull() ?: 1
            val total = price * count

            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Payment Confirmation") },
                text = {
                    Text("Have you completed the M-Pesa payment?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // SAVE TICKET IN FIRESTORE
                            viewModel.saveTicketToFirestore(
                                event = e,
                                ticketType = selectedTicket,
                                count = count,
                                totalPrice = total.toInt()
                            ) {
                                showConfirmDialog = false
                                navController.navigate("profile")
                            }
                        }
                    ) {
                        Text("YES, I'VE PAID")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showConfirmDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

    } ?: run {

        // -------------------------------------------------
        // Loading state
        // -------------------------------------------------
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

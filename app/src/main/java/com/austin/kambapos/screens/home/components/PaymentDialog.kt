package com.austin.kambapos.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PaymentDialog(
    onDismiss: () -> Unit,
    onSelect: (PaymentMethod) -> Unit
) {

    val paymentInfo = listOf(
        "💵 Dinheiro" to PaymentMethod.CASH,
        "💳 Cartão" to PaymentMethod.CARD,
        "📱 QR Code" to PaymentMethod.QRCODE
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Forma de pagamento",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                paymentInfo.forEach { (label, method) ->
                    Button(
                        onClick = { onSelect(method) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}


enum class PaymentMethod(val apiValue: String) {
    CASH("cash"),
    CARD("card"),
    QRCODE("QrCode"),
    BANK_TRANSFER("BankTransfer")
}
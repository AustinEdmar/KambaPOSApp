package com.austin.kambapos.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.austin.kambapos.utils.toKwanza


@Composable
fun CashPaymentDialog(
    total: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit  // ✅ Double, Double
) {
    var receivedText by remember { mutableStateOf("") }

    val received = receivedText.toDoubleOrNull() ?: 0.0  // ✅ Double
    val change: Double = (received - total).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Pagamento em Dinheiro", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Text("Total: ${total.toKwanza()} ")

                OutlinedTextField(
                    value = receivedText,
                    onValueChange = { receivedText = it },
                    placeholder = {
                        Text(
                            text = "Valor recebido",
                            color = Color(0xFFAAAAAA),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFC107),
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Color(0xFFFFC107)
                    )
                )

                Text(
                    text = "Troco: ${change.toKwanza()} ",  // ✅ formatKzCart
                    fontWeight = FontWeight.Bold,
                    color = if (received >= total) Color(0xFF4CAF50) else Color.Red
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(received, change) },  // ✅ Double, Double
                enabled = received >= total
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
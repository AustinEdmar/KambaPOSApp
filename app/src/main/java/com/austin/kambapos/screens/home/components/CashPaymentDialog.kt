package com.austin.kambapos.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.utils.KwanzaVisualTransformation
import com.austin.kambapos.utils.formatToKwanza
import com.austin.kambapos.utils.parseKwanzaToDouble
import com.austin.kambapos.utils.toKwanza
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CashPaymentDialog(
    total: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {
    var receivedDigits by remember { mutableStateOf("") }

    val received = parseKwanzaToDouble(formatToKwanza(receivedDigits))
    val change = (received - total).coerceAtLeast(0.0)




    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // ─────────────────────────────────────────────
                // HEADER
                // ─────────────────────────────────────────────

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp
                                )
                            )
                            .background(
                                brush = SunmiGradient
                            ),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp
                        ),
                        color = Color.Transparent
                    ) {}

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Ícone
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "Pagamento em dinheiro",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Título
                        Text(
                            text = "PAGAMENTO",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Fechar
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End
                        ) {

                            TextButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Cancelar",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }

                // ─────────────────────────────────────────────
                // CORPO
                // ─────────────────────────────────────────────

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 20.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        text = "Pagamento em dinheiro",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )

                    // ─────────────────────────────────────────
                    // TOTAL
                    // ─────────────────────────────────────────

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF7F7F7))
                            .padding(
                                horizontal = 14.dp,
                                vertical = 12.dp
                            )
                    ) {

                        Text(
                            text = "Total a pagar",
                            fontSize = 12.sp,
                            color = Color(0xFF777777)
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = total.toKwanza(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF222222)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    // ─────────────────────────────────────────
                    // VALOR RECEBIDO
                    // ─────────────────────────────────────────

                    Text(
                        text = "Valor recebido",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )

                    OutlinedTextField(
                        value = receivedDigits,
                        onValueChange = { input ->
                            receivedDigits = input.filter { it.isDigit() }
                        },
                        visualTransformation = KwanzaVisualTransformation(),
                        placeholder = {
                            Text(text = "0,00 Kz", color = Color(0xFFAAAAAA), fontSize = 14.sp)
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

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    // ─────────────────────────────────────────
                    // TROCO
                    // ─────────────────────────────────────────

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Troco",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF555555)
                        )

                        Text(
                            text = change.toKwanza(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (received >= total) {
                                Color(0xFF4CAF50)
                            } else {
                                Color.Red
                            }
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    // ─────────────────────────────────────────
                    // CONFIRMAR
                    // ─────────────────────────────────────────

                    Button(
                        onClick = {
                            onConfirm(received, change)
                        },
                        enabled = received >= total,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                brush = SunmiGradient
                            ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color(0xFFE0E0E0),
                            contentColor = Color.White,
                            disabledContentColor = Color(0xFF999999)
                        )
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Confirmar pagamento",
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(
                                modifier = Modifier.size(8.dp)
                            )

                            Text(
                                text = "Confirmar pagamento",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
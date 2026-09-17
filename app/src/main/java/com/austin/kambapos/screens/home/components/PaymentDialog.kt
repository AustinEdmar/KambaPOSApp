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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.austin.kambapos.screens.home.components.Orders.PaymentMethodItem
import com.austin.kambapos.ui.theme.SunmiGradient

@Composable
fun PaymentDialog(
    onDismiss: () -> Unit,
    onSelect: (PaymentMethod) -> Unit
) {

    val paymentInfo = listOf(
        Triple(
            "Dinheiro",
            "Pagamento em numerário",
            PaymentMethod.CASH
        ),
        Triple(
            "Cartão",
            "Pagamento com cartão",
            PaymentMethod.CARD
        ),
        Triple(
            "QR Code",
            "Pagamento através de QR Code",
            PaymentMethod.QRCODE
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true,
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

                        // Espaço esquerdo
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        // Título
                        Text(
                            text = "FORMA DE PAGAMENTO",
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
                                    contentDescription = "Fechar",
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
                        text = "Selecione como o cliente irá pagar",
                        fontSize = 13.sp,
                        color = Color(0xFF555555)
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    paymentInfo.forEach { (title, description, method) ->

                        PaymentMethodItem(
                            title = title,
                            description = description,
                            icon = when (method) {
                                PaymentMethod.CASH ->
                                    Icons.Default.Money

                                PaymentMethod.CARD ->
                                    Icons.Default.CreditCard

                                PaymentMethod.QRCODE ->
                                    Icons.Default.QrCode2

                                PaymentMethod.BANK_TRANSFER ->
                                    Icons.Default.AccountBalance
                            },
                            onClick = {
                                onSelect(method)
                            }
                        )
                    }
                }
            }
        }
    }
}
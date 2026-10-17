package com.austin.kambapos.screens.home.components.Orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.austin.kambapos.core.lib.SunmiPrinter
import com.austin.kambapos.data.local.entity.OrderWithItems
import com.austin.kambapos.ui.theme.DividerColor
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.ui.theme.TextMain
import com.austin.kambapos.ui.theme.TextMuted
import com.austin.kambapos.utils.toKwanza

@Composable
fun OrderDetailsDialog(
    orderWithItems: OrderWithItems,
    onDismiss: () -> Unit,
    reprintEnabled: Boolean = true,
    onReprint: () -> Unit
) {
    val order = orderWithItems.order
    val items = orderWithItems.items



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

                        // Ícone à esquerda
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "Venda",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Título
                        Text(
                            text = "VENDA #${order.id}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Botão fechar
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
                        )
                        .heightIn(max = 500.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Data da venda
                    Text(
                        text = order.created_at
                            ?.take(16)
                            ?.replace("T", "  ")
                            ?: "–",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    HorizontalDivider(
                        color = DividerColor
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    // Título dos itens
                    Text(
                        text = "Itens",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF333333)
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    // ─────────────────────────────────────────
                    // LISTA DE ITENS
                    // ─────────────────────────────────────────

                    if (items.isEmpty()) {

                        Text(
                            text = "Sem itens registados",
                            fontSize = 13.sp,
                            color = TextMuted
                        )

                    } else {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(
                                    1f,
                                    fill = false
                                ),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            items(
                                items = items,
                                key = { it.id }
                            ) { item ->

                                Column {

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = "${item.quantity}x ${
                                                item.product_name_local
                                                    ?: item.product_name
                                                    ?: "–"
                                            }",
                                            fontSize = 13.sp,
                                            color = TextMain,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(
                                            modifier = Modifier.width(12.dp)
                                        )

                                        Text(
                                            text = item.total_with_iva.toKwanza(),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextMain
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier.height(2.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${item.unit_price.toKwanza()} un.",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )

                                        Text(
                                            text = "|",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )

                                        Text(
                                            text = "iva: ${item.iva_rate.toInt()}%",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }


                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    HorizontalDivider(
                        color = DividerColor
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    // ─────────────────────────────────────────
                    // RESUMO
                    // ─────────────────────────────────────────

                    SummaryRow(
                        label = "IVA",
                        value = order.iva.toKwanza(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        valueColor = Color.Gray
                    )

                    SummaryRow(
                        label = "Subtotal",
                        value = order.subtotal.toKwanza(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        valueColor = Color.Gray
                    )

                    SummaryRow(
                        label = "Total",
                        value = order.total.toKwanza(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        valueColor = Color.Blue
                    )


                    if (orderWithItems.order.status != "open") {
                        Button(
                            onClick = onReprint,
                            enabled = reprintEnabled,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(6.dp))
                            Text("Imprimir talão", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
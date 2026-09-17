package com.austin.kambapos.screens.home.components.Orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.austin.kambapos.data.local.entity.OrderEntity
import com.austin.kambapos.ui.theme.CardBg
import com.austin.kambapos.ui.theme.DividerColor
import com.austin.kambapos.ui.theme.TextMuted

import com.austin.kambapos.utils.toKwanza

@Composable
 fun OrderCard(order: OrderEntity, onClick: () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = "Venda ID #${order.id.toString().uppercase()}",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
                //  StatusBadge(order.status)
            }

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text     = order.created_at?.take(16)?.replace("T", "  ") ?: "–",
                    fontSize = 12.sp,
                    color    = TextMuted
                )
                Text("|", fontSize = 12.sp, color = TextMuted)

                val isOpen = order.status == "open"

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = if (isOpen) "Aberto" else "Fechado",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOpen) Color(0xFF3DDBBF) else Color(0xFFF53C3C),
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }


            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
            Spacer(Modifier.height(10.dp))
            //"${shift?.initialAmount.toKwanza()} "
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
            // SummaryRow("Método Pagamento", statusLabel(order.status))
        }
    }
}
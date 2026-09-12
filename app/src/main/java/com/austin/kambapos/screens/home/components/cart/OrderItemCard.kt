package com.austin.kambapos.screens.home.components.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.austin.kambapos.data.api.CoreUrl
import com.austin.kambapos.data.model.CartItem

@Composable
fun OrderItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    increaseEnabled: Boolean,
    decreaseEnabled: Boolean
) {
    val fullImageUrl = when {
        item.imageUrl == null -> null
        item.imageUrl.startsWith("http") -> item.imageUrl
        else -> "${CoreUrl.STORAGE_URL}${item.imageUrl.removePrefix("/")}"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            0.7.dp,
            Color(0xFFE5E5E5)
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 15.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = fullImageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEEEEEE))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(1.dp))
                Row {
                    Text("${item.stockLabel} ", fontSize = 12.sp, color = Color.Gray)
                    Text("${item.stockQty},  ", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.SemiBold)
                    Text("Preço: ${item.unitPrice} kz", fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(1.dp))
                Row {
                    Text("Preço total:  ", fontSize = 12.sp, color = Color.Gray)
                    Text("${item.unitPrice * item.quantity} KZ", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Row {
                    Text("Iva ao produto: ", fontSize = 12.sp, color = Color.Gray)
                    Text("${item.iva} %", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))

            QuantityStepperCart(
                quantity = item.quantity,
                onIncrease = onIncrease,
                onDecrease = onDecrease,
                decreaseEnabled = decreaseEnabled,
                increaseEnabled = increaseEnabled
            )
        }
    }
}

package com.austin.kambapos.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

import com.austin.kambapos.data.local.entity.ProductEntity
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.data.api.CoreUrl

@Composable
fun ProductItemCard(
    product: ProductEntity? = null,
    onClick: () -> Unit
) {
    val fullImageUrl = when {
        product?.imageUrl == null -> null
        product.imageUrl.startsWith("http") -> product.imageUrl
        else -> "${CoreUrl.STORAGE_URL}${product.imageUrl.removePrefix("/")}"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 🔥 Imagem ocupa todo card
            AsyncImage(
                model = fullImageUrl,
                contentDescription = product?.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            // 🔥 Fundo escuro com opacidade (igual da imagem)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        color = Color.Black.copy(alpha = 0.4f)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                product?.name?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            // 🔥 Badge de preço (mantido como pediu)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .background(brush = SunmiGradient,
                        RoundedCornerShape(12.dp))

                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${product?.price?.toInt()} Kz",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

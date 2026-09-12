package com.austin.kambapos.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.austin.kambapos.data.api.CoreUrl
import com.austin.kambapos.ui.theme.SunmiGradient

@Composable
fun CategoryChip(
    icon: String? = null,
    iconM: ImageVector? = null,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fullImageUrl = when {
        icon == null -> null
        icon.startsWith("http") -> icon
        else -> "${CoreUrl.STORAGE_URL}${icon.removePrefix("/")}"
    }

    val shape = RoundedCornerShape(16.dp)

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // ✅ sempre transparente, sem conflito de sombra
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = Color.LightGray.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 0.dp else 5.dp // sem sombra quando tem gradiente
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .then(
                    if (isSelected) {
                        Modifier.background(brush = SunmiGradient, shape = shape)
                    } else {
                        Modifier.background(color = MaterialTheme.colorScheme.surface, shape = shape)
                    }
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            fullImageUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = text,
                    modifier = Modifier.size(24.dp)
                )
            }

            iconM?.let {
                Icon(
                    imageVector = it,
                    contentDescription = text,
                    modifier = Modifier.size(18.dp),
                )
            }

            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
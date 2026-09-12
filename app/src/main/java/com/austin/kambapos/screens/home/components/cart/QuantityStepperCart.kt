package com.austin.kambapos.screens.home.components.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.ui.theme.SunmiOrange

@Composable
fun QuantityStepperCart(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    decreaseEnabled: Boolean,
    increaseEnabled: Boolean
) {
    Column {
        OutlinedIconButton(
            onClick = onDecrease,
            modifier = Modifier.size(30.dp),
            enabled = decreaseEnabled,
            shape = CircleShape,
            border = BorderStroke(1.5.dp, SunmiOrange),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = SunmiOrange
            )
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Diminuir", modifier = Modifier.size(14.dp))
        }

        Spacer(modifier = Modifier.padding(top = 2.dp))

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(brush = SunmiGradient), // ✅ gradiente aqui
            contentAlignment = Alignment.Center
        ) {
            Text(quantity.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.padding(top = 2.dp))
        OutlinedIconButton(
            onClick = onIncrease,
            enabled = increaseEnabled,
            modifier = Modifier.size(30.dp),
            shape = CircleShape,
            border = BorderStroke(1.5.dp, SunmiOrange),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = SunmiOrange
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(14.dp))
        }
    }
}
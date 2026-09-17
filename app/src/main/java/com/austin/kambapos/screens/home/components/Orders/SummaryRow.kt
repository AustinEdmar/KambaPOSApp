package com.austin.kambapos.screens.home.components.Orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.austin.kambapos.ui.theme.TextMuted


@Composable
fun SummaryRow(
    label: String,
    value: String,
    fontSize: TextUnit = 13.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    valueColor: Color = Color.Blue
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            color = TextMuted
        )

        Text(
            text = value,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = valueColor
        )
    }
}
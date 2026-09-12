package com.austin.kambapos.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.austin.kambapos.data.local.entity.TableEntity


@Composable
fun TableItem(
    table: TableEntity,
    //isSelected: Boolean,
    onTableClick: (TableEntity) -> Unit
) {
    val backgroundColor = when (table.status) {
        "available" -> Color(0xFF4CAF50)   // verde
        "busy" -> Color(0xFFFFC107)        // amarel
        "reserved" -> Color(0xFFF44336)    // red
        else -> Color.Gray
    }



   // val borderColor = if (isSelected) Color(0xFFF44336) else Color.Transparent

    Box(
        modifier = Modifier
            .aspectRatio(1f)
           //// .border(3.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onTableClick(
                table
            ) },

        contentAlignment = Alignment.Center
    ) {
        // Table shape with chairs
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top chairs
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(8.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left chair
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(40.dp)
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                )

                // Table center
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(backgroundColor, RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = table.number,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Right chair
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(40.dp)
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                )
            }

            // Bottom chairs
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(8.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
            )
        }
    }
}

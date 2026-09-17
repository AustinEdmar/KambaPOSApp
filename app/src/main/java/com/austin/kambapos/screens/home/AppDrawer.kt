package com.austin.kambapos.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.austin.kambapos.R
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.ui.theme.SunmiOrange

@Composable
fun AppDrawer(
    currentRoute: String = "home",
    navController: NavController,          // 👈 adicionado
    onCloseDrawer: () -> Unit              // 👈 renomeado para ficar claro
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = SunmiGradient)
                .padding(start = 10.dp, top = 30.dp, bottom = 5.dp )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 🔥 Logo
                AsyncImage(
                    model = R.drawable.logo,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(64.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // 🔥 Textos
                Column {

                    Row {
                        Text(
                            text = "Kamba",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Text(
                            text = "POS",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary // ✅ contraste sobre o gradiente
                            )
                        )
                    }

                    Text(
                        text = "PONTO DE VENDA",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val itemShape = RoundedCornerShape(12.dp)

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (currentRoute == "home") SunmiOrange else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (currentRoute == "home") FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selected = currentRoute == "home",
            onClick = {
                onCloseDrawer()
                navController.navigate("home")   // 👈
            },
            shape = itemShape,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = SunmiOrange.copy(alpha = 0.12f),
                selectedIconColor = SunmiOrange,
                selectedTextColor = SunmiOrange,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = "Pedidos",
                    tint = if (currentRoute == "orders") SunmiOrange else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = "Minhas vendas",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (currentRoute == "orders") FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selected = currentRoute == "orders",
            onClick = {
                onCloseDrawer()
                navController.navigate("orders") // 👈
            },
            shape = itemShape,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = SunmiOrange.copy(alpha = 0.12f),
                selectedIconColor = SunmiOrange,
                selectedTextColor = SunmiOrange,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    tint = if (currentRoute == "profile") SunmiOrange else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = "Perfil",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (currentRoute == "profile") FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selected = currentRoute == "profile",
            onClick = {
                onCloseDrawer()
                navController.navigate("profile") // 👈
            },
            shape = itemShape,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = SunmiOrange.copy(alpha = 0.12f),
                selectedIconColor = SunmiOrange,
                selectedTextColor = SunmiOrange,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Text(
            text = "Versão 1.2",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp, start = 16.dp, top = 12.dp)
        )
    }
}
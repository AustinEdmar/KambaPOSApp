package com.austin.kambapos.screens.home.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.austin.kambapos.ui.theme.SunmiGradient


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppTopBar(
    title: String?,
    amountTitle: String?,
    showMenu: Boolean = true,
    showCart: Boolean = false,
    showSearch: Boolean = false,
    showScanner: Boolean = false,
    showProfile: Boolean = false,
    cartCount: Int = 0,
    onMenuClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onScannerClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},

) {
    TopAppBar(
        modifier = Modifier

            .background(brush = SunmiGradient),
        title = {
            if (title != null  ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            // fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    if (amountTitle != null) {
                        Text(
                            text = amountTitle,
                            style = MaterialTheme.typography.titleSmall.copy(
                                // fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if (showMenu) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            if (showSearch) {
                IconButton( onClick = onSearchClick ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }
            if (showScanner) {
                IconButton( onClick = onScannerClick ) {
                    Icon(
                        Icons.Default.DocumentScanner,
                        contentDescription = "Scanner",
                        tint = Color.White
                    )
                }
            }
            if (showCart) {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(48.dp)
                ) {

                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge {

                                        Text(if (cartCount > 99) "99+" else cartCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Carrinho",
                            tint = Color.White
                        )
                    }
                }
            }

            if (showProfile) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Perfil",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

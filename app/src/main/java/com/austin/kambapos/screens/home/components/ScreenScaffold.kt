package com.austin.kambapos.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.austin.kambapos.screens.home.AppDrawer
import com.austin.kambapos.ui.theme.SunmiGradient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    navController: NavController,          // 👈 adicionado
    title: String? = null,
    amountTitle: String? = null,
    showMenu: Boolean = true,
    showSearch: Boolean = false,
    showScanner: Boolean = false,
    showCart: Boolean = false,
    showProfile: Boolean = false,
    cartCount: Int = 0,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }, // ✅ tipo explícito
    onCartClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,         // 👈
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier
                .background( Color(0xFF3DDBBF)), // gradiente aplicado aqui
            topBar = {
                MyAppTopBar(
                    title = title,
                    amountTitle = amountTitle,
                    showMenu = showMenu,
                    showCart = showCart,
                    showSearch = showSearch,
                    showScanner = showScanner,
                    showProfile = showProfile,
                    cartCount = cartCount,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onCartClick = onCartClick,
                    onSearchClick = onSearchClick,
                    onProfileClick = onProfileClick
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent // deixa transparente para o gradiente do modifier aparecer
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}
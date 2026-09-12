package com.austin.kambapos.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.austin.kambapos.data.model.UiStates.AuthUiState
import com.austin.kambapos.data.model.UiStates.ShitftUiState
import com.austin.kambapos.data.model.UiStates.UserUiState
import com.austin.kambapos.navigation.Screens
import com.austin.kambapos.screens.home.components.ScreenScaffold
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.utils.toKwanza
import com.austin.kambapos.viewmodel.AuthViewModel
import com.austin.kambapos.viewmodel.ChatViewModel   // 👈 novo
import com.austin.kambapos.viewmodel.HomeViewModel
import com.austin.kambapos.viewmodel.OrderViewModel
import com.austin.kambapos.viewmodel.ShiftViewModel


@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    AuthviewModel: AuthViewModel = hiltViewModel(),
    HomeViewModel: HomeViewModel = hiltViewModel(),
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),  // 👈 novo

    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    isConnected: Boolean
) {
    val uiState = AuthviewModel.uiState
    val shift by shiftViewModel.shift.collectAsState()
    val userState = HomeViewModel.uiState
    val total by orderViewModel.totalSales.collectAsState()
    val shiftUiState = shiftViewModel.shitftUiState
    val connectionStatus = if (isConnected) "Conectado" else "Desconectado"
    val snackbarHostState = remember { SnackbarHostState() }
    var showCloseShiftDialog by remember { mutableStateOf(false) }

    // 👈 novo — estado do chat
    val messages by chatViewModel.messages.collectAsState()
    val chatListState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) chatListState.animateScrollToItem(messages.size - 1)
    }

    LaunchedEffect(userState) {
        if (userState is UserUiState.Success) {
            orderViewModel.setUserId(userState.user.id)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Unauthenticated) {
            navController.navigate(Screens.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is UserUiState.Success) {
            orderViewModel.setUserId(uiState.user.id)
        }
    }

    LaunchedEffect(shiftUiState) {
        when (shiftUiState) {
            is ShitftUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (shiftUiState as ShitftUiState.Error).message,
                    duration = SnackbarDuration.Long
                )
            }
            is ShitftUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = "Turno fechado com sucesso!",
                    duration = SnackbarDuration.Short
                )
            }
            else -> {}
        }
    }

    val isLoading = uiState is AuthUiState.Loading
    val userId = (userState as? UserUiState.Success)?.user?.id

    ScreenScaffold(
        title = "Caixa: ${shift?.userName ?: "Nenhum"}",
        amountTitle = "Fundo: ${shift?.initialAmount.toKwanza()}",
        showMenu = true,
        showCart = false,
        showProfile = false,
        onCartClick = {},
        snackbarHostState = snackbarHostState,
        navController = navController,
        onProfileClick = onProfileClick
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Profile Header Card ──────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0x18000000),
                        spotColor = Color(0x18000000)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8EEFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        when (val state = HomeViewModel.uiState) {
                            is UserUiState.Loading -> {
                                Text(
                                    "Carregando...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF9E9E9E)
                                )
                            }
                            is UserUiState.Success -> {
                                Text(
                                    text = state.user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                state.user.email?.let {
                                    Text(
                                        text = it,
                                        fontSize = 13.sp,
                                        color = Color(0xFF9E9E9E)
                                    )
                                }
                            }
                            is UserUiState.Error -> {
                                Text(
                                    "Erro: ${state.message}",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Text(
                            text = "Caixa",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Chat Card ────────────────────────────────────────────────────  👈 novo
//            Card(
//                shape = RoundedCornerShape(20.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(280.dp)
//                    .shadow(
//                        elevation = 6.dp,
//                        shape = RoundedCornerShape(20.dp),
//                        ambientColor = Color(0x18000000),
//                        spotColor = Color(0x18000000)
//                    )
//            ) {
//                Column(modifier = Modifier.fillMaxSize()) {
//
//                    // cabeçalho do chat
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(MaterialTheme.colorScheme.primary)
//                            .padding(horizontal = 14.dp, vertical = 10.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            Icons.Default.Chat,
//                            contentDescription = null,
//                            tint = Color.White,
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Chat da equipa",
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = Color.White,
//                            modifier = Modifier.weight(1f)
//                        )
//                        Text(
//                            text = "${messages.size} msgs",
//                            fontSize = 11.sp,
//                            color = Color.White.copy(alpha = 0.7f)
//                        )
//                    }
//
//                    // lista de mensagens
//                    if (messages.isEmpty()) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(Color(0xFFF8F9FA)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = "Nenhuma mensagem ainda...",
//                                fontSize = 13.sp,
//                                color = Color(0xFF9E9E9E)
//                            )
//                        }
//                    } else {
//                        LazyColumn(
//                            state = chatListState,
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(Color(0xFFF8F9FA))
//                                .padding(horizontal = 12.dp),
//                            verticalArrangement = Arrangement.spacedBy(8.dp),
//                            contentPadding = PaddingValues(vertical = 10.dp)
//                        ) {
//                            items(messages) { msg ->
//                                Column(modifier = Modifier.fillMaxWidth()) {
//                                    Text(
//                                        text = msg.user,
//                                        fontSize = 11.sp,
//                                        fontWeight = FontWeight.SemiBold,
//                                        color = MaterialTheme.colorScheme.primary
//                                    )
//                                    Spacer(modifier = Modifier.height(2.dp))
//                                    Surface(
//                                        shape = RoundedCornerShape(4.dp, 12.dp, 12.dp, 12.dp),
//                                        color = Color.White,
//                                        shadowElevation = 1.dp
//                                    ) {
//                                        Text(
//                                            text = msg.message,
//                                            fontSize = 13.sp,
//                                            modifier = Modifier.padding(
//                                                horizontal = 10.dp,
//                                                vertical = 6.dp
//                                            )
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
            // ── fim Chat Card ────────────────────────────────────────────────

            Spacer(modifier = Modifier.padding(20.dp))

            // ── Close shift button ───────────────────────────────────────────
            OutlinedButton(
                onClick = { showCloseShiftDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)) // ✅ recorta antes de pintar
                    .background(brush = SunmiGradient),
                shape = RoundedCornerShape(10.dp),
                border = null,
                elevation = ButtonDefaults.buttonElevation(0.dp), // ✅ evita sombra azulada por cima do gradiente
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Icon(
                    Icons.Outlined.LockClock,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Fechar o Turno", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Logout button ────────────────────────────────────────────────
            Button(
                onClick = { AuthviewModel.logout() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.error,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Outlined.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sair", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Printer status pill ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isConnected) Color(0xFF4CAF50) else Color(0xFFE53935)
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Impressora: $connectionStatus",
                    fontSize = 13.sp,
                    color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFE53935)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ── Dialog de confirmação ────────────────────────────────────────────
        if (showCloseShiftDialog) {
            AlertDialog(
                onDismissRequest = { showCloseShiftDialog = false },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                icon = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Fechar o turno?",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Esta ação irá encerrar o turno actual e registar o total de vendas.",
                            fontSize = 13.sp,
                            color = Color(0xFF9E9E9E),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                ShiftSummaryRow(label = "Operador", value = shift?.userName ?: "—")
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    thickness = 0.5.dp,
                                    color = Color(0x20000000)
                                )
                                ShiftSummaryRow(
                                    label = "Fundo inicial",
                                    value = shift?.initialAmount.toKwanza()
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    thickness = 0.5.dp,
                                    color = Color(0x20000000)
                                )
                                ShiftSummaryRow(
                                    label = "Total de vendas",
                                    value = "${total.toKwanza()} ",
                                    valueColor = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showCloseShiftDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Cancelar")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCloseShiftDialog = false
                            userId?.let {
                                shiftViewModel.CloseShift(
                                    initial_amount = shift?.initialAmount,
                                    total,
                                    it
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)) // ✅ recorta antes de pintar
                            .background(brush = SunmiGradient),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp), // ✅ evita sombra azulada por cima do gradiente
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)

                    ) {
                        Text("Fechar turno", fontWeight = FontWeight.SemiBold)
                    }
                },
            )
        }
    }
}

@Composable
private fun ShiftSummaryRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Color(0xFF9E9E9E))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (valueColor == Color.Unspecified) Color.Unspecified else valueColor
        )
    }
}
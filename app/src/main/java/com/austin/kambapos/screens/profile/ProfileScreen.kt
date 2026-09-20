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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.austin.kambapos.viewmodel.ChatViewModel   // 👈 novo (chat em pausa, ver bloco comentado abaixo)
import com.austin.kambapos.viewmodel.HomeViewModel
import com.austin.kambapos.viewmodel.OrderViewModel
import com.austin.kambapos.viewmodel.ShiftViewModel

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
// Cores de suporte que não existem no color scheme por omissão do MaterialTheme.
// Mantidas centralizadas aqui para facilitar dark-mode/tema no futuro.
private val StatusOnline = Color(0xFF2E7D32)
private val StatusOffline = Color(0xFFC62828)
private val WarningContainer = Color(0xFFFFF3E0)
private val WarningOnContainer = Color(0xFFE65100)

@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    AuthviewModel: AuthViewModel = hiltViewModel(),
    HomeViewModel: HomeViewModel = hiltViewModel(),
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),  // 👈 novo (chat em pausa, ver bloco comentado abaixo)
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    isConnected: Boolean
) {
    val uiState = AuthviewModel.uiState
    val shift by shiftViewModel.shift.collectAsState()
    val userState = HomeViewModel.uiState
    val total by orderViewModel.totalSales.collectAsState()

    val shiftUiState = shiftViewModel.shitftUiState
    val snackbarHostState = remember { SnackbarHostState() }
    var showCloseShiftDialog by remember { mutableStateOf(false) }


    // 👈 novo — estado do chat (UI em pausa, ver bloco comentado abaixo)
    val messages by chatViewModel.messages.collectAsState()
    val chatListState = rememberLazyListState()

    // dentro de ProfileScreen:
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isClosingShift by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) chatListState.animateScrollToItem(messages.size - 1)
    }

    LaunchedEffect(userState) {
        if (userState is UserUiState.Success) {
            orderViewModel.setUserId(userState.user.id)
        }
    }

    LaunchedEffect(shift) {
        shift?.let {
            orderViewModel.setShiftId(it.id)   // ajuste o nome do campo se não for "id"
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Unauthenticated && !isLoggingOut) {
            navController.navigate(Screens.Login.route) {
                popUpTo(0) { inclusive = true }
            }
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            ProfileHeaderCard(userState = HomeViewModel.uiState)

            Spacer(modifier = Modifier.height(14.dp))

            PrinterStatusCard(isConnected = isConnected)

            // ── Chat Card ────────────────────────────────────────────────────  👈 novo (em pausa, retomar quando necessário)
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

            Spacer(modifier = Modifier.height(28.dp))

            SectionLabel("Turno")

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryGradientButton(
                text = "Fechar o Turno",
                icon = Icons.Outlined.LockClock,
                enabled = !isClosingShift && !isLoggingOut,   // 👈 bloqueia nos dois casos
                onClick = { showCloseShiftDialog = true }
            )

            Spacer(modifier = Modifier.height(28.dp))

            SectionLabel("Sessão")

            Spacer(modifier = Modifier.height(8.dp))

            LogoutButton(
                isLoading = isLoggingOut,
                onClick = { showLogoutDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showLogoutDialog) {
            LogoutConfirmDialog(
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    showLogoutDialog = false
                    isLoggingOut = true
                    AuthviewModel.logout() // destrói a sessão imediatamente

                    scope.launch {
                        delay(4000)
                        navController.navigate(Screens.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                        isLoggingOut = false
                    }
                }
            )
        }

        if (showCloseShiftDialog) {
            CloseShiftDialog(
                operatorName = shift?.userName,
                initialAmount = shift?.initialAmount.toKwanza(),
                totalSales = total.toKwanza(),
                onDismiss = { showCloseShiftDialog = false },
                onConfirm = {
                    showCloseShiftDialog = false
                    userId?.let {
                        shiftViewModel.CloseShift(
                            initial_amount = shift?.initialAmount,
                            total,
                            it
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(userState: UserUiState) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initials = (userState as? UserUiState.Success)?.user?.name
                ?.trim()
                ?.split(" ")
                ?.filter { it.isNotBlank() }
                ?.take(2)
                ?.joinToString("") { it.first().uppercase() }
                ?: "?"

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                when (userState) {
                    is UserUiState.Loading -> {
                        Text(
                            "Carregando...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is UserUiState.Success -> {
                        Text(
                            text = userState.user.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        userState.user.email?.let {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = it,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    is UserUiState.Error -> {
                        Text(
                            "Erro: ${userState.message}",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Caixa",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun PrinterStatusCard(isConnected: Boolean) {
    val statusColor = if (isConnected) StatusOnline else StatusOffline
    val statusLabel = if (isConnected) "Impressora conectada" else "Impressora desconectada"

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = statusColor.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = statusColor
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun PrimaryGradientButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,          // 👈 novo
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,            // 👈 novo
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = if (enabled) SunmiGradient
                else Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
            ),
        shape = RoundedCornerShape(14.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (enabled) LocalContentColor.current
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = if (enabled) LocalContentColor.current
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LogoutButton(isLoading: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.error,
                strokeWidth = 2.dp
            )
        } else {
            Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sair", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CloseShiftDialog(
    operatorName: String?,
    initialAmount: String,
    totalSales: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(WarningContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = WarningOnContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Fechar o turno?",
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Esta ação irá encerrar o turno actual e registar o total de vendas.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        ShiftSummaryRow(label = "Operador", value = operatorName ?: "—")
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        ShiftSummaryRow(label = "Fundo inicial", value = initialAmount)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        ShiftSummaryRow(
                            label = "Total de vendas",
                            value = totalSales,
                            valueColor = StatusOnline
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            Button(

                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(brush = SunmiGradient),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("Fechar turno", fontWeight = FontWeight.SemiBold)
            }
        }
    )
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
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor
        )
    }
}


@Composable
private fun LogoutConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(WarningContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null,
                    tint = WarningOnContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Terminar sessão?",
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "Vai precisar de iniciar sessão novamente para continuar a usar a aplicação.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Red),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("Sim, sair", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    )
}
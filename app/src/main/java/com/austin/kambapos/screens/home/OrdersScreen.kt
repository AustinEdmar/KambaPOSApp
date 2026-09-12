package com.austin.kambapos.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.austin.kambapos.data.local.entity.OrderEntity
import com.austin.kambapos.data.model.UiStates.AuthUiState
import com.austin.kambapos.data.model.UiStates.UserUiState
import com.austin.kambapos.navigation.Screens
import com.austin.kambapos.screens.home.components.ScreenScaffold
import com.austin.kambapos.utils.toKwanza
import com.austin.kambapos.viewmodel.AuthViewModel
import com.austin.kambapos.viewmodel.HomeViewModel
import com.austin.kambapos.viewmodel.OrderViewModel
import com.austin.kambapos.viewmodel.ShiftViewModel

// ── Palette ──────────────────────────────────────────────────────────────────
private val Blue      = Color(0xFF1565C0)
private val Green     = Color(0xFF43A047)
private val Orange    = Color(0xFFEF6C00)
private val Purple    = Color(0xFF8E24AA)
private val PageBg    = Color(0xFFF5F6FA)
private val CardBg    = Color(0xFFFFFFFF)
private val TextMain  = Color(0xFF1A1A2E)
private val TextMuted = Color(0xFF9E9E9E)
private val Divider   = Color(0xFFEEEEEE)

private val FILTERS = listOf("All Order", "New", "In Progress", "Ready", "Paid")

private fun statusColor(s: String) = when (s.trim().lowercase()) {
    "new"         -> Orange
    "in_progress" -> Blue
    "ready"       -> Purple
    "paid"        -> Green
    else          -> TextMuted
}

private fun statusLabel(s: String) = when (s.trim().lowercase()) {
    "new"         -> "New Order"
    "in_progress" -> "In Progress"
    "ready"       -> "Ready"
    "paid"        -> "Paid"
    else          -> s.replaceFirstChar { it.uppercase() }
}

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun OrdersScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit,
    AuthviewModel: AuthViewModel = hiltViewModel(),
    HomeViewModel: HomeViewModel = hiltViewModel(),
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
) {
    val uiState   = AuthviewModel.uiState
    val userState = HomeViewModel.uiState          // ← igual ao ProfileScreen
    val shift     by shiftViewModel.shift.collectAsState()
    val orders    by orderViewModel.orders.collectAsState()

    val total by orderViewModel.totalSales.collectAsState()
    val count by orderViewModel.ordersCount.collectAsState()



    // Redireciona se deslogado
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Unauthenticated) {
            navController.navigate(Screens.Login.route) { popUpTo(0) { inclusive = true } }
        }
    }

    // ✅ MESMO padrão do ProfileScreen — aguarda UserUiState.Success para ter o ID
    LaunchedEffect(userState) {
        if (userState is UserUiState.Success) {
            orderViewModel.setUserId(userState.user.id)
        }
    }

    var selectedFilter by remember { mutableStateOf("All Order") }

    val filtered = remember(orders, selectedFilter) {
        if (selectedFilter == "All Order") orders
        else orders.filter {
            it.status.trim().lowercase() == selectedFilter.lowercase().replace(" ", "_")
        }
    }

    ScreenScaffold(
        title = "Caixa: ${shift?.userName ?: "Nenhum"}",
        amountTitle = "Fundo: ${shift?.initialAmount.toKwanza()}",
        showMenu       = true,
        showCart       = true,
        showProfile    = false,
        onProfileClick = onProfileClick,
        navController  = navController,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {




            // ── Stats Card ───────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(
                        label = "Total Vendas",
                        value = "${total.toKwanza()} "
                    )
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    )
                    StatItem(
                        label = "Turno",
                        value = if (shift?.status == "open") "Aberto" else "Fechado"
                    )
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    )
                    StatItem(
                        label = "Maneio",
                        value = "${shift?.initialAmount.toKwanza()} "
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Result count ──────────────────────────────────────────
            Text(
                text       = "${filtered.size} Resultado(s)",
                modifier   = Modifier.padding(horizontal = 28.dp,  vertical = 2.dp),
                fontSize   = 13.sp,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

           // Spacer(Modifier.height(2.dp))

            // ── List / Empty state ────────────────────────────────────
            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum pedido encontrado", color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
                }
            } else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { order ->
                        OrderCard(order)
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}



// ── Order Card ────────────────────────────────────────────────────────────────
@Composable
private fun OrderCard(order: OrderEntity) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = "Pedido ID #${order.id.toString().uppercase()}",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
              //  StatusBadge(order.status)
            }

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text     = order.created_at?.take(16)?.replace("T", "  ") ?: "–",
                    fontSize = 12.sp,
                    color    = TextMuted
                )
                Text("|", fontSize = 12.sp, color = TextMuted)

            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Divider, thickness = 0.8.dp)
            Spacer(Modifier.height(10.dp))
       //"${shift?.initialAmount.toKwanza()} "
            SummaryRow("Subtotal",         "${order.subtotal.toKwanza()} ")
            SummaryRow("Total",            "${order.total.toKwanza()} ")
           // SummaryRow("Método Pagamento", statusLabel(order.status))
        }
    }
}

// ── Status Badge ──────────────────────────────────────────────────────────────
@Composable
private fun StatusBadge(status: String) {
    val color = statusColor(status)
    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primary) {
        Text(
            text       = statusLabel(status),
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = Color.White
        )
    }
}

// ── Summary Row ───────────────────────────────────────────────────────────────
@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextMuted)
        Text(value, fontSize = 13.sp, color = TextMain, fontWeight = FontWeight.Medium)
    }
}


@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}
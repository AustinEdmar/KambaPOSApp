package com.austin.kambapos.screens.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.austin.kambapos.core.lib.SunmiPrinter
import com.austin.kambapos.data.local.entity.OrderEntity
import com.austin.kambapos.data.local.entity.OrderWithItems
import com.austin.kambapos.data.local.mapper.toReceiptModel
import com.austin.kambapos.data.model.UiStates.AuthUiState
import com.austin.kambapos.data.model.UiStates.UserUiState
import com.austin.kambapos.navigation.Screens
import com.austin.kambapos.screens.home.components.Orders.OrderCard
import com.austin.kambapos.screens.home.components.Orders.OrderDetailsDialog
import com.austin.kambapos.screens.home.components.Orders.SummaryRow
import com.austin.kambapos.screens.home.components.ScreenScaffold

import com.austin.kambapos.utils.toKwanza
import com.austin.kambapos.viewmodel.AuthViewModel
import com.austin.kambapos.viewmodel.HomeViewModel
import com.austin.kambapos.viewmodel.OrderViewModel
import com.austin.kambapos.viewmodel.ShiftViewModel
import kotlinx.coroutines.launch

// ── Palette ──────────────────────────────────────────────────────────────────



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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val printer = remember { SunmiPrinter(context.applicationContext) }
    var printing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { printer.connectPrinter() }
    DisposableEffect(Unit) { onDispose { printer.disconnect() } }


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
            Log.d("ORDerid", "User ID: ${userState}")
        }
    }

    LaunchedEffect(shift) {
        shift?.let { orderViewModel.setShiftId(it.id) }
    }





    val ordersDetails by orderViewModel.ordersWithItems.collectAsState()



    var selectedOrder by remember { mutableStateOf<OrderWithItems?>(null) }
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
        showCart       = false,
        showProfile    = false,
        onProfileClick = onProfileClick,
        navController  = navController,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Stats Card (igual) ───────────────────────────────────
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

            Text(
                text       = "${filtered.size} Resultado(s)",
                modifier   = Modifier.padding(horizontal = 28.dp, vertical = 2.dp),
                fontSize   = 13.sp,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

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
                        OrderCard(
                            order = order,
                            onClick = {
                                selectedOrder = ordersDetails.find { it.order.id == order.id }
                            }
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }

    selectedOrder?.let { orderWithItems ->
        OrderDetailsDialog(
            orderWithItems = orderWithItems,
            onDismiss = { selectedOrder = null },
            reprintEnabled = !printing,
            onReprint = {
                printing = true
                scope.launch {
                    orderViewModel.loadReceipt(orderWithItems.order.id)
                        .onSuccess { resp ->
                            printer.printReceipt(resp.toReceiptModel("REIMPRESSAO"))
                        }
                        .onFailure {
                            Toast.makeText(
                                context,
                                it.message ?: "Erro ao obter o talão",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    printing = false
                }
            }
        )
    }

}






// ── Summary StateItem ───────────────────────────────────────────────────────────────



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
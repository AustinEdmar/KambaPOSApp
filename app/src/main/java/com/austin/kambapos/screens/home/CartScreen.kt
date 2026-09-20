package com.austin.kambapos.screens.home

import android.media.SoundPool
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import com.austin.kambapos.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.austin.kambapos.data.model.CartItemDTO
import com.austin.kambapos.navigation.Screens
import androidx.compose.ui.platform.LocalContext
import com.austin.kambapos.core.lib.SunmiPrinter
import com.austin.kambapos.data.model.ReceiptModel
import com.austin.kambapos.screens.home.components.CashPaymentDialog
import com.austin.kambapos.screens.home.components.PaymentDialog
import com.austin.kambapos.screens.home.components.PaymentMethod
import com.austin.kambapos.screens.home.components.ScreenScaffold
import com.austin.kambapos.screens.home.components.cart.OrderItemCard

import com.austin.kambapos.screens.home.components.cart.SummaryRowCart
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.utils.toKwanza

import com.austin.kambapos.viewmodel.CartViewModel
import com.austin.kambapos.viewmodel.OrderViewModel
import com.austin.kambapos.viewmodel.ShiftViewModel
import kotlinx.coroutines.launch


@Composable
fun CartScreen(
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    navController: NavHostController,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val shift     by shiftViewModel.shift.collectAsState()

    val cartCount by cartViewModel.cartCount.collectAsState()
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    val orderId by orderViewModel.orderId.collectAsState()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showCashDialog by remember { mutableStateOf(false) }
    val closeOrderState by orderViewModel.closeOrderState.collectAsState()


    val cartitems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    var cartLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val printer = remember { SunmiPrinter(context.applicationContext) }

    LaunchedEffect(Unit) {
        printer.connectPrinter()
    }

    DisposableEffect(Unit) {
        onDispose {
            printer.disconnect()
        }
    }

    // Busca ordem aberta (fluxo PDV sem mesa)
    LaunchedEffect(Unit) {
        orderViewModel.observeOrders()
    }

    LaunchedEffect(orderId) {
        cartViewModel.setOrderId(orderId)
    }

    LaunchedEffect(closeOrderState) {
        closeOrderState?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            orderViewModel.clearCloseOrderState()
        }
    }

    LaunchedEffect(cartitems) {
        when {
            cartitems.isNotEmpty() -> cartLoaded = true
            cartLoaded && cartitems.isEmpty() -> {
                navController.navigate(Screens.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val soundPool = remember {
        SoundPool.Builder().setMaxStreams(1).build()
    }

    val soundId = remember {
        soundPool.load(context, R.raw.beep, 1)
    }

    var showSearchDialog by remember { mutableStateOf(false) }

    val subtotal = cartitems.sumOf {
        (it.product?.price ?: 0.0) * it.cartItem.quantity
    }

    val tax = cartitems.sumOf { cartItemWithProduct ->
        val price    = cartItemWithProduct.product?.price ?: 0.0
        val qty      = cartItemWithProduct.cartItem.quantity
        val ivaRate  = cartItemWithProduct.product?.taxPercentage ?: 0.0
        price * qty * (ivaRate / 100.0)
    }

    val total = subtotal + tax

    ScreenScaffold(
        title = "Caixa: ${shift?.userName ?: "Nenhum"}",
        amountTitle = "Fundo: ${shift?.initialAmount.toKwanza()}",
        showMenu = true,
        showCart = cartitems.isNotEmpty(),
        showSearch = false,
        cartCount = cartCount,
        showProfile = false,
        onSearchClick = { showSearchDialog = true },
        onCartClick = onCartClick,
        navController = navController,
        onProfileClick = onProfileClick,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues()
            ) {
                items(cartitems, key = { it.cartItem.id }) { cartItemWithProduct ->
                    cartItemWithProduct.product?.let { product ->
                        OrderItemCard(
                            item = CartItemDTO(
                                id        = cartItemWithProduct.cartItem.id,
                                name      = product.name,
                                stockQty  = product.stock,
                                iva  = product.taxPercentage?.toInt(),
                                unitPrice = product.price.toInt(),
                                quantity  = cartItemWithProduct.cartItem.quantity,
                                imageUrl  = product.imageUrl
                            ),
                            onIncrease = {
                               soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                                cartViewModel.increaseQuantity(cartItemWithProduct)
                            },
                            onDecrease = {
                                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                                cartViewModel.decreaseQuantity(cartItemWithProduct)
                            },
                            decreaseEnabled = cartItemWithProduct.cartItem.quantity > 0,
                            increaseEnabled = product.stock > 1
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SummaryRowCart("Subtotal", subtotal.toKwanza(), Color.Gray, Color.Black)
                    SummaryRowCart("IVA", "${tax.toKwanza()} ", Color.Gray, Color.Gray)
                }

                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total a pagar", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                    Text(
                        total.toKwanza(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (showPaymentDialog) {
                    PaymentDialog(
                        onDismiss = { showPaymentDialog = false },
                        onSelect = { paymentMethod ->
                            showPaymentDialog = false
                            selectedPaymentMethod = paymentMethod

                            when(paymentMethod) {
                                PaymentMethod.CASH -> {
                                    showCashDialog = true
                                }
                                else -> {
                                    val items = cartitems.mapNotNull { cartItemWithProduct ->
                                        cartItemWithProduct.product?.let { product ->
                                            CartItemDTO(
                                                id = cartItemWithProduct.cartItem.id,
                                                name = product.name,
                                                stockQty = product.stock,
                                                iva = product.taxPercentage?.toInt(),
                                                unitPrice = product.price.toInt(),
                                                quantity = cartItemWithProduct.cartItem.quantity,
                                                imageUrl = product.imageUrl
                                            )
                                        }
                                    }

                                    orderViewModel.closeOrder(
                                        orderId = orderId,
                                        paymentMethod = paymentMethod.apiValue,
                                        received = null,
                                        change = null
                                    )

                                    val receipt = ReceiptModel(
                                        items = items,
                                        subtotal = subtotal,
                                        tax = tax,
                                        total = total,
                                        tableId = 0,
                                        orderId = orderId,
                                        paymentMethod = paymentMethod.apiValue
                                    )

                                    printer.printReceipt(receipt.copy(copyType = "1ª VIA - CLIENTE"))
                                    kotlinx.coroutines.MainScope().launch {
                                        kotlinx.coroutines.delay(5000)
                                        printer.printReceipt(receipt.copy(copyType = "2ª VIA - LOJA"))
                                    }
                                }
                            }
                        }
                    )
                }

                if (showCashDialog) {
                    CashPaymentDialog(
                        total = total,
                        onDismiss = { showCashDialog = false },
                        onConfirm = { received, change ->
                            showCashDialog = false

                            orderViewModel.closeOrder(
                                orderId = orderId,
                                paymentMethod = selectedPaymentMethod?.apiValue ?: "cash",
                                received = received,
                                change = change
                            )

                            val receipt = ReceiptModel(
                                items = cartitems.mapNotNull { it.product?.let { p ->
                                    CartItemDTO(
                                        id = it.cartItem.id,
                                        name = p.name,
                                        stockQty = p.stock,
                                        iva = p.taxPercentage?.toInt(),
                                        unitPrice = p.price.toInt(),
                                        quantity = it.cartItem.quantity,
                                        imageUrl = p.imageUrl
                                    )
                                }},
                                subtotal = subtotal,
                                tax = tax,
                                total = total,
                                tableId = 0,
                                orderId = orderId,
                                paymentMethod = selectedPaymentMethod?.apiValue ?: "cash",
                                paidAmount = received,
                                change = change
                            )

                            printer.printReceipt(receipt.copy(copyType = "1ª VIA - CLIENTE"))
                            kotlinx.coroutines.MainScope().launch {
                                kotlinx.coroutines.delay(10000)
                                printer.printReceipt(receipt.copy(copyType = "2ª VIA - LOJA"))
                            }
                        }
                    )
                }
                Button(
                    onClick = {
                        showPaymentDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 5.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp)) // ✅ recorta antes de pintar
                        .background(brush = SunmiGradient),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp), // ✅ evita sombra azulada por cima do gradiente
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Pagar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }
        }
    }
}

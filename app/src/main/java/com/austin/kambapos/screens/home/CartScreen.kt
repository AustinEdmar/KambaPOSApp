package com.austin.kambapos.screens.home

import android.media.SoundPool
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.austin.kambapos.core.lib.SunmiPrinter
import com.austin.kambapos.data.local.entity.CartItemWithProduct
import com.austin.kambapos.data.local.mapper.toReceiptModel
import com.austin.kambapos.data.model.CartItemDTO
import com.austin.kambapos.navigation.Screens
import com.austin.kambapos.screens.home.components.CashPaymentDialog
import com.austin.kambapos.screens.home.components.CheckoutOptionsDialog
import com.austin.kambapos.screens.home.components.NifPromptDialog
import com.austin.kambapos.screens.home.components.PaymentDialog
import com.austin.kambapos.screens.home.components.PaymentMethod
import com.austin.kambapos.screens.home.components.ScreenScaffold
import com.austin.kambapos.screens.home.components.cart.AuthorizationPinDialog
import com.austin.kambapos.screens.home.components.cart.OrderItemCard
import com.austin.kambapos.screens.home.components.cart.SummaryRowCart
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.utils.toKwanza
import com.austin.kambapos.viewmodel.CartViewModel
import com.austin.kambapos.viewmodel.CheckoutViewModel
import com.austin.kambapos.viewmodel.OrderViewModel
import com.austin.kambapos.viewmodel.ShiftViewModel
import kotlinx.coroutines.delay

@Composable
fun CartScreen(
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    navController: NavHostController,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    checkoutViewModel: CheckoutViewModel = hiltViewModel(),
) {
    val shift by shiftViewModel.shift.collectAsState()
    val checkout by checkoutViewModel.state.collectAsStateWithLifecycle()

    val cartCount by cartViewModel.cartCount.collectAsState()
    val orderId by orderViewModel.orderId.collectAsState()
    val closeOrderState by orderViewModel.closeOrderState.collectAsState()
    val cartitems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    val authFeedback by cartViewModel.authFeedback.collectAsStateWithLifecycle()


    var showNifPrompt by remember { mutableStateOf(false) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showCashDialog by remember { mutableStateOf(false) }
    var showAuthModal by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }

    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var pendingDecreaseItem by remember { mutableStateOf<CartItemWithProduct?>(null) }
    var cartLoaded by remember { mutableStateOf(false) }

    // true entre o envio do close e a resposta do servidor: impede que o
    // "carrinho vazio" navegue para a Home antes de o talão ser impresso
    var isClosing by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val printer = remember { SunmiPrinter(context.applicationContext) }

    fun navigateHome() {
        navController.navigate(Screens.Home.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    // Fecha a venda no servidor. O talão só é impresso quando a resposta chega.
    fun closeOrder(method: PaymentMethod?, received: Double?, change: Double?) {
        isClosing = true
        orderViewModel.closeOrder(
            orderId = orderId,
            paymentMethod = method?.apiValue ?: "cash",
            received = received,
            change = change,
            customerId = checkout.customerIdForOrder,
            documentType = checkout.documentType.apiValue
        )
    }

    LaunchedEffect(authFeedback) {
        authFeedback?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            cartViewModel.clearAuthFeedback()
        }
    }

    LaunchedEffect(Unit) { printer.connectPrinter() }

    DisposableEffect(Unit) {
        onDispose { printer.disconnect() }
    }

    // Busca ordem aberta (fluxo PDV sem mesa)
    LaunchedEffect(Unit) { orderViewModel.observeOrders() }

    LaunchedEffect(orderId) { cartViewModel.setOrderId(orderId) }

    LaunchedEffect(closeOrderState) {
        closeOrderState?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            orderViewModel.clearCloseOrderState()
        }
    }

    LaunchedEffect(cartitems) {
        when {
            cartitems.isNotEmpty() -> cartLoaded = true
            cartLoaded && cartitems.isEmpty() && !isClosing -> navigateHome()
        }
    }

    // Venda fechada com sucesso: imprime as duas vias com os dados do servidor

    LaunchedEffect(Unit) {
        orderViewModel.orderClosed.collect { resp ->
            try {
                checkoutViewModel.reset()

                Log.d("PRINT", "1ª via")
                printer.printReceiptAndWait(resp.toReceiptModel("1ª VIA - CLIENTE"))

                delay(3000)   // agora conta a partir do fim da impressão: tempo para rasgar

                Log.d("PRINT", "2ª via")
                printer.printReceiptAndWait(resp.toReceiptModel("2ª VIA - LOJA"))

                Log.d("PRINT", "fim")
                isClosing = false
                navigateHome()
            } catch (e: kotlinx.coroutines.CancellationException) {
                Log.e("PRINT", "cancelado antes de terminar (o ecrã saiu?)", e)
                throw e
            } catch (e: Exception) {
                Log.e("PRINT", "erro ao imprimir", e)
            }
        }
    }



    // falha ao fechar: o carrinho continua cheio, o operador pode tentar de novo
    LaunchedEffect(Unit) {
        orderViewModel.closeFailed.collect { isClosing = false }
    }

    val soundPool = remember {
        SoundPool.Builder().setMaxStreams(1).build()
    }
    val soundId = remember { soundPool.load(context, R.raw.beep, 1) }

    fun round2(value: Double): Double = Math.round(value * 100.0) / 100.0

    // AGT exige arredondamento por excesso (ceiling) no imposto por linha —
    // confirmado pelo erro E70 do validador (ex.: 500.10 * 14% = 70.014 ->
    // AGT espera 70.02, não 70.01 de um round() normal).
    fun ceilMoney(value: Double, decimals: Int = 2): Double {
        val factor = Math.pow(10.0, decimals.toDouble())
        val rounded = Math.round(value * factor * 10000.0) / 10000.0 // guarda ruído de float
        return kotlin.math.ceil(rounded) / factor
    }

    val subtotal = cartitems.sumOf {
        round2(it.cartItem.unitPriceSnapshot * it.cartItem.quantity)
    }

    val tax = cartitems.sumOf { cartItemWithProduct ->
        val price = cartItemWithProduct.cartItem.unitPriceSnapshot
        val qty = cartItemWithProduct.cartItem.quantity
        val ivaRate = cartItemWithProduct.cartItem.ivaRateSnapshot

        val subtotalLine = round2(price * qty)       // 1) arredonda ANTES do IVA
        ceilMoney(subtotalLine * (ivaRate / 100.0))  // 2) IVA por excesso
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
                                id = cartItemWithProduct.cartItem.id,
                                name = product.name,
                                stockQty = product.stock,
                                iva = product.taxPercentage?.toInt(),
                                unitPrice = product.price.toKwanza(),
                                quantity = cartItemWithProduct.cartItem.quantity,
                                imageUrl = product.imageUrl
                            ),
                            onIncrease = {
                                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                                cartViewModel.increaseQuantity(cartItemWithProduct)
                            },
                            onDecrease = {
                                pendingDecreaseItem = cartItemWithProduct
                                showAuthModal = true
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

                // 1) Factura com NIF?
                if (showNifPrompt) {
                    NifPromptDialog(
                        onYes = {
                            showNifPrompt = false
                            showCheckoutDialog = true
                        },
                        onNo = {
                            showNifPrompt = false
                            checkoutViewModel.reset()   // volta a FR e consumidor final
                            showPaymentDialog = true
                        },
                        onDismiss = { showNifPrompt = false }
                    )
                }

                // 2) Documento e cliente
                if (showCheckoutDialog) {
                    CheckoutOptionsDialog(
                        vm = checkoutViewModel,
                        onDismiss = { showCheckoutDialog = false },
                        onContinue = {
                            showCheckoutDialog = false
                            showPaymentDialog = true
                        }
                    )
                }

                // 3) Forma de pagamento
                if (showPaymentDialog) {
                    PaymentDialog(
                        onDismiss = { showPaymentDialog = false },
                        onSelect = { paymentMethod ->
                            showPaymentDialog = false
                            selectedPaymentMethod = paymentMethod

                            when (paymentMethod) {
                                PaymentMethod.CASH -> showCashDialog = true
                                else -> closeOrder(paymentMethod, received = null, change = null)
                            }
                        }
                    )
                }

                // 4) Dinheiro: valor recebido e troco
                if (showCashDialog) {
                    CashPaymentDialog(
                        total = total,
                        onDismiss = { showCashDialog = false },
                        onConfirm = { received, change ->
                            showCashDialog = false
                            closeOrder(selectedPaymentMethod, received, change)
                        }
                    )
                }

                Button(
                    onClick = { showNifPrompt = true },
                    enabled = !isClosing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 5.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(brush = SunmiGradient),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Pagar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }
        }
    }

    if (showAuthModal) {
        AuthorizationPinDialog(
            onDismiss = {
                showAuthModal = false
                pendingDecreaseItem = null
            },
            onPinComplete = { pin ->
                cartViewModel.onPinSubmitted(pin, pendingDecreaseItem)
                showAuthModal = false
                pendingDecreaseItem = null
            }
        )
    }
}
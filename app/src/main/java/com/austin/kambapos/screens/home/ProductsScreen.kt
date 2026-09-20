package com.austin.kambapos.screens.home

import android.app.Activity
import android.content.Intent
import android.media.SoundPool
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.austin.kambapos.R
import com.austin.kambapos.data.model.UiStates.AuthUiState
import com.austin.kambapos.data.model.UiStates.UserUiState
import com.austin.kambapos.navigation.Screens

import com.austin.kambapos.screens.home.components.EmptyProductsState
import com.austin.kambapos.screens.home.components.OpenTurnDialog
import com.austin.kambapos.screens.home.components.ProductItemCard
import com.austin.kambapos.screens.home.components.ScreenScaffold
import com.austin.kambapos.screens.home.components.SearchDialog
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.ui.theme.SunmiOrange
import com.austin.kambapos.utils.toKwanza
import com.austin.kambapos.viewmodel.AuthViewModel
import com.austin.kambapos.viewmodel.CartViewModel
import com.austin.kambapos.viewmodel.HomeViewModel
import com.austin.kambapos.viewmodel.OrderViewModel
import com.austin.kambapos.viewmodel.ProductViewModel
import com.austin.kambapos.viewmodel.ShiftViewModel
import com.austin.kambapos.viewmodel.UsersViewModel
import kotlin.math.roundToInt

@Composable
fun ProductsScreen(
    navController: NavHostController,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    HomeViewModel: HomeViewModel = hiltViewModel(),
    AuthviewModel: AuthViewModel = hiltViewModel(),
    usersViewModel: UsersViewModel = hiltViewModel(), // ← adiciona
) {
    var showAbrirTurnoDialog by remember { mutableStateOf(true) }
    val userState = HomeViewModel.uiState
    var fabOffsetX by remember { mutableStateOf(0f) }
    var fabOffsetY by remember { mutableStateOf(0f) }

    val cartitems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    val uiState = AuthviewModel.uiState

    val shift by shiftViewModel.shift.collectAsState()
    val products by productViewModel.products.collectAsState()
    val categories by productViewModel.categories.collectAsState()
    val selectedCategory by productViewModel.selectedCategory.collectAsState()
    val orderId by orderViewModel.orderId.collectAsState()
    val cartCount by cartViewModel.cartCount.collectAsState()
    var showSearchDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val error by orderViewModel.errorMessage.collectAsState()

    val soundPool = remember { SoundPool.Builder().setMaxStreams(1).build() }
    val soundId = remember { soundPool.load(context, R.raw.beep, 1) }

    // ─── Sunmi Scanner Launcher ───────────────────────────────────────────────
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            @Suppress("UNCHECKED_CAST")
            val scanResults = result.data?.extras
                ?.getSerializable("data") as? ArrayList<HashMap<String, String>>

            scanResults?.firstOrNull()?.let { hashMap ->
                val barcode = hashMap["VALUE"].orEmpty()

                if (barcode.isBlank()) return@let

                val product = products.find { it.barcode == barcode }

                if (product == null) {
                    Toast.makeText(context, "Produto não encontrado: $barcode", Toast.LENGTH_SHORT).show()
                    return@let
                }

                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)

                if (product.stock <= 0) {
                    Toast.makeText(context, "Produto sem estoque", Toast.LENGTH_SHORT).show()
                    return@let
                }

                shift?.let { s ->
                    // ✅ Otimizado: Abre a ordem se necessário e adiciona o produto no callback
                    orderViewModel.openOrder(s.id) { newOrderId ->
                        cartViewModel.setOrderId(newOrderId)
                        cartViewModel.addProduct(product, newOrderId)
                    }
                } ?: run {
                    Toast.makeText(context, "Nenhum turno aberto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ─── Função para lançar o scanner Sunmi ──────────────────────────────────
    fun launchSunmiScanner() {
        try {
            val intent = Intent("com.sunmi.scan").apply {
                putExtra("PLAY_SOUND", true)
                putExtra("PLAY_VIBRATE", false)
                putExtra("IS_SHOW_SETTING", false)
                putExtra("IS_SHOW_ALBUM", false)
                putExtra("IS_OPEN_LIGHT", true)
                putExtra("SCAN_MODE", false)
                putExtra("IDENTIFY_MORE_CODE", false)
                putExtra("IDENTIFY_INVERSE", true)
                putExtra("IS_QR_CODE_ENABLE", true)
                putExtra("IS_EAN_8_ENABLE", true)
                putExtra("IS_EAN_13_ENABLE", true)
                putExtra("IS_UPC_A_ENABLE", true)
                putExtra("IS_UPC_E_ENABLE", true)
                putExtra("IS_CODE_128_ENABLE", true)
            }
            scannerLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("SunmiScanner", "Erro ao iniciar scanner Sunmi", e)
        }
    }

    // ─── LaunchedEffects ──────────────────────────────────────────────────────
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Unauthenticated) {
            navController.navigate(Screens.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(userState) {
        if (userState is UserUiState.Success) {
            orderViewModel.setUserId(userState.user.id)
        }
    }

    LaunchedEffect(shift) {
        shift?.let {
            if (it.status == "open") {
                showAbrirTurnoDialog = false
                orderViewModel.observeCurrentOrder(it.id)
            } else {
                showAbrirTurnoDialog = true
            }
        }
    }

    LaunchedEffect(orderId) {
        cartViewModel.setOrderId(orderId)
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    // ─── UI ───────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier.fillMaxSize()
            .background(brush = SunmiGradient)
    ) {
        ScreenScaffold(
            title = "Caixa: ${shift?.userName ?: "Nenhum"}",
            amountTitle = "Fundo: ${shift?.initialAmount.toKwanza()}",
            showMenu = true,
            showCart = cartitems.isNotEmpty(),
            showSearch = true,
            showScanner = false,
            cartCount = cartCount,
            showProfile = false,
            onSearchClick = { showSearchDialog = true },
            onCartClick = onCartClick,
            navController = navController,
            onProfileClick = onProfileClick,
        ) {
            // ── Categorias ────────────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        iconM = Icons.Default.Fastfood,
                        text = "Todos",
                        isSelected = selectedCategory == null,
                        onClick = { productViewModel.onCategorySelected(null) }
                    )
                }
                items(categories.size) { index ->
                    val category = categories[index]
                    CategoryChip(
                        icon = category.imagePath,
                        text = category.name,
                        isSelected = selectedCategory == category.id,
                        onClick = { productViewModel.onCategorySelected(category.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // ── Produtos ──────────────────────────────────────────────────────
            if (products.isEmpty()) {
                EmptyProductsState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products, key = { product -> product.id }) { product ->
                        ProductItemCard(
                            product = product,
                            onClick = {
                                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                                if (product.stock <= 0) {
                                    Toast.makeText(context, "Produto sem estoque", Toast.LENGTH_SHORT).show()
                                    return@ProductItemCard
                                }
                                shift?.let { s ->
                                    orderViewModel.openOrder(s.id) { newOrderId ->
                                        cartViewModel.setOrderId(newOrderId)
                                        cartViewModel.addProduct(product, newOrderId)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // ── FAB Scanner (arrastável) ──────────────────────────────────────────
        FloatingActionButton(
            onClick = { /* O clique é tratado no pointerInput para evitar disparos duplicados */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 84.dp)
                .offset { IntOffset(fabOffsetX.roundToInt(), fabOffsetY.roundToInt()) }
                .size(56.dp) // ✅ tamanho fixo — evita que o FAB expanda
                .clip(CircleShape) // ✅ recorta o gradiente na forma circular
                .background(brush = SunmiGradient) // ✅ gradiente aplicado diretamente aqui
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        var isDrag = false
                        do {
                            val event = awaitPointerEvent()
                            val drag = event.changes.firstOrNull() ?: break
                            val delta = drag.positionChange()
                            if (delta.getDistance() > 10f) {
                                isDrag = true
                                fabOffsetX += delta.x
                                fabOffsetY += delta.y
                                drag.consume()
                            }
                        } while (event.changes.any { it.pressed })

                        // ✅ Corrige o bug de sair do scan: Lança o scanner apenas se não foi arraste e uma única vez
                        if (!isDrag) {
                            launchSunmiScanner()
                        }
                    }
                },
            containerColor = Color.Transparent, // ✅ transparente — o gradiente do modifier aparece
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp) // evita sombra por cima do gradiente
        ) {
            Icon(
                imageVector = Icons.Default.DocumentScanner,
                contentDescription = "Escanear produto"
            )
        }
    }

    // ─── Diálogos ─────────────────────────────────────────────────────────────
    if (showSearchDialog) {
        SearchDialog(
            onDismiss = {
                showSearchDialog = false
            },
            onSearch = { query ->
                productViewModel.onSearchChanged(query)
            }
        )
    }

    if (showAbrirTurnoDialog) {
        OpenTurnDialog(
            onDone = { amountStr ->
                val amount = amountStr.toDoubleOrNull()
                shiftViewModel.shift(amount, null)
            },
            onDismiss = { showAbrirTurnoDialog = false }
        )
    }
}

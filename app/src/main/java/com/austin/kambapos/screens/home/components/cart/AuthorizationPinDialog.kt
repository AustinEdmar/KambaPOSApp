package com.austin.kambapos.screens.home.components.cart

import android.media.SoundPool
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.austin.kambapos.R
import com.austin.kambapos.ui.theme.SunmiGradient

@Composable
fun AuthorizationPinDialog(
    onDismiss: () -> Unit,
    onPinComplete: (String) -> Unit,
    pinLength: Int = 6
) {
    val context = LocalContext.current

    var pin by remember { mutableStateOf("") }

    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }

    val soundId = remember {
        soundPool.load(context, R.raw.beep, 1)
    }

    DisposableEffect(Unit) {
        onDispose {
            soundPool.release()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // ─────────────────────────────────────────────
                // HEADER
                // ─────────────────────────────────────────────

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp
                                )
                            )
                            .background(
                                brush = SunmiGradient
                            ),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp
                        ),
                        color = Color.Transparent
                    ) {}

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "AUTORIZAÇÃO",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End
                        ) {

                            TextButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Cancelar",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // ─────────────────────────────────────────────
                // CORPO
                // ─────────────────────────────────────────────

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 20.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Autorização necessária",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Text(
                        text = "Peça ao supervisor para digitar o PIN",
                        fontSize = 13.sp,
                        color = Color(0xFF777777)
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    // ─────────────────────────────────────────
                    // INDICADOR DO PIN
                    // ─────────────────────────────────────────

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        repeat(pinLength) { index ->

                            val filled = index < pin.length

                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(
                                        color = if (filled) {
                                            Color(0xFFFFC107)
                                        } else {
                                            Color(0xFFE0E0E0)
                                        },
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    // ─────────────────────────────────────────
                    // TECLADO
                    // ─────────────────────────────────────────

                    val keys = listOf(
                        "1", "2", "3",
                        "4", "5", "6",
                        "7", "8", "9",
                        "⌫", "0", "OK"
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(keys) { key ->

                            when (key) {

                                "OK" -> {

                                    Button(
                                        onClick = {
                                            soundPool.play(
                                                soundId,
                                                1f,
                                                1f,
                                                0,
                                                0,
                                                1f
                                            )

                                            if (pin.isNotEmpty()) {
                                                onPinComplete(pin)
                                                pin = ""
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .clip(
                                                RoundedCornerShape(10.dp)
                                            )
                                            .background(
                                                brush = SunmiGradient
                                            ),
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 0.dp
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Icon(
                                                imageVector = Icons.Default.KeyboardReturn,
                                                contentDescription = "Confirmar",
                                                tint = Color.White
                                            )

                                            Spacer(
                                                modifier = Modifier.width(4.dp)
                                            )


                                        }
                                    }
                                }

                                else -> {

                                    Button(
                                        onClick = {

                                            soundPool.play(
                                                soundId,
                                                1f,
                                                1f,
                                                0,
                                                0,
                                                1f
                                            )

                                            when (key) {

                                                "⌫" -> {
                                                    if (pin.isNotEmpty()) {
                                                        pin = pin.dropLast(1)
                                                    }
                                                }

                                                else -> {
                                                    if (pin.length < pinLength) {
                                                        pin += key
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFF5F5F5),
                                            contentColor = Color(0xFF333333)
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 1.dp
                                        )
                                    ) {

                                        if (key == "⌫") {

                                            Icon(
                                                imageVector = Icons.Default.Backspace,
                                                contentDescription = "Apagar",
                                                modifier = Modifier.size(21.dp),
                                                tint = Color(0xFF555555)
                                            )

                                        } else {

                                            Text(
                                                text = key,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    // ─────────────────────────────────────────
                    // CANCELAR
                    // ─────────────────────────────────────────

                    TextButton(
                        onClick = onDismiss
                    ) {

                        Text(
                            text = "Cancelar",
                            color = Color(0xFF777777),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
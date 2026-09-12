package com.austin.kambapos.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.austin.kambapos.data.model.UiStates.ShitftUiState
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.utils.formatToKwanza
import com.austin.kambapos.utils.parseKwanzaToDouble
import com.austin.kambapos.viewmodel.AuthViewModel
import com.austin.kambapos.viewmodel.HomeViewModel
import com.austin.kambapos.viewmodel.ShiftViewModel
import kotlinx.coroutines.delay

@Composable
fun OpenTurnDialog(
    onDismiss: () -> Unit,
    onDone: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    AuthviewModel: AuthViewModel = hiltViewModel(),
    shiftViewModel: ShiftViewModel = hiltViewModel()
) {
    var initial_amount by remember { mutableStateOf("") }
    val initial_amountShiftFormated = formatToKwanza(initial_amount)

    val state = shiftViewModel.shitftUiState

    // 👇 COLOQUE AQUI
    if (state is ShitftUiState.Success) {
        LaunchedEffect(state) {
            delay(1000)
            onDismiss()
        }
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
                usePlatformDefaultWidth = false,
        dismissOnClickOutside = false,   // 👈 impede fechar ao clicar fora
        dismissOnBackPress = false       // 👈 impede fechar com botão voltar
    )
    ) {


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ── Header laranja ──────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {

                    // Fundo
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .background(brush = SunmiGradient),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        color = Color.Transparent
                    ) {}

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "ABRIR O TURNO",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End
                        ) {



                            TextButton(
                                onClick = { AuthviewModel.logout() },

                                shape = RoundedCornerShape(10.dp)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Sair",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }


                        }
                    }
                }

                // ── Corpo ───────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Fundo do caixa",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )

                    OutlinedTextField(
                        value = initial_amountShiftFormated,
                        onValueChange = { input ->
                            // Guarda apenas os dígitos
                            initial_amount = input.filter { it.isDigit() }
                        },

                        placeholder = {
                            Text(
                                text = "Insira o fundo de maneio",
                                color = Color(0xFFAAAAAA),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFC107),
                            unfocusedBorderColor = Color(0xFFDDDDDD),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = Color(0xFFFFC107)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))





                    Button(
                        onClick = {
                            val valor = parseKwanzaToDouble(initial_amountShiftFormated)
                            onDone(valor.toString())
                           // onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)) // ✅ recorta antes de pintar
                            .background(brush = SunmiGradient),
                        shape = RoundedCornerShape(10.dp),
                        border = null,
                        elevation = ButtonDefaults.buttonElevation(0.dp), // ✅ evita sombra azulada por cima do gradiente
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            when (state) {

                                is ShitftUiState.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                }

                                is ShitftUiState.Success -> {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Sucesso!",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.AttachMoney,
                                        contentDescription = "money",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Adicionar Fundo",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }


                }
                }
            }
        }


        //
    }

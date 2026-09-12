package com.austin.kambapos.screens.auth


import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.austin.kambapos.R
import com.austin.kambapos.data.model.UiStates.LoginUiState
import com.austin.kambapos.navigation.Screens
import com.austin.kambapos.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

/**
 * VERSÃO MELHORADA COM RECURSOS ADICIONAIS:
 * - Validação em tempo real com feedback visual
 * - Animação de shake ao erro
 * - Focus management (TAB entre campos)
 * - Keyboard actions (Next/Done)
 * - Ripple effects
 * - Success animation
 */
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.uiState
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("austin@gmail.com") }
    var password by remember { mutableStateOf("923.eddy") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Validation states
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6

    // FIX: derivedStateOf deve ler o estado (email, password) diretamente no bloco
    val isFormValid by remember {
        derivedStateOf {
            Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 6
        }
    }

    val isButtonEnabled =
        state !is LoginUiState.Loading &&
                isFormValid

    // Animation states
    var logoVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }
    var imageVisible by remember { mutableStateOf(false) }
    var fieldsVisible by remember { mutableStateOf(false) }

    // Shake animation for errors
    var shakeError by remember { mutableStateOf(false) }
    val shakeOffset by animateFloatAsState(
        targetValue = if (shakeError) 0f else 1f,
        animationSpec = spring(
            dampingRatio = 0.3f,
            stiffness = 500f
        ),
        finishedListener = { shakeError = false },
        label = "shake"
    )

    // Launch animations sequentially
    LaunchedEffect(Unit) {
        delay(100)
        logoVisible = true
        delay(200)
        titleVisible = true
        delay(150)
        subtitleVisible = true
        delay(300)
        imageVisible = true
        delay(200)
        fieldsVisible = true
    }

    // 🔑 Observa sucesso e navega
    LaunchedEffect(state) {
        if (state is LoginUiState.Success) {
            // Pequeno delay para mostrar success state
            delay(500)
            navController.navigate(Screens.Home.route) {
                popUpTo(Screens.Login.route) { inclusive = true }
            }
        }
    }

    // Show error toast and shake animation
    LaunchedEffect(state) {
        if (state is LoginUiState.Error) {
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            shakeError = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = R.drawable.splashscreen),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(top =  30.dp))

            // Logo Icon with success animation
            AnimatedVisibility(
                visible = logoVisible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, // centraliza horizontalmente
                    modifier = Modifier.padding(top = 8.dp) // opcional, para dar um espaço do topo
                ) {
                    Box(
                        modifier = Modifier.size(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            contentScale = ContentScale.Fit // mantém toda a imagem visível
                        )
                    }



                    Text(
                        text = "KambaPOS",
                        fontWeight = FontWeight.Bold,

                        fontSize = 20.sp,
                        color = Color.White
                    )
                }




            }

            Spacer(modifier = Modifier.height(5.dp))

            // Title with animation
            AnimatedVisibility(
                visible = titleVisible,
                enter = slideInVertically(
                    initialOffsetY = { -40 },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(animationSpec = tween(500))
            ) {
                Text(
                    text = "Acesse sua conta",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle with animation
            AnimatedVisibility(
                visible = subtitleVisible,
                enter = slideInVertically(
                    initialOffsetY = { -30 },
                    animationSpec = tween(durationMillis = 500, delayMillis = 100)
                ) + fadeIn(animationSpec = tween(500, delayMillis = 100))
            ) {
                Text(
                    text = "Insira suas credenciais para acessar sua conta",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Image with animation
            AnimatedVisibility(
                visible = imageVisible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    initialScale = 0.3f
                ) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp), // tamanho do container
                    contentAlignment = Alignment.Center
                ) {
                    // Primeira imagem (menor, atrás)
                    Image(
                        painter = painterResource(id = R.drawable.arco),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(200.dp) // tamanho da imagem
                            .offset(y = 30.dp) // desce 30.dp
                    )


                    // Segunda imagem (maior, na frente)
                    Image(
                        painter = painterResource(id = R.drawable.moco),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(300.dp) // maior que a primeira
                    )
                }

            }



            // Form fields with staggered animation and shake
            AnimatedVisibility(
                visible = fieldsVisible,
                enter = slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(durationMillis = 600)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                Column(
                    modifier = Modifier.offset(x = if (shakeError) (1 - shakeOffset) * 10.dp else 0.dp)
                ) {
                    // Email Field with validation
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailTouched = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = {
                            Text(
                                "Email",
                                color = Color.Gray.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            if (emailTouched) {
                                Icon(
                                    imageVector = if (isEmailValid) Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (isEmailValid) Color.Green else Color.Red
                                )
                            }
                        },
                        isError = emailTouched && !isEmailValid,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            errorContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            errorBorderColor = Color.Red.copy(alpha = 0.5f),
                            cursorColor = Color(0xFFFF8C42),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(28.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    // Email error message
                    AnimatedVisibility(
                        visible = emailTouched && !isEmailValid,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = "Por favor, insira um email válido",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Field with validation
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordTouched = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = {
                            Text(
                                "Password",
                                color = Color.Gray.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically, // centraliza os ícones na mesma linha
                                horizontalArrangement = Arrangement.spacedBy(4.dp) // espaçamento entre os ícones
                            ) {
                                if (passwordTouched && isPasswordValid) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )
                                }
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },

                        isError = passwordTouched && !isPasswordValid,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            errorContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            errorBorderColor = Color.Red.copy(alpha = 0.5f),
                            cursorColor = Color(0xFFFF8C42),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(28.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (isFormValid) {
                                    viewModel.login(email, password)

                                }
                            }
                        )
                    )

                    // Password error message
                    AnimatedVisibility(
                        visible = passwordTouched && !isPasswordValid,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = "A senha deve ter no mínimo 6 caracteres",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Forgot Password
                   Text(
                                           text = "Esqueceu a senha ?",
                                          fontSize = 13.sp,
                                           color = Color.White,
                                           modifier = Modifier
                                              .align(Alignment.End)
                                              .clickable(
                                                 interactionSource = remember { MutableInteractionSource() },
                                                  indication = null
                                              ) {
                                                   // Navigate to forgot password
                                                },
                                           fontWeight = FontWeight.Medium
                                        )



                    // Remember Me Checkbox


                    Spacer(modifier = Modifier.height(5.dp))


                    // Login Button with Loading State
                    Button(
                        onClick = {
                            emailTouched = true
                            passwordTouched = true
                            if (isFormValid) {
                                viewModel.login(email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        enabled = isButtonEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid) Color.White else Color.White.copy(alpha = 0.6f),
                            disabledContainerColor = Color.White.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {

                        when (state) {
                            is LoginUiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFFFF8C42),
                                    strokeWidth = 2.dp
                                )
                            }
                            is LoginUiState.Success -> {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color.Green,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sucesso!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Green
                                )
                            }
                            else -> {
                                Text(
                                    text = "Entrar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF8C42)
                                )
                            }
                        }
                    }


                }
            }
        }
    }
}

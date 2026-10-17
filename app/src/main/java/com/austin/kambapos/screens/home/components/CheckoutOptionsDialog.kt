
package com.austin.kambapos.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.austin.kambapos.data.model.DocumentType
import com.austin.kambapos.ui.theme.SunmiGradient
import com.austin.kambapos.viewmodel.CheckoutViewModel
import com.austin.kambapos.viewmodel.NewCustomerForm

private val CheckoutBlue = Color(0xFF3554C1)
private val CheckoutGreen = Color(0xFF2E7D32)
private val CheckoutGray = Color(0xFF777777)
private val CheckoutBorder = Color(0xFFDDDDDD)
private val CheckoutText = Color(0xFF333333)
private val CheckoutYellow = Color(0xFFFFC107)

@Composable
fun CheckoutOptionsDialog(
    vm: CheckoutViewModel,
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {
    val s by vm.state.collectAsStateWithLifecycle()

    var form by remember {
        mutableStateOf(NewCustomerForm())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val dialogMaxHeight = maxHeight * 0.9f

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .heightIn(max = dialogMaxHeight),
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

                    // =========================================
                    // CABEÇALHO FIXO
                    // =========================================

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
                                .background(SunmiGradient),
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
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )

                            Text(
                                text = "DOCUMENTO E CLIENTE",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            TextButton(
                                onClick = onDismiss,
                                enabled = !s.loading,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Fechar",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    // =========================================
                    // FORMULÁRIO COM SCROLL VERTICAL
                    // =========================================

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .padding(
                                horizontal = 20.dp,
                                vertical = 18.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        // TIPO DE DOCUMENTO

                        Text(
                            text = "Tipo de documento",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CheckoutText
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(
                                    rememberScrollState()
                                ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DocumentType.entries.forEach { type ->
                                FilterChip(
                                    selected = s.documentType == type,
                                    onClick = {
                                        vm.onDocumentType(type)
                                    },
                                    enabled = !s.loading,
                                    label = {
                                        Text(
                                            text = type.apiValue,
                                            fontSize = 12.sp
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFE8EDFF),
                                        selectedLabelColor = CheckoutBlue,
                                        selectedLeadingIconColor = CheckoutBlue
                                    )
                                )
                            }
                        }

                        Text(
                            text = s.documentType.hint,
                            fontSize = 12.sp,
                            color = CheckoutGray
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        // =====================================
                        // PESQUISA POR NIF
                        // =====================================

                        Text(
                            text = "Dados fiscais do cliente",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CheckoutText
                        )

                        OutlinedTextField(
                            value = s.nif,
                            onValueChange = vm::onNifChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("NIF (opcional)")
                            },
                            placeholder = {
                                Text(
                                    text = "Introduza o NIF",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 14.sp
                                )
                            },
                            singleLine = true,
                            enabled = !s.loading,
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            trailingIcon = {
                                TextButton(
                                    onClick = vm::search,
                                    enabled = !s.loading
                                ) {
                                    if (s.loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )

                                        Spacer(
                                            modifier = Modifier.size(4.dp)
                                        )

                                        Text("Procurar")
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CheckoutYellow,
                                unfocusedBorderColor = CheckoutBorder,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color(0xFFF7F7F7),
                                cursorColor = CheckoutYellow
                            )
                        )

                        // =====================================
                        // CLIENTE ENCONTRADO
                        // =====================================

                        s.customer?.let { customer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CheckoutGreen,
                                    modifier = Modifier.size(22.dp)
                                )

                                Spacer(
                                    modifier = Modifier.size(8.dp)
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Cliente encontrado",
                                        fontSize = 11.sp,
                                        color = CheckoutGreen
                                    )

                                    Text(
                                        text = customer.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF245B29)
                                    )
                                }
                            }
                        }

                        // =====================================
                        // MENSAGEM DE ERRO
                        // =====================================

                        s.error?.let { error ->
                            Text(
                                text = error,
                                color = Color(0xFFD32F2F),
                                fontSize = 12.sp
                            )
                        }

                        // =====================================
                        // REGISTO DE NOVO CLIENTE
                        // =====================================

                        if (s.notFound) {

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = CheckoutBlue,
                                    modifier = Modifier.size(21.dp)
                                )

                                Spacer(
                                    modifier = Modifier.size(8.dp)
                                )

                                Text(
                                    text = "Registar novo cliente",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CheckoutBlue
                                )
                            }

                            Text(
                                text = "Preencha os dados do cliente.",
                                fontSize = 12.sp,
                                color = CheckoutGray
                            )

                            // PARTICULAR OU EMPRESA

                            Text(
                                text = "Tipo de cliente",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = CheckoutText
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = form.type == "individual",
                                    onClick = {
                                        form = form.copy(
                                            type = "individual"
                                        )
                                    },
                                    enabled = !s.loading,
                                    label = {
                                        Text("Particular")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFE8EDFF),
                                        selectedLabelColor = CheckoutBlue
                                    )
                                )

                                FilterChip(
                                    selected = form.type == "company",
                                    onClick = {
                                        form = form.copy(
                                            type = "company"
                                        )
                                    },
                                    enabled = !s.loading,
                                    label = {
                                        Text("Empresa")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFE8EDFF),
                                        selectedLabelColor = CheckoutBlue
                                    )
                                )
                            }

                            // NOME

                            OutlinedTextField(
                                value = form.name,
                                onValueChange = {
                                    form = form.copy(name = it)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Nome *")
                                },
                                placeholder = {
                                    Text("Nome do cliente")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null
                                    )
                                },
                                singleLine = true,
                                enabled = !s.loading,
                                shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CheckoutYellow,
                                    unfocusedBorderColor = CheckoutBorder,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = CheckoutYellow
                                )
                            )

                            // TELEFONE

                            OutlinedTextField(
                                value = form.phone,
                                onValueChange = {
                                    form = form.copy(phone = it)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Telefone")
                                },
                                placeholder = {
                                    Text("Ex.: 923 000 000")
                                },
                                singleLine = true,
                                enabled = !s.loading,
                                shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CheckoutYellow,
                                    unfocusedBorderColor = CheckoutBorder,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = CheckoutYellow
                                )
                            )

                            // EMAIL

                            OutlinedTextField(
                                value = form.email,
                                onValueChange = {
                                    form = form.copy(email = it)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Email")
                                },
                                placeholder = {
                                    Text("cliente@exemplo.com")
                                },
                                singleLine = true,
                                enabled = !s.loading,
                                shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CheckoutYellow,
                                    unfocusedBorderColor = CheckoutBorder,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = CheckoutYellow
                                )
                            )

                            // MORADA

                            OutlinedTextField(
                                value = form.address,
                                onValueChange = {
                                    form = form.copy(address = it)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Morada")
                                },
                                placeholder = {
                                    Text("Morada do cliente")
                                },
                                enabled = !s.loading,
                                minLines = 2,
                                maxLines = 3,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CheckoutYellow,
                                    unfocusedBorderColor = CheckoutBorder,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = CheckoutYellow
                                )
                            )

                            // REGISTAR CLIENTE

                            Button(
                                onClick = {
                                    vm.createCustomer(form)
                                },
                                enabled = form.name.trim().length >= 3 &&
                                        !s.loading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CheckoutBlue,
                                    disabledContainerColor = Color(0xFFE0E0E0),
                                    disabledContentColor = Color(0xFF999999)
                                )
                            ) {
                                if (s.loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )

                                    Spacer(
                                        modifier = Modifier.size(8.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Spacer(
                                        modifier = Modifier.size(8.dp)
                                    )
                                }

                                Text(
                                    text = "Registar cliente",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // =========================================
                    // RODAPÉ FIXO
                    // =========================================

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(
                                horizontal = 20.dp,
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            enabled = !s.loading,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Cancelar",
                                color = CheckoutGray
                            )
                        }

                        Button(
                            onClick = onContinue,
                            enabled = !s.loading,
                            modifier = Modifier
                                .weight(1.4f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CheckoutBlue,
                                disabledContainerColor = Color(0xFFE0E0E0),
                                disabledContentColor = Color(0xFF999999)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier = Modifier.size(6.dp)
                            )

                            Text(
                                text = "Continuar",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

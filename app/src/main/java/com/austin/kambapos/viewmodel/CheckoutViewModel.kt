package com.austin.kambapos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.model.CreateCustomerRequest
import com.austin.kambapos.data.model.CustomerDTO
import com.austin.kambapos.data.model.DocumentType
import com.austin.kambapos.data.repository.CustomerRepository
import com.austin.kambapos.data.repository.LookupResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class NewCustomerForm(
    val name: String = "",
    val type: String = "individual",   // "individual" | "company"
    val phone: String = "",
    val email: String = "",
    val address: String = ""
)
data class CheckoutState(
    val nif: String = "",
    val customer: CustomerDTO? = null,
    val documentType: DocumentType = DocumentType.FR,
    val loading: Boolean = false,
    val notFound: Boolean = false,
    val error: String? = null
) {
    val customerIdForOrder: Long?
        get() = customer?.takeUnless { it.isFinalConsumer }?.id
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repo: CustomerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    fun onNifChange(v: String) = _state.update {
        it.copy(nif = v.trim().uppercase(), customer = null, notFound = false, error = null)
    }

    fun onDocumentType(t: DocumentType) = _state.update { it.copy(documentType = t) }

    fun search() = viewModelScope.launch {
        val nif = _state.value.nif
        if (nif.length < 9) { _state.update { it.copy(error = "NIF inválido") }; return@launch }
        _state.update { it.copy(loading = true, error = null) }
        apply(repo.lookup(nif))
    }



    fun createCustomer(form: NewCustomerForm) = viewModelScope.launch {
        _state.update { it.copy(loading = true) }
        apply(
            repo.create(
                CreateCustomerRequest(
                    name = form.name.trim(),
                    taxNumber = _state.value.nif,
                    customerType = form.type,
                    // vazio vira null: evita colisão no unique do email
                    phone = form.phone.trim().takeIf { it.isNotEmpty() },
                    email = form.email.trim().takeIf { it.isNotEmpty() },
                    address = form.address.trim().takeIf { it.isNotEmpty() }
                )
            )
        )
    }

    private fun apply(r: LookupResult) = _state.update {
        when (r) {
            is LookupResult.Found -> it.copy(loading = false, customer = r.customer, notFound = false)
            LookupResult.NotFound -> it.copy(loading = false, notFound = true)
            is LookupResult.Error -> it.copy(loading = false, error = r.message)
        }
    }

    fun reset() { _state.value = CheckoutState() }
}
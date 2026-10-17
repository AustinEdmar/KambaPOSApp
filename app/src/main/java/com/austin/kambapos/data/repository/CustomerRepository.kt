package com.austin.kambapos.data.repository

import com.austin.kambapos.data.api.CustomerApi
import com.austin.kambapos.data.model.CreateCustomerRequest
import com.austin.kambapos.data.model.CustomerDTO
import com.austin.kambapos.data.model.LookupRequest
import jakarta.inject.Inject

sealed interface LookupResult {
    data class Found(val customer: CustomerDTO) : LookupResult
    data object NotFound : LookupResult
    data class Error(val message: String) : LookupResult
}

class CustomerRepository @Inject constructor(private val api: CustomerApi) {

    suspend fun lookup(nif: String): LookupResult = try {
        val r = api.lookup(LookupRequest(nif))
        when {
            r.isSuccessful -> r.body()?.customer?.let { LookupResult.Found(it) } ?: LookupResult.NotFound
            r.code() == 404 -> LookupResult.NotFound
            else -> LookupResult.Error("Erro ${r.code()}")
        }
    } catch (e: Exception) {
        LookupResult.Error("Sem ligação ao servidor")
    }

    suspend fun create(req: CreateCustomerRequest): LookupResult {
        return try {
            val r = api.create(req)
            when {
                r.isSuccessful -> LookupResult.Found(r.body()!!)
                // 422: NIF já existe (outro terminal criou entretanto)
                r.code() == 422 -> when (val l = lookup(req.taxNumber)) {
                    is LookupResult.Found -> l                       // NIF já existia
                    else -> LookupResult.Error("Dados inválidos (email já registado?)")
                }
                else -> LookupResult.Error("Erro ${r.code()}")
            }
        } catch (e: Exception) {
            LookupResult.Error("Sem ligação ao servidor")
        }
    }
}
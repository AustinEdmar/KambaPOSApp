package com.austin.kambapos.data.model

// Sealed class para representar o resultado da sync
sealed class SyncResult {
    object Success : SyncResult()
    data class NetworkError(val message: String) : SyncResult()
    data class ServerError(val code: Int, val message: String) : SyncResult()
    data class UnknownError(val message: String) : SyncResult()
}
package com.austin.kambapos.utils

import com.google.gson.Gson
import retrofit2.HttpException

fun HttpException.parseErrorMessage(): String {
    return try {
        val errorBody = this.response()?.errorBody()?.string()
        val gson = Gson()
        val json = gson.fromJson(errorBody, Map::class.java)
        json["message"] as? String ?: "Erro desconhecido"
    } catch (e: Exception) {
        "Erro desconhecido"
    }
}
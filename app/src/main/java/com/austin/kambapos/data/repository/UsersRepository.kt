package com.austin.kambapos.data.repository

import com.austin.kambapos.data.api.UsersApi
import com.austin.kambapos.data.local.dao.UsersDao
import com.austin.kambapos.data.local.entity.UsersEntity
import com.austin.kambapos.data.local.mapper.toEntity
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val api: UsersApi,
    private val usersDao: UsersDao
) {
    suspend fun syncUsers() {
        val response = api.getUsers()
        usersDao.insertUsers(response.map { it.toEntity() })
    }

    suspend fun findByPin(pin: String): UsersEntity? {
        return usersDao.findByPin(pin)
    }
}
package com.austin.kambapos.data.repository

import android.util.Log
import com.austin.kambapos.data.api.ProductApi
import com.austin.kambapos.data.local.dao.CartDao
import com.austin.kambapos.data.local.dao.CategoryDao
import com.austin.kambapos.data.local.dao.ProductDao
import com.austin.kambapos.data.local.entity.CategoryEntity
import com.austin.kambapos.data.local.entity.ProductEntity
import com.austin.kambapos.data.local.mapper.toEntity
import com.austin.kambapos.data.model.ProductDTO
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductRepository @Inject constructor(
    private val api: ProductApi,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val cartDao: CartDao
) {
    // 🔹 UI sempre observa o banco
    fun observeProducts(
        categoryId: Int?,
        search: String?
    ): Flow<List<ProductEntity>> {
        return productDao.observeProducts(categoryId, search)
    }

    fun observeCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.observeCategories()
    }

    // 🔹 Sincronização remota


    fun startAutoSync(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                try {
                    syncAll()
                } catch (e: Exception) {
                    Log.e("SYNC", "Erro", e)
                }

                delay(10000) // 10 segundos
            }
        }
    }

    suspend fun syncAll() {
        try {
            val allProducts = mutableListOf<ProductDTO>()
            var page = 1
            var lastPage: Int

            do {
                val response = api.getProducts(page = page, perPage = 100)
                allProducts += response.data
                lastPage = response.lastPage
                page++
            } while (page <= lastPage)

            val categoriesResponse = api.getCategories()

            val productEntities = allProducts.map { it.toEntity() }
            val categoryEntities = categoriesResponse.data.map { it.toEntity() }

            categoryDao.clearAll()
            categoryDao.insertAll(categoryEntities)

            productDao.clearAll()
            productDao.insertAll(productEntities)

            Log.d("SYNC_DEBUG", "Produtos inseridos: ${productEntities.size}")
        } catch (e: Exception) {
            Log.e("SYNC_ERROR", "Erro sync", e)
        }
    }


}
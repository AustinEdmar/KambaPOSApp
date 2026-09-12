package com.austin.kambapos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


//
@HiltViewModel
class ProductViewModel @Inject constructor(

    private val productRepository: ProductRepository

) : ViewModel() {



    val selectedCategory = MutableStateFlow<Int?>(null)
    private val searchQuery = MutableStateFlow<String?>(null)

    val products = combine(
        selectedCategory,
        searchQuery
    ) { category, search ->
        category to search
    }.flatMapLatest { (category, search) ->
        productRepository.observeProducts(category, search)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )



    val categories = productRepository.observeCategories()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    /// product
    init {

        viewModelScope.launch {
            productRepository.startAutoSync(viewModelScope)
            //productRepository.syncAll()
            delay(5000)

            val test = productRepository.observeProducts(null, null).first()
            Log.d("ROOM_TEST", "Produtos no banco: ${test}")
        }
    }


    /// product


    fun onCategorySelected(categoryId: Int?) {

        searchQuery.value = null
        selectedCategory.value = categoryId

    }


    fun onSearchChanged(query: String?) {
        searchQuery.value = query
    }



}

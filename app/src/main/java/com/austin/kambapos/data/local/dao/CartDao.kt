package com.austin.kambapos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.austin.kambapos.data.local.entity.CartItemEntity
import com.austin.kambapos.data.local.entity.CartItemWithProduct
import com.austin.kambapos.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Transaction
    @Query("""
    SELECT * FROM cart_items
    WHERE orderId = :orderId AND quantity > 0
""")
    fun getCart(orderId: Int?): Flow<List<CartItemWithProduct>>

    @Query("""
        SELECT * FROM cart_items
        WHERE orderId = :orderId AND productId = :productId
        LIMIT 1
    """)
    suspend fun getItem(
        orderId: Int?,
        productId: Int
    ): CartItemEntity?


    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    //    suspend fun insert(item: CartItemEntity)
    // Muda para IGNORE — quem gerencia conflito é a lógica, não o Room
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: CartItemEntity): Long  // retorna -1 se ignorou

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("""
        UPDATE products 
        SET stock = stock - 1 
        WHERE id = :productId AND stock > 0
    """)
    suspend fun decrementStock(productId: Int): Int


    @Query("""
    UPDATE products 
    SET stock = stock + 1
    WHERE id = :productId
""")
    suspend fun incrementStock(productId: Int)

    @Query("""
    UPDATE cart_items
    SET quantity = quantity - 1,
        pendingSync = 1
    WHERE id = :itemId
""")
    suspend fun decrementQuantity(itemId: Int)

    @Query("""
    DELETE FROM cart_items
    WHERE id = :itemId AND orderId = :orderId
""")
    suspend fun deleteItem(itemId: Int, orderId: Int?)




    @Query("""
    DELETE FROM products
    WHERE id = :productId
""")
    suspend fun deleteProduct(productId: Int)


    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("""
    SELECT * FROM products
    WHERE id = :productId
    LIMIT 1
""")
    suspend fun getProductById(productId: Int): ProductEntity?

    @Update
    suspend fun update(item: CartItemEntity)

    @Query("""
        DELETE FROM cart_items
        WHERE orderId = :orderId
    """)
    suspend fun clearCart(orderId: Int?)


    @Query("""
        SELECT * FROM cart_items
        WHERE pendingSync = 1
    """)
    suspend fun getPendingItems(): List<CartItemEntity>



    @Query("""
UPDATE cart_items
SET 
    quantity = quantity + 1,
    delta = delta + 1,
    pendingSync = 1,
     syncVersion = syncVersion + 1   -- ← adicionar aqui
WHERE orderId = :orderId AND productId = :productId
""")
    suspend fun increaseQuantity(orderId: Int?, productId: Int): Int


    @Query("""
    UPDATE cart_items
    SET 
        quantity = quantity - 1,
        delta = delta - 1,
        pendingSync = 1,
        syncVersion = syncVersion + 1,
        pendingAuthorizationPin = :pin
    WHERE id = :itemId
""")
    suspend fun decreaseQuantity(itemId: Int, pin: String?)

    @Transaction
    suspend fun decreaseQuantityTransaction(item: CartItemEntity, pin: String?) {
        incrementStock(item.productId)
        decreaseQuantity(item.id, pin)
    }



    @Transaction
    suspend fun addToCartTransaction(orderId: Int?, product: ProductEntity) {

        insertProduct(product)

        val rowsAffected = decrementStock(product.id)
        if (rowsAffected == 0) {
            throw Exception("Produto sem estoque")
        }

        // se já existe linha para este produto no pedido, o preço congelado
        // da PRIMEIRA unidade prevalece — não se re-congela a cada +1,
        // tal como a API só grava unit_price uma vez no primeiro addItem()
        val rows = increaseQuantity(orderId, product.id)

        if (rows == 0) {
            insert(
                CartItemEntity(
                    orderId = orderId,
                    productId = product.id,
                    quantity = 1,
                    delta = 1,
                    pendingSync = true,
                    unitPriceSnapshot = product.price,
                    ivaRateSnapshot = product.taxPercentage ?: 0.0
                )
            )
        }
    }

    @Transaction
    suspend fun increaseQuantityTransaction(item: CartItemEntity) {

        val rowsAffected = decrementStock(item.productId)

        if (rowsAffected == 0) {
            throw Exception("Produto sem estoque")
        }

        increaseQuantity(item.orderId, item.productId)
    }





    @Query("""
    UPDATE cart_items
    SET quantity = quantity
    WHERE productId IN (:productIds)
""")
    suspend fun refreshCartItems(productIds: List<Int>)



    @Query("SELECT * FROM cart_items")
    suspend fun getAllItems(): List<CartItemEntity>


    //se um produto não estiver presente em nenhum item do carrinho (cart_items),
    // //ele deve ser deletado da tabela products. Isso pode ser feito diretamente
    // com uma query que faz DELETE condicional usando NOT EXISTS ou LEFT JOIN.
    @Query("""
    DELETE FROM products
    WHERE id = :productId
    AND NOT EXISTS (
        SELECT 1 FROM cart_items
        WHERE productId = :productId
    )
""")
    suspend fun deleteProductIfNotInCart(productId: Int)


    @Query("""
    DELETE FROM orders
    WHERE id = :orderId
    AND NOT EXISTS (
        SELECT 1 FROM cart_items
        WHERE orderId = :orderId
    )
""")
    suspend fun deleteOrderIfNotInCart(orderId: Int?)


}

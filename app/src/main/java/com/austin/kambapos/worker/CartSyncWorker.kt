package com.austin.kambapos.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.austin.kambapos.data.repository.CartRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CartSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: CartRepository // 🔥 INJETADO PELO HILT
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("SYNCWORKER", "Worker executando")

        return try {

            val success = repository.syncCart()

            if (success) {
                Result.success()
            } else {
                Result.retry()
            }

        } catch (e: Exception) {
            Result.retry()
        }
    }
}
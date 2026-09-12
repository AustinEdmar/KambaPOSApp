package com.austin.kambapos.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import java.util.concurrent.TimeUnit

class CartSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun schedule() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<CartSyncWorker>()
            .setConstraints(constraints)
           // .setInitialDelay(3, TimeUnit.SECONDS) // Aguarda acumular operações
            .setInitialDelay(500, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "cart_sync",
              //  ExistingWorkPolicy.KEEP, // ✅ Mantém o worker atual se já estiver rodando
                //ExistingWorkPolicy.REPLACE, // 🔥 Trocar aqui
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                request
            )
    }
}

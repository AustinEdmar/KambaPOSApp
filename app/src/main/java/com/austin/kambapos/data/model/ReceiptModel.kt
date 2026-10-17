package com.austin.kambapos.data.model

data class ReceiptCompany(
    val name: String? = null,
    val nif: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val certificateNumber: String? = null
)



data class ReceiptLine(
    val code: String? = null,
    val name: String,
    val quantity: Double,
    val unitPrice: Double,
    val taxRate: Double,
    val net: Double
)

data class ReceiptTaxLine(val rate: Double, val base: Double, val tax: Double)

data class ReceiptModel(
    val lines: List<ReceiptLine>,
    val subtotal: Double,
    val discount: Double = 0.0,
    val tax: Double,
    val total: Double,

    val orderId: Int?,
    val paymentMethod: String,
    val paidAmount: Double? = null,
    val change: Double? = null,
    val copyType: String = "ORIGINAL",

    val documentType: String = "FR",
    val documentNumber: String? = null,
    val issuedAt: String? = null,
    val hash4: String? = null,
    val qrContent: String? = null,
    val legalText: String? = null,
    val terminal: String? = null,
    val itemsCount: Int = 0,
    val taxSummary: List<ReceiptTaxLine> = emptyList(),
    val customerName: String = "Consumidor Final",
    val customerTaxNumber: String = "999999999",
    val customerAddress: String? = null,
    val operatorName: String? = null,
    val company: ReceiptCompany? = null
)
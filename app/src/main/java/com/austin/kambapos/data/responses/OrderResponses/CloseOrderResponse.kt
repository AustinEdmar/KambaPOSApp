package com.austin.kambapos.data.responses.OrderResponses

import com.google.gson.annotations.SerializedName

data class CloseOrderResponse(
    val message: String? = null,
    @SerializedName("order_id") val orderId: Int? = null,
    @SerializedName("invoice_id") val invoiceId: Long? = null,
    @SerializedName("invoice_number") val invoiceNumber: String? = null,
    @SerializedName("agt_document_no") val agtDocumentNo: String? = null,
    @SerializedName("document_type") val documentType: String? = null,
    @SerializedName("issued_at") val issuedAt: String? = null,
    val hash4: String? = null,
    @SerializedName("qr_content") val qrContent: String? = null,
    @SerializedName("legal_text") val legalText: String? = null,
    val terminal: String? = null,
    @SerializedName("items_count") val itemsCount: Int? = null,
    val subtotal: Double? = null,
    val discount: Double? = null,
    val tax: Double? = null,
    val total: Double? = null,
    @SerializedName("tax_summary") val taxSummary: List<TaxSummaryResponse>? = null,
    val customer: CloseCustomer? = null,
    val operator: String? = null,
    val payment: PaymentResponse? = null,
    val items: List<OrderItemResponse>? = null,
    val company: CloseCompany? = null
)

data class OrderItemResponse(
    val code: String? = null,
    val description: String? = null,
    val quantity: Double? = null,
    @SerializedName("unit_price") val unitPrice: Double? = null,
    @SerializedName("tax_rate") val taxRate: Double? = null,
    val net: Double? = null
)

data class TaxSummaryResponse(
    val rate: Double? = null,
    val base: Double? = null,
    val tax: Double? = null
)

data class PaymentResponse(
    val method: String? = null,
    val received: Double? = null,
    val change: Double? = null
)

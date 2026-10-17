package com.austin.kambapos.data.model

enum class DocumentType(
    val apiValue: String,
    val label: String,
    val hint: String
) {
    FR("FR", "Factura-Recibo", "Pago no acto"),
    TV("TV", "Talão de venda", "Venda a dinheiro"),
    FT("FT", "Factura", "Pagamento posterior")
}
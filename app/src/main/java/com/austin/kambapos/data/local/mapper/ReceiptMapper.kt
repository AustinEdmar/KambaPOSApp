package com.austin.kambapos.data.local.mapper

import com.austin.kambapos.data.model.CartItemDTO
import com.austin.kambapos.data.model.ReceiptCompany
import com.austin.kambapos.data.model.ReceiptLine
import com.austin.kambapos.data.model.ReceiptModel
import com.austin.kambapos.data.model.ReceiptTaxLine
import com.austin.kambapos.data.responses.OrderResponses.CloseOrderResponse
import com.austin.kambapos.utils.toKwanza

fun CloseOrderResponse.toReceiptModel(copyType: String) = ReceiptModel(
    lines = items.orEmpty().map {
        ReceiptLine(
            code = it.code,
            name = it.description ?: "",
            quantity = it.quantity ?: 0.0,
            unitPrice = it.unitPrice ?: 0.0,
            taxRate = it.taxRate ?: 0.0,
            net = it.net ?: 0.0
        )
    },
    subtotal = subtotal ?: 0.0,
    discount = discount ?: 0.0,
    tax = tax ?: 0.0,
    total = total ?: 0.0,
    orderId = orderId,
    paymentMethod = payment?.method ?: "cash",
    paidAmount = payment?.received,
    change = payment?.change,
    copyType = copyType,
    documentType = documentType ?: "FR",
    documentNumber = agtDocumentNo ?: invoiceNumber,
    issuedAt = issuedAt,
    hash4 = hash4,
    qrContent = qrContent,
    legalText = legalText,
    terminal = terminal,
    itemsCount = itemsCount ?: 0,
    taxSummary = taxSummary.orEmpty().map {
        ReceiptTaxLine(it.rate ?: 0.0, it.base ?: 0.0, it.tax ?: 0.0)
    },
    customerName = customer?.name ?: "Consumidor Final",
    customerTaxNumber = customer?.taxNumber ?: "999999999",
    customerAddress = customer?.address,
    operatorName = operator,
    company = company?.let {
        ReceiptCompany(it.name, it.nif, it.address, it.phone, it.certificateNumber)
    }
)

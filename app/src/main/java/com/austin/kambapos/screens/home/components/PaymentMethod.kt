package com.austin.kambapos.screens.home.components

enum class PaymentMethod(val apiValue: String) {
    CASH("cash"),
    CARD("card"),
    QRCODE("qrcode"),
    BANK_TRANSFER("bank_transfer")
}

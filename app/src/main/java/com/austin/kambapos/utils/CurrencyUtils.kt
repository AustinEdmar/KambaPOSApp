package com.austin.kambapos.utils

// CurrencyUtils.kt

// 🔹 para input (MANTER)
fun formatToKwanza(value: String): String {
    val digits = value.filter { it.isDigit() }
    if (digits.isEmpty()) return ""

    val number = digits.toLongOrNull() ?: return ""
    val intPart = number / 100
    val decPart = number % 100

    val formatted = "%,d".format(intPart).replace(',', '.')
    return "$formatted,${"%02d".format(decPart)} Kz"
}

// 🔹 para converter de volta (MANTER)
fun parseKwanzaToDouble(formatted: String): Double {
    val clean = formatted
        .replace(" Kz", "")
        .replace(".", "")
        .replace(",", ".")
    return clean.toDoubleOrNull() ?: 0.0
}

// 🔥 NOVO (profissional)
fun Double?.toKwanza(): String {
    if (this == null) return "0,00 Kz"

    val intPart = this.toInt()
    val decPart = ((this - intPart) * 100).toInt()

    val formatted = "%,d".format(intPart).replace(',', '.')
    return "$formatted,${"%02d".format(decPart)} Kz"
}
package com.austin.kambapos.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.math.BigDecimal
import java.math.RoundingMode
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

    // BigDecimal a partir da STRING (não do Double directamente) evita
    // herdar o ruído binário do Double antes mesmo de arredondar
    val bd = BigDecimal(this.toString()).setScale(2, RoundingMode.HALF_UP)

    val intPart = bd.toBigInteger()
    val decPart = bd.subtract(BigDecimal(intPart)).movePointRight(2).toInt()

    val formatted = "%,d".format(intPart).replace(',', '.')
    return "$formatted,${"%02d".format(decPart)} Kz"
}

class KwanzaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = formatToKwanza(digits)

        if (digits.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var digitCount = 0
                var transformedOffset = 0
                for (char in formatted) {
                    if (digitCount == offset) break
                    if (char.isDigit()) digitCount++
                    transformedOffset++
                }
                return transformedOffset.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                var digitCount = 0
                for (i in 0 until offset.coerceAtMost(formatted.length)) {
                    if (formatted[i].isDigit()) digitCount++
                }
                return digitCount.coerceAtMost(digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
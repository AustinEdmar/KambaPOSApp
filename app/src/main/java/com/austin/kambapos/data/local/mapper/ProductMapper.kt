package com.austin.kambapos.data.local.mapper

import com.austin.kambapos.data.local.entity.ProductEntity
import com.austin.kambapos.data.model.ProductDTO

fun ProductDTO.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        productCode = productCode,
        description = description,
        price = price,
        stock = stock,
        taxRateId = taxRateId,
        taxCode = taxRateDTO?.taxCode,
        taxPercentage = taxRateDTO?.taxPercentage?.toDouble(),
        barcode = barcode,
        imageUrl = imagePath,
        categoryId = categoryDTO.id
    )
}

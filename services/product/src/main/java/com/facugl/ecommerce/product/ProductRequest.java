package com.facugl.ecommerce.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequest(
                Integer id,
                @NotNull(message = "Product name is required") String name,
                @NotNull(message = "Product description is required") String description,
                @Positive(message = "Availeable quantity should be positive") double availableQuantity,
                @Positive(message = "Price should be positive") BigDecimal price,
                @NotNull(message = "Product category is required") Integer categoryId) {

}

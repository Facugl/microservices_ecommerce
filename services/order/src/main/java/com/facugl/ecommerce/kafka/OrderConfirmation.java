package com.facugl.ecommerce.kafka;

import java.math.BigDecimal;
import java.util.List;

import com.facugl.ecommerce.customer.CustomerResponse;
import com.facugl.ecommerce.order.PaymentMethod;
import com.facugl.ecommerce.product.PurchaseResponse;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products) {

}

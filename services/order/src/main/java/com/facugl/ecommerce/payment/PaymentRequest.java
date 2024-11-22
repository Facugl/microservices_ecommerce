package com.facugl.ecommerce.payment;

import java.math.BigDecimal;

import com.facugl.ecommerce.customer.CustomerResponse;
import com.facugl.ecommerce.order.PaymentMethod;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer) {

}

package com.facugl.ecommerce.notification;

import java.math.BigDecimal;

import com.facugl.ecommerce.payment.PaymentMethod;

public record PaymentNotificationRequest(
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String customerFirstName,
        String customerLastname,
        String customerEmail) {

}
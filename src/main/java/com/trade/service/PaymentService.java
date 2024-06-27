package com.trade.service;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import com.trade.Entity.PaymentOrder;
import com.trade.Entity.User;
import com.trade.domain.PaymentMethod;
import com.trade.response.PaymentResponse;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean proceedPaymentOrder(PaymentOrder paymentOrder,String paymentId) throws RazorpayException;

    PaymentResponse createRazorPayPaymentLink(User user, Long amount) throws RazorpayException;

    PaymentResponse createStripePaymentLink(User user, Long amount,Long OrderId) throws StripeException;
}

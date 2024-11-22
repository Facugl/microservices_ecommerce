package com.facugl.ecommerce.order;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.facugl.ecommerce.customer.CustomerClient;
import com.facugl.ecommerce.exception.BusinessException;
import com.facugl.ecommerce.kafka.OrderConfirmation;
import com.facugl.ecommerce.kafka.OrderProducer;
import com.facugl.ecommerce.orderLine.OrderLineRequest;
import com.facugl.ecommerce.orderLine.OrderLineService;
import com.facugl.ecommerce.payment.PaymentClient;
import com.facugl.ecommerce.payment.PaymentRequest;
import com.facugl.ecommerce.product.ProductClient;
import com.facugl.ecommerce.product.PurchaseRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final CustomerClient customerClient;
	private final ProductClient productClient;
	private final OrderRepository repository;
	private final OrderMapper mapper;
	private final OrderLineService orderLineService;
	private final OrderProducer orderProducer;
	private final PaymentClient paymentClient;

	public Integer createOrder(OrderRequest request) {
		// check the customer --> OpenFeign
		var customer = this.customerClient.findCustomerById(request.customerId())
				.orElseThrow(
						() -> new BusinessException(
								"Cannot create order:: No Customer exists with the provided ID"));

		// purchase the products --> product-ms (REST Template)
		var purchasedProducts = this.productClient.purchaseProducts(request.products());

		// persist order
		var order = this.repository.save(mapper.toOrder(request));

		// persist order lines
		for (PurchaseRequest purchaseRequest : request.products()) {
			orderLineService.saveOrderLine(
					new OrderLineRequest(
							null,
							order.getId(),
							purchaseRequest.productId(),
							purchaseRequest.quantity()));
		}

		// start payment process
		var paymentRequest = new PaymentRequest(
				request.amount(),
				request.paymentMethod(),
				order.getId(),
				order.getReference(),
				customer);
		paymentClient.requestOrderedPayment(paymentRequest);

		// send the order confirmation --> notification-ms (kafka)
		orderProducer.sendOrderConfirmation(
				new OrderConfirmation(
						request.reference(),
						request.amount(),
						request.paymentMethod(),
						customer,
						purchasedProducts));

		return order.getId();
	}

	public List<OrderResponse> findAll() {
		return repository.findAll()
				.stream()
				.map(mapper::fromOrder)
				.collect(Collectors.toList());
	}

	public OrderResponse findById(Integer orderId) {
		return repository.findById(orderId)
				.map(mapper::fromOrder)
				.orElseThrow(() -> new EntityNotFoundException(
						String.format("No order found with the provided ID: %d", orderId)));
	}

}

package com.m.orderservice.service;

import com.m.orderservice.dto.OrderItemRequest;
import com.m.orderservice.dto.OrderRequest;
import com.m.orderservice.model.Order;
import com.m.orderservice.model.OrderItem;
import com.m.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void createOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderItem> orderItems = orderRequest.getOrderItemRequests()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderItems(orderItems);

        orderRepository.save(order);
    }

    private OrderItem mapToDto(OrderItemRequest orderItemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(orderItemRequest.getPrice());
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setSkuCode(orderItemRequest.getSkuCode());
        return orderItem;
    }
}

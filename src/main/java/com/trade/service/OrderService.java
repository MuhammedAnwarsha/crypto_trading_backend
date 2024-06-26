package com.trade.service;

import com.trade.Entity.Coin;
import com.trade.Entity.Order;
import com.trade.Entity.OrderItem;
import com.trade.Entity.User;
import com.trade.domain.OrderType;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId) throws Exception;

    List<Order> getAllOrdersOfUser(Long userId,OrderType orderType,String assetSymbol);

    Order processOrder(Coin coin,double quantity,OrderType orderType,User user) throws Exception;
}

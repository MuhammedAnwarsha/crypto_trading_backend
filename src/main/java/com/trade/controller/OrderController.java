package com.trade.controller;

import com.trade.Entity.Coin;
import com.trade.Entity.Order;
import com.trade.Entity.User;
import com.trade.domain.OrderType;
import com.trade.request.CreateOrderRequest;
import com.trade.service.CoinService;
import com.trade.service.OrderService;
import com.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(@RequestHeader("Authorization")String jwt,
                                                 @RequestBody CreateOrderRequest req)throws Exception{

        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findById(req.getCoinId());

        Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(),user);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@RequestHeader("Authorization")String jwt,
                                              @PathVariable Long orderId)throws Exception{

        User user = userService.findUserProfileByJwt(jwt);
        Order order = orderService.getOrderById(orderId);
        if (order.getUser().getId().equals(user.getId())){

            return ResponseEntity.ok(order);
        }else {
            throw new Exception("you don't have access");
        }
    }

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersForUser(@RequestHeader("Authorization")String jwt,
                                                           @RequestParam(required = false)OrderType orderType,
                                                           @RequestParam(required = false)String assetSymbol)throws Exception{

        Long userId = userService.findUserProfileByJwt(jwt).getId();

        List<Order> userOrders = orderService.getAllOrdersOfUser(userId,orderType,assetSymbol);

        return ResponseEntity.ok(userOrders);
    }
}

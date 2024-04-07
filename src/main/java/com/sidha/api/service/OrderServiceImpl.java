package com.sidha.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.request.CreateOrderRequestDTO;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.user.Klien;
import com.sidha.api.repository.OrderDb;
import com.sidha.api.repository.UserDb;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderDb orderRepository;
  
  @Autowired
  private UserDb userRepository;

  @Autowired
  private OrderItemService orderItemService;

  @Override
  public Order save(Order order) {
    return orderRepository.save(order);

  }

  @Override
  public Order saveOrder(CreateOrderRequestDTO request) {
    var listOrderItemDTO = request.getOrderItems();
    var user = userRepository.findById(request.getUserId()).orElse(null);
    if (user == null) {
      return null;
    } else{
      var order = new Order();
      order.setKlien((Klien) user);
      order.setTanggalPengiriman(request.getTanggalPengiriman());
      var orderSaved = orderRepository.save(order);
      
      for (var orderItemDTO : listOrderItemDTO) {
        orderItemService.saveOrderItem(orderItemDTO, orderSaved, (Klien) user);
      }

      var totalPrice = 0;
      for (var orderItem : orderSaved.getOrderItems()) {
        totalPrice += orderItem.getPrice();
      }
      orderSaved.setTotalPrice(totalPrice);
      orderRepository.save(orderSaved);

      return orderSaved;
    }

  }

  @Override
  public Order updateOrder(CreateOrderRequestDTO request) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateOrder'");
  }

  @Override
  public Order setStatusOrder(String idOrder, String status) {
    var orderUUID = UUID.fromString(idOrder);
    var order = orderRepository.findById(orderUUID).orElse(null);
    if (order == null) {
      return null;
    }
    return orderRepository.save(order);
  }

  @Override
  public Order getOrderById(String idOrder) {
    UUID orderUUID = UUID.fromString(idOrder);
    return orderRepository.findById(orderUUID).orElse(null);
  }

  @Override
  public Order deleteOrder(String idOrder) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteOrder'");
  }

  @Override
  public List<Order> getAllOrder() {
    return orderRepository.findAll();
  }

}

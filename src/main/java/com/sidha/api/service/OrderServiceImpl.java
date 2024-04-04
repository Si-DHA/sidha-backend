package com.sidha.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.request.CreateOrderRequestDTO;
import com.sidha.api.model.Order;
import com.sidha.api.model.enumerator.StatusOrder;
import com.sidha.api.repository.OrderDb;
import com.sidha.api.repository.UserDb;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderDb orderRepository;
  
  @Autowired
  private UserDb userRepository;

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
      order.setUser(user);
      order.setStatus(StatusOrder.DIBUAT);
      var orderSaved = orderRepository.save(order);
      for (var orderItemDTO : listOrderItemDTO) {
      }

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
    order.setStatus(StatusOrder.valueOf(status));
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

  @Override
  public List<Order> getOrderByStatus(StatusOrder status) {
    return orderRepository.findByStatus(status);
  }

}

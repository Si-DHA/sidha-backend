package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidha.api.DTO.request.CreateOrderRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/api/order")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @Autowired
  private ObjectMapper mapper;

  @GetMapping("/detail")
  public ResponseEntity<?> getDetailOrder(@RequestParam String uuid) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(new BaseResponse<>(true, 200, "Success", orderService.getOrderById(uuid)));
  }

  @PostMapping("/buat")
  public ResponseEntity<?> createNewOrder(@RequestBody CreateOrderRequestDTO entity) {
    try {
      var order = orderService.saveOrder(entity);
      var orderJson = mapper.writeValueAsString(order);
      System.err.println(orderJson);
      return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Success", order));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(false, 400, e.getMessage(), null));
    }
  }

}

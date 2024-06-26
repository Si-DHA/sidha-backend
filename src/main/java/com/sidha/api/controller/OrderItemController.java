package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sidha.api.service.OrderItemService;
import com.sidha.api.DTO.response.BaseResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order-item")
public class OrderItemController {

  @Autowired
  private OrderItemService orderItemService;

  @GetMapping("/revenue/today")
  public ResponseEntity<?> getTotalRevenueForToday() {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getTotalRevenueForToday()));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/revenue/weekly")
  public ResponseEntity<?> getWeeklyRevenueInMonth(
      @RequestParam int year,
      @RequestParam int month) {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getWeeklyRevenueInMonth(year, month)));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/revenue/monthly")
  public ResponseEntity<?> getMonthlyRevenueInYear(
      @RequestParam int year) {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getMonthlyRevenueInYear(year)));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/revenue/yearly")
  public ResponseEntity<?> getYearlyRevenueInRange(
      @RequestParam int startYear,
      @RequestParam int endYear) {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getYearlyRevenueInRange(startYear, endYear)));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/orders/count-by-status")
  public ResponseEntity<?> getOrderCountByStatus() {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getOrderCountByStatus()));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/revenue/total")
  public ResponseEntity<?> getTotalRevenueForThisWeek() {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getTotalRevenue()));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/orders/completed")
  public ResponseEntity<?> getTotalCompletedOrderItem() {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getTotalCompletedOrderItem()));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/orders/weekly")
  public ResponseEntity<?> getTotalOrderWeeklyByStatus(
      @RequestParam("year") int year,
      @RequestParam("month") int month,
      @RequestParam("status") int status) {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getWeeklyOrder(year, month, status)));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/orders/monthly")
  public ResponseEntity<?> getTotalOrderMonthlyByStatus(
      @RequestParam("year") int year,
      @RequestParam("status") int status) {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getMonthlyOrder(year, status)));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/orders/yearly")
  public ResponseEntity<?> getTotalOrderYearlyByStatus(
      @RequestParam("startYear") int startYear,
      @RequestParam("endYear") int endYear,

      @RequestParam("status") int status) {
    try {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getYearlyOrder(startYear, endYear, status)));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
    }
  }

  @GetMapping("/revenue/list")
  public ResponseEntity<?> getListRevenue(@RequestParam String timeRange) {
    if (timeRange.equals("today")) {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getListRevenueForToday()));
    } else if (timeRange.equals("week")){
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getListRevenueForThisWeek()));
    } else if (timeRange.equals("month")){
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getListRevenueForThisMonth()));
    } else if (timeRange.equals("year")){
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getListRevenueForThisYear()));
    } else {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", "Invalid time range"));
    }
  }

  @GetMapping("/orders/list")
  public ResponseEntity<?> getListOrder(@RequestParam String timeRange, @RequestParam int status) {
    if (timeRange.equals("today")) {
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getListOrderForToday(status)));
    } else if (timeRange.equals("week")){
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getListOrderForThisWeek(status)));
    } else if (timeRange.equals("month")){
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getListOrderForThisMonth(status)));
    } else if (timeRange.equals("year")){
      return ResponseEntity.ok()
          .body(new BaseResponse<>(true, 200, "Success", orderItemService.getListOrderForThisYear(status)));
    } else {
      return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", "Invalid time range"));
    }
  }

}
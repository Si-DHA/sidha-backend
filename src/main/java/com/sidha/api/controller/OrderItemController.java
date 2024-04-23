package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sidha.api.service.OrderItemService;

import java.util.Map;

@RestController
public class OrderItemController {

  @Autowired
  private OrderItemService orderItemService;

  @GetMapping("/revenue/today")
  public Long getTotalRevenueForToday() {
    return orderItemService.getTotalRevenueForToday();
  }

  @GetMapping("/revenue/weekly")
  public Map<Integer, Long> getWeeklyRevenueInMonth(
      @RequestParam int year,
      @RequestParam int month) {
    return orderItemService.getWeeklyRevenueInMonth(year, month);
  }

  @GetMapping("/revenue/monthly")
  public Map<Integer, Long> getMonthlyRevenueInYear(
      @RequestParam int year) {
    return orderItemService.getMonthlyRevenueInYear(year);
  }

  @GetMapping("/revenue/yearly")
  public Map<Integer, Long> getYearlyRevenueInRange(
      @RequestParam int startYear,
      @RequestParam int endYear) {
    return orderItemService.getYearlyRevenueInRange(startYear, endYear);
  }

  @GetMapping("/orders/count-by-status")
  public Map<Integer, Long> getOrderCountByStatus() {
    return orderItemService.getOrderCountByStatus();
  }
}
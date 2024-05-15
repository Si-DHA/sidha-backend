package com.sidha.api.service;

import java.util.List;

import com.sidha.api.model.order.OrderItem;
import java.util.Map;

public interface OrderItemService {
  Long getTotalRevenueForToday();

  Long getTotalRevenueForThisWeek();

  Long getTotalRevenueForThisMonth();

  Long getTotalRevenueForThisYear();

  List<List<Object>> getTotalRevenue();

  List<List<Object>> getWeeklyRevenueInMonth(int year, int month);

  List<List<Object>> getMonthlyRevenueInYear(int year);

  List<List<Object>> getYearlyRevenueInRange(int startYear, int endYear);

  List<List<Object>> getOrderCountByStatus();

  List<List<Object>> getTotalCompletedOrderItem();

  List<List<Object>> getWeeklyOrder(int year, int month, int status);

  List<List<Object>> getMonthlyOrder(int year, int status);

  List<List<Object>> getYearlyOrder(int startYear, int endYear, int status);

  List<Map<String, Object>> getListRevenueForToday();

  List<Map<String, Object>> getListRevenueForThisWeek();

  List<Map<String, Object>> getListRevenueForThisMonth();

  List<Map<String, Object>> getListRevenueForThisYear();

  List<OrderItem> getListOrderForToday(int status);

  List<OrderItem> getListOrderForThisWeek(int status);

  List<OrderItem> getListOrderForThisMonth(int status);

  List<OrderItem> getListOrderForThisYear(int status);
} 

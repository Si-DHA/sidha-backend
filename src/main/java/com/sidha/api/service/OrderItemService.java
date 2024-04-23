package com.sidha.api.service;

import java.util.Map;

public interface OrderItemService {
  Long getTotalRevenueForToday();

  Map<Integer, Long> getWeeklyRevenueInMonth(int year, int month);

  Map<Integer, Long> getMonthlyRevenueInYear(int year);

  Map<Integer, Long> getYearlyRevenueInRange(int startYear, int endYear);

  Map<Integer, Long> getOrderCountByStatus();
}

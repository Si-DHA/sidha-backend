package com.sidha.api.service;

import java.util.List;
public interface OrderItemService {
  Long getTotalRevenueForToday();
  
  Double getTotalRevenueForThisWeek();

  Double getTotalRevenueForThisMonth();

  Double getTotalRevenueForThisYear();

  List<List<Object>>  getWeeklyRevenueInMonth(int year, int month);

  List<List<Object>>  getMonthlyRevenueInYear(int year);

  List<List<Object>>  getYearlyRevenueInRange(int startYear, int endYear);

  List<List<Object>>  getOrderCountByStatus();




}

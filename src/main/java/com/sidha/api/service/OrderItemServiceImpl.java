package com.sidha.api.service;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.glassfish.jaxb.core.annotation.OverrideAnnotationOf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.model.order.OrderItem;
import com.sidha.api.repository.OrderItemDb;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemDb orderItemDb;

    @Override
    public List<List<Object>> getWeeklyRevenueInMonth(int year, int month) {
        // Generate all weeks in the given month
        List<Integer> allWeeks = generateAllWeeksInMonth(year, month);

        // Get the revenue data from the database
        List<Object[]> revenueData = orderItemDb.getWeeklyRevenueInMonth(year, month);

        // Create a map for quick lookup
        Map<Integer, Object[]> revenueDataMap = new HashMap<>();
        for (Object[] data : revenueData) {
            int week = ((Number) data[0]).intValue();
            revenueDataMap.put(week, data);
        }

        // Get the first week in the month
        int firstWeek = allWeeks.get(0);

        // Fill the result with revenue data or 0, and adjust the week number
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Minggu", "Pendapatan"));
        for (int week : allWeeks) {
            List<Object> row = new ArrayList<>();
            if (revenueDataMap.containsKey(week)) {
                Object[] data = revenueDataMap.get(week);
                row.add(((Number) data[0]).intValue() - firstWeek + 1);
                row.add(data[1]);
            } else {
                row.add(week - firstWeek + 1);
                row.add(0);
            }
            result.add(row);
        }

        return result;
    }

    private List<Integer> generateAllWeeksInMonth(int year, int month) {
        List<Integer> weeks = new ArrayList<>();
        LocalDate date = LocalDate.of(year, month, 1);
        while (date.getMonthValue() == month) {
            int week = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            if (!weeks.contains(week)) {
                weeks.add(week);
            }
            date = date.plusDays(1);
        }
        return weeks;
    }

    @Override
    public List<List<Object>> getMonthlyRevenueInYear(int year) {
        // Get the revenue data from the database
        List<Object[]> revenueData = orderItemDb.getMonthlyRevenueInYear(year);

        // Create a map for quick lookup
        Map<Integer, Object[]> revenueDataMap = new HashMap<>();
        for (Object[] data : revenueData) {
            int month = ((Number) data[0]).intValue();
            revenueDataMap.put(month, data);
        }

        // Array of month names
        String[] monthNames = { "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember" };

        // Fill the result with revenue data or 0 for each month
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Bulan", "Pendapatan"));
        for (int month = 1; month <= 12; month++) {
            List<Object> row = new ArrayList<>();
            row.add(monthNames[month - 1]); // Use month - 1 as index to get month name
            if (revenueDataMap.containsKey(month)) {
                row.add(revenueDataMap.get(month)[1]);
            } else {
                row.add(0);
            }

            result.add(row);
        }

        return result;
    }

    @Override
    public List<List<Object>> getYearlyRevenueInRange(int startYear, int endYear) {
        // Get the revenue data from the database
        List<Object[]> revenueData = orderItemDb.getYearlyRevenueInRange(startYear, endYear);

        // Create a map for quick lookup
        Map<Integer, Object[]> revenueDataMap = new HashMap<>();
        for (Object[] data : revenueData) {
            int year = ((Number) data[0]).intValue();
            revenueDataMap.put(year, data);
        }

        // Fill the result with revenue data or 0 for each year
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Tahun", "Pendapatan"));
        for (int year = startYear; year <= endYear; year++) {
            List<Object> row = new ArrayList<>();
            row.add(year);
            if (revenueDataMap.containsKey(year)) {
                row.add(revenueDataMap.get(year)[1]);
            } else {
                row.add(0);
            }
            result.add(row);
        }

        return result;
    }

    @Override
    public List<List<Object>> getOrderCountByStatus() {
        List<Object[]> data = orderItemDb.getOrderCountByStatus();
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Count", "Status"));
        for (Object[] row : data) {
            result.add(Arrays.asList(row));
        }
        return result;
    }

    @Override
    public Long getTotalRevenueForToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();

        return orderItemDb.getTotalRevenueForToday(startDate, endDate);
    }

    @Override
    public Long getTotalRevenueForThisWeek() {
        return orderItemDb.getRevenueForThisWeek();

    }

    @Override
    public Long getTotalRevenueForThisMonth() {
        return orderItemDb.getRevenueForThisMonth();
    }

    @Override
    public Long getTotalRevenueForThisYear() {
        return orderItemDb.getRevenueForThisYear();
    }

    @Override
    public List<List<Object>> getTotalRevenue() {
        Long today = getTotalRevenueForToday();
        Long weekly = getTotalRevenueForThisWeek();
        Long monthly = getTotalRevenueForThisMonth();
        Long yearly = getTotalRevenueForThisYear();
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("today", today));
        result.add(Arrays.asList("weekly", weekly));
        result.add(Arrays.asList("monthly", monthly));
        result.add(Arrays.asList("yearly", yearly));
        return result;
    }

    @Override
    public List<List<Object>> getTotalCompletedOrderItem() {
        int week, month, year;
        Calendar calendar = Calendar.getInstance();
        week = calendar.get(Calendar.WEEK_OF_YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        Date today = new Date();
        Date startDate = new Date(today.getYear(), today.getMonth(), today.getDate(), 0, 0, 0);
        Date endDate = new Date(today.getYear(), today.getMonth(), today.getDate(), 23, 59, 59);

        try {
            var totalToday = orderItemDb.getTotalCompletedOrdersForToday(startDate, endDate, 5);
            var weeklyList = orderItemDb.getWeeklyOrdersInMonth(year, month, 5);
            Long weekly = 0L;
            for (Object[] data : weeklyList) {
                int weekNumber = ((Number) data[0]).intValue();
                if (weekNumber == week) {
                    weekly = (Long) data[1];
                    break;
                }
            }

            var monthlyList = orderItemDb.getMonthlyOrdersInYear(year, 5);
            Long monthly = 0L;
            for (Object[] data : monthlyList) {
                int monthNumber = ((Number) data[0]).intValue();
                if (monthNumber == month) {
                    monthly = (Long) data[1];
                    break;
                }
            }
            var yearlyList = orderItemDb.getYearlyOrdersInRange(year, year, 5);
            Long yearly = (Long) yearlyList.get(0)[1];
            List<List<Object>> result = new ArrayList<>();
            result.add(Arrays.asList("today", totalToday));
            result.add(Arrays.asList("weekly", weekly));
            result.add(Arrays.asList("monthly", monthly));
            result.add(Arrays.asList("yearly", yearly));
            return result;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public List<List<Object>> getWeeklyOrder(int year, int month, int status) {
         // Generate all weeks in the given month
         List<Integer> allWeeks = generateAllWeeksInMonth(year, month);

         // Get the revenue data from the database
         List<Object[]> orderData = orderItemDb.getWeeklyOrdersInMonth(year, month, status);
 
         // Create a map for quick lookup
         Map<Integer, Object[]> orderDataMap = new HashMap<>();
         for (Object[] data : orderData) {
             int week = ((Number) data[0]).intValue();
             orderDataMap.put(week, data);
         }
 
         // Get the first week in the month
         int firstWeek = allWeeks.get(0);
 
         // Fill the result with revenue data or 0, and adjust the week number
         List<List<Object>> result = new ArrayList<>();
         result.add(Arrays.asList("Minggu", "Total Order Item"));
         for (int week : allWeeks) {
             List<Object> row = new ArrayList<>();
             if (orderDataMap.containsKey(week)) {
                 Object[] data = orderDataMap.get(week);
                 row.add(((Number) data[0]).intValue() - firstWeek + 1);
                 row.add(data[1]);
             } else {
                 row.add(week - firstWeek + 1);
                 row.add(0);
             }
             result.add(row);
         }
 
         return result;
    }

    @Override
    public List<List<Object>> getMonthlyOrder(int year, int status) {
        // Get the revenue data from the database
        List<Object[]> orderData = orderItemDb.getMonthlyOrdersInYear(year, status);

        // Create a map for quick lookup
        Map<Integer, Object[]> orderDataMap = new HashMap<>();
        for (Object[] data : orderData) {
            int month = ((Number) data[0]).intValue();
            orderDataMap.put(month, data);
        }

        // Array of month names
        String[] monthNames = { "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember" };

        // Fill the result with revenue data or 0 for each month
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Bulan", "Total Order Item"));
        for (int month = 1; month <= 12; month++) {
            List<Object> row = new ArrayList<>();
            row.add(monthNames[month - 1]); // Use month - 1 as index to get month name
            if (orderDataMap.containsKey(month)) {
                row.add(orderDataMap.get(month)[1]);
            } else {
                row.add(0);
            }

            result.add(row);
        }

        return result;
    }

    @Override
    public List<List<Object>> getYearlyOrder(int startYear, int endYear, int status) {
        // Get the revenue data from the database
        List<Object[]> orderData = orderItemDb.getYearlyOrdersInRange(startYear, endYear, status);

        // Create a map for quick lookup
        Map<Integer, Object[]> orderDataMap = new HashMap<>();
        for (Object[] data : orderData) {
            int year = ((Number) data[0]).intValue();
            orderDataMap.put(year, data);
        }

        // Fill the result with revenue data or 0 for each year
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Tahun", "Total Order Item"));
        for (int year = startYear; year <= endYear; year++) {
            List<Object> row = new ArrayList<>();
            row.add(year);
            if (orderDataMap.containsKey(year)) {
                row.add(orderDataMap.get(year)[1]);
            } else {
                row.add(0);
            }
            result.add(row);
        }

        return result;
    }

    @Override
    public  List<Map<String, Object>>   getListRevenueForToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        return orderItemDb.getListRevenueForToday(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getListRevenueForThisWeek() {
        return orderItemDb.getListRevenueForThisWeek();
    }

    @Override
    public List<Map<String, Object>> getListRevenueForThisMonth() {
        return orderItemDb.getListRevenueForThisMonth();
    }

    @Override
    public List<Map<String, Object>> getListRevenueForThisYear() {
        return orderItemDb.getListRevenueForThisYear();
    }

    @Override
    public List<OrderItem> getListOrderForToday(int status) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        return orderItemDb.getListOrdersForToday(startDate, endDate, 5);
    }

    @Override
    public List<OrderItem> getListOrderForThisWeek(int status) {
        return orderItemDb.getListOrdersForThisWeek(status);
      
    }

    @Override
    public List<OrderItem> getListOrderForThisMonth(int status) {
        return orderItemDb.getListOrdersForThisMonth(status);
    }


    @Override
    public List<OrderItem> getListOrderForThisYear(int status) {
        return orderItemDb.getListOrdersForThisYear(status);
    }
}

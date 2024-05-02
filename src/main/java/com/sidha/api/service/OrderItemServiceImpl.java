package com.sidha.api.service;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.repository.OrderItemDb;
import java.util.List;
import java.util.Arrays;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemDb orderItemDb;

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
        String[] monthNames = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", 
                               "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    
        // Fill the result with revenue data or 0 for each month
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Month", "Revenue"));
        for (int month = 1; month <= 12; month++) {
            List<Object> row = new ArrayList<>();
            row.add(monthNames[month - 1]);  // Use month - 1 as index to get month name
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
        result.add(Arrays.asList("Year", "Revenue"));
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
    public Double getTotalRevenueForThisWeek() {
       
        // get current week, month, year
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        List<Object[]> revenueData = orderItemDb.getWeeklyRevenueInMonth(year, month);
        for (Object[] data : revenueData) {
            int weekNumber = ((Number) data[0]).intValue();
            if (weekNumber == week) {
                return ((Number) data[1]).doubleValue();
            }
        }
        return null;

    }

    @Override
    public Double getTotalRevenueForThisMonth() {
        // get current month, year
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        List<Object[]> revenueData = orderItemDb.getMonthlyRevenueInYear(year);
        for (Object[] data : revenueData) {
            int monthNumber = ((Number) data[0]).intValue();
            if (monthNumber == month) {
                return ((Number) data[1]).doubleValue();
            }
        }
        return null;
   
    }

    @Override
    public Double getTotalRevenueForThisYear() {
        // get current year
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        List<Object[]> revenueData = orderItemDb.getYearlyRevenueInRange(year, year);
        for (Object[] data : revenueData) {
            int yearNumber = ((Number) data[0]).intValue();
            if (yearNumber == year) {
                return ((Number) data[1]).doubleValue();
            }
        }
        return null;
    }
}

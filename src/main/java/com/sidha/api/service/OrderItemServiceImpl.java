package com.sidha.api.service;

import java.util.Calendar;
import java.util.Map;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.repository.OrderItemDb;

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
    public Map<Integer, Long> getWeeklyRevenueInMonth(int year, int month) {
        return orderItemDb.getWeeklyRevenueInMonth(year, month);
    }

    @Override
    public Map<Integer, Long> getMonthlyRevenueInYear(int year) {
        return orderItemDb.getMonthlyRevenueInYear(year);
    }

    @Override
    public Map<Integer, Long> getYearlyRevenueInRange(int startYear, int endYear) {
        return orderItemDb.getYearlyRevenueInRange(startYear, endYear);
    }

    @Override
    public Map<Integer, Long> getOrderCountByStatus() {
        return orderItemDb.getOrderCountByStatus();
    }
}
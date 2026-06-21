package com.mall.service.impl;

import com.mall.mapper.StatisticsMapper;
import com.mall.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    public StatisticsServiceImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    @Override
    public Map<String, Object> dashboard() {
        return statisticsMapper.dashboard();
    }

    @Override
    public Map<String, Object> sales(String type) {
        LocalDate now = LocalDate.now();
        Map<String, Object> result = new LinkedHashMap<>();
        if ("week".equals(type)) {
            result.put("list", statisticsMapper.salesByDay(
                    now.minusDays(7).toString(), now.toString()));
        } else {
            result.put("list", statisticsMapper.salesByMonth(
                    String.valueOf(now.getYear())));
        }
        return result;
    }

    @Override
    public Map<String, Object> userStats() {
        LocalDate now = LocalDate.now();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", statisticsMapper.userRegByDay(
                now.minusDays(30).toString(), now.toString()));
        return result;
    }

    @Override
    public Map<String, Object> revenue(String type) {
        LocalDate now = LocalDate.now();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", statisticsMapper.revenueByMonth(
                String.valueOf(now.getYear())));
        return result;
    }
}

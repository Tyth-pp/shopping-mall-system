package com.mall.service;

import java.util.Map;

public interface StatisticsService {
    Map<String, Object> dashboard();
    Map<String, Object> sales(String type);
    Map<String, Object> userStats();
    Map<String, Object> revenue(String type);
}

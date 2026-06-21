package com.mall.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

@Mapper
public interface StatisticsMapper {
    Map<String, Object> dashboard();
    List<Map<String, Object>> salesByDay(String startDate, String endDate);
    List<Map<String, Object>> salesByMonth(String year);
    List<Map<String, Object>> userRegByDay(String startDate, String endDate);
    List<Map<String, Object>> revenueByMonth(String year);
    Integer countTodayOrders();
    BigDecimal todayRevenue();
    Integer countNewUsersToday();
    Integer countTotalProducts();
}

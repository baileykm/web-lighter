package com.bailey.web.lighter.vo;

import java.util.List;

/**
 * 常用信息VO - 排序规则
 *
 * @author Bailey
 */

public class Ordering {
    private List<Order> orders;

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public enum SortOrder {
        ASC, DESC
    }

    public static class Order {
        private String    field;
        private SortOrder order;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public SortOrder getOrder() {
            return order;
        }

        public void setOrder(SortOrder order) {
            this.order = order;
        }
    }
}
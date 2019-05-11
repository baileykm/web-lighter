package com.bailey.web.lighter.vo;

import java.util.List;

/**
 * @author Bailey
 */
public class ListWithTotal<T> {
    private List<T> list;
    private Long    total;

    public ListWithTotal() {
    }

    public ListWithTotal(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}

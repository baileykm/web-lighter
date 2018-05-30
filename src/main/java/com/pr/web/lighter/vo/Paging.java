package com.pr.web.lighter.vo;

/**
 * 常用信息VO - 分页信息
 *
 * @author Bailey
 */
public class Paging {
    private Integer page;
    private Integer pageSize;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}

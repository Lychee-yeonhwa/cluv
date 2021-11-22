package com.shop.constant;

public enum ReverseAuctionSearchSortColumn {
    REG_TIME("regTime"),
    PRICE("price");

    private final String columnName;

    ReverseAuctionSearchSortColumn(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return columnName;
    }
}

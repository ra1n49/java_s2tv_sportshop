package com.s2tv.sportshop.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CHO_XAC_NHAN("Chờ xác nhận"),
    DANG_CHUAN_BI_HANG("Đang chuẩn bị hàng"),
    DANG_GIAO("Đang giao"),
    HOAN_THANH("Hoàn thành"),
    YEU_CAU_HOAN("Yêu cầu hoàn"),
    HOAN_HANG("Hoàn hàng"),
    HUY_HANG("Hủy hàng");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }
}

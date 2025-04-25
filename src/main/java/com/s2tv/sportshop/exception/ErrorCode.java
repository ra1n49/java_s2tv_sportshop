package com.s2tv.sportshop.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SYSTEM_EXCEPTION(5,"Lỗi hệ thống"),
    USER_EXISTED(1,"Người dùng đã tồn tại!")
    ;
    ErrorCode(int EC, String EM){
        this.EC=EC;
        this.EM=EM;
    }

    private int EC;
    private String EM;

}

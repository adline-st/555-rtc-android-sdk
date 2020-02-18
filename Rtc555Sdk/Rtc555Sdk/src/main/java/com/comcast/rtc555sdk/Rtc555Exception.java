package com.comcast.rtc555sdk;

public class Rtc555Exception extends Exception {
    private String code;

    public Rtc555Exception(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}

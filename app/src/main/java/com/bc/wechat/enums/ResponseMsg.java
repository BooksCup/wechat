package com.bc.wechat.enums;

/**
 * 返回消息
 *
 * @author zhou
 */
public enum ResponseMsg {
    /**
     * wechat-server接口返回信息
     */
    USER_EXISTS("USER_EXISTS", "用户已存在"),
    ;

    ResponseMsg(String responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    private String responseCode;
    private String responseMessage;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}

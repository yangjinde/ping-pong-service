package com.example.pongservice.dto;

/**
 * Pong Result DTO
 *
 * @author yjd
 */
public class PongResDto {

    /**
     * response status
     */
    private int status;

    /**
     * response body
     */
    private String body;

    /**
     * response error message
     */
    private String errorMsg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

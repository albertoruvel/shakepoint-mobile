package com.shakepoint.mobile.data.res;

/**
 * Created by jose.rubalcaba on 02/12/2018.
 */

public class AuthenticationResponse {
    private String authenticationToken;
    private String message;
    private boolean success;

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

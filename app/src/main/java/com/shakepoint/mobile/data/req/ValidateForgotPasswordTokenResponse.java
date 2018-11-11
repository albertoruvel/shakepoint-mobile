package com.shakepoint.mobile.data.req;

public class ValidateForgotPasswordTokenResponse {
    private Boolean tokenValid;

    public ValidateForgotPasswordTokenResponse(Boolean tokenValid) {
        this.tokenValid = tokenValid;
    }

    public Boolean isTokenValid() {
        return tokenValid;
    }

    public void setTokenValid(Boolean tokenValid) {
        this.tokenValid = tokenValid;
    }
}

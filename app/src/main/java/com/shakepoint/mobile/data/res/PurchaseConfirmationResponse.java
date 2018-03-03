package com.shakepoint.mobile.data.res;

/**
 * Created by jose.rubalcaba on 02/27/2018.
 */

public class PurchaseConfirmationResponse {
    private String imageUrl;
    private boolean success;
    private String message;


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

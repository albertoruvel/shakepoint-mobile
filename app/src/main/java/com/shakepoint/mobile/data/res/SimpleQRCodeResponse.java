package com.shakepoint.mobile.data.res;

/**
 * Created by jose.rubalcaba on 02/19/2018.
 */

public class SimpleQRCodeResponse {
    private String imageUrl;

    public SimpleQRCodeResponse() {
    }

    public SimpleQRCodeResponse(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

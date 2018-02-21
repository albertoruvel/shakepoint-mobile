package com.shakepoint.mobile.data.res;

/**
 * Created by jose.rubalcaba on 02/16/2018.
 */

public class QrCodeResponse {
    private String machineId;
    private String machineName;
    private String productName;
    private String imageUrl;
    private String purchaseDate;

    public QrCodeResponse() {
    }

    public QrCodeResponse(String machineId, String machineName, String productName, String imageUrl, String purchaseDate) {
        this.machineId = machineId;
        this.machineName = machineName;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.purchaseDate = purchaseDate;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}

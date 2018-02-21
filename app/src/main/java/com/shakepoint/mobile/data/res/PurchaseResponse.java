package com.shakepoint.mobile.data.res;

/**
 * Created by jose.rubalcaba on 02/16/2018.
 */

public class PurchaseResponse {
    private String purchaseId;
    private String productName;
    private double total;
    private String machineName;
    private String purchaseDate;

    public PurchaseResponse() {
    }

    public PurchaseResponse(String purchaseId, String productName, double total, String machineName, String purchaseDate) {
        this.purchaseId = purchaseId;
        this.productName = productName;
        this.total = total;
        this.machineName = machineName;
        this.purchaseDate = purchaseDate;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}

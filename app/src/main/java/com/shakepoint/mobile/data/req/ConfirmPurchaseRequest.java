package com.shakepoint.mobile.data.req;

/**
 * Created by jose.rubalcaba on 02/19/2018.
 */

public class ConfirmPurchaseRequest {
    private String purchaseId;
    private String reference;

    public ConfirmPurchaseRequest(String purchaseId, String reference) {
        this.purchaseId = purchaseId;
        this.reference = reference;
    }

    public ConfirmPurchaseRequest() {
        super();
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}

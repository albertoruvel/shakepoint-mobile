package com.shakepoint.mobile.data.req;

/**
 * Created by jose.rubalcaba on 02/19/2018.
 */

public class ConfirmPurchaseRequest {
    private String purchaseId;
    private String cardNumber;
    private String cardExpirationDate;
    private String cvv;

    public ConfirmPurchaseRequest(String purchaseId, String cardNumber, String cardExpirationDate, String cvv) {
        this.purchaseId = purchaseId;
        this.cardNumber = cardNumber;
        this.cardExpirationDate = cardExpirationDate;
        this.cvv = cvv;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpirationDate() {
        return cardExpirationDate;
    }

    public void setCardExpirationDate(String cardExpirationDate) {
        this.cardExpirationDate = cardExpirationDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
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

}

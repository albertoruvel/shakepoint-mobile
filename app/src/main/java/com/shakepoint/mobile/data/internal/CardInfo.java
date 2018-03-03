package com.shakepoint.mobile.data.internal;

/**
 * Created by jose.rubalcaba on 02/27/2018.
 */

public class CardInfo {
    private final String cardNumber;
    private final String cardExpirationDate;

    public CardInfo(String cardNumber, String cardExpirationDate) {
        this.cardNumber = cardNumber;
        this.cardExpirationDate = cardExpirationDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardExpirationDate() {
        return cardExpirationDate;
    }
}

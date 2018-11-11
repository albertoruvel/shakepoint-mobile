package com.shakepoint.mobile.data.req;

public class CreatePromoCodeRequest {
    private String description;
    private String expirationDate;
    private int discount;
    private String productId;
    private String trainerId;
    private int promotionType;
    private boolean allProducts;

    public CreatePromoCodeRequest() {
    }

    public CreatePromoCodeRequest(String description, String expirationDate, int discount, String productId, String trainerId, int promotionType, boolean allProducts) {
        this.description = description;
        this.expirationDate = expirationDate;
        this.discount = discount;
        this.productId = productId;
        this.trainerId = trainerId;
        this.promotionType = promotionType;
        this.allProducts = allProducts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public int getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(int promotionType) {
        this.promotionType = promotionType;
    }

    public boolean isAllProducts() {
        return allProducts;
    }

    public void setAllProducts(boolean allProducts) {
        this.allProducts = allProducts;
    }
}

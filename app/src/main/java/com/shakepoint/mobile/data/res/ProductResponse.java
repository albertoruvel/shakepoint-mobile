package com.shakepoint.mobile.data.res;

/**
 * Created by jose.rubalcaba on 02/14/2018.
 */

public class ProductResponse {

    private String id;
    private String name;
    private String logoUrl;
    private double price;
    private String description;
    private String productType;
    private String nutritionalDataUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getNutritionalDataUrl() {
        return nutritionalDataUrl;
    }

    public void setNutritionalDataUrl(String nutritionalDataUrl) {
        this.nutritionalDataUrl = nutritionalDataUrl;
    }

    @Override
    public String toString() {
        return name;
    }
}

package com.shakepoint.mobile.data.res.admin;
import java.util.List;
public class GetVendingProducts {
    private List<VendingProductDetails> products;
    private String vendingName;

    public GetVendingProducts(List<VendingProductDetails> products, String vendingName) {
        this.products = products;
        this.vendingName = vendingName;
    }

    public List<VendingProductDetails> getProducts() {
        return products;
    }

    public void setProducts(List<VendingProductDetails> products) {
        this.products = products;
    }

    public String getVendingName() {
        return vendingName;
    }

    public void setVendingName(String vendingName) {
        this.vendingName = vendingName;
    }
}

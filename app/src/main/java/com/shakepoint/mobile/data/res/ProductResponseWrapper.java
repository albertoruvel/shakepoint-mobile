package com.shakepoint.mobile.data.res;

import java.util.List;

/**
 * Created by jose.rubalcaba on 02/14/2018.
 */

public class ProductResponseWrapper {
    private List<ProductResponse> products;

    public List<ProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }
}

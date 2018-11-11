package com.shakepoint.mobile.data.req.admin;

public class CreateFlavorRequest {
    private String name;
    private String hexColor;

    public CreateFlavorRequest(String name, String hexColor) {
        this.name = name;
        this.hexColor = hexColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }
}

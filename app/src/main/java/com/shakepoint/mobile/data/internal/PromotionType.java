package com.shakepoint.mobile.data.internal;

public enum PromotionType {
    ALL_PRODUCTS("Todos los productos", 1),
    SINGLE_PRODUCT("Un solo producto", 2),
    TRAINER("Entrenador", 3),
    SEASON("Temporada", 4),
    SEASON_SINGLE_PRODUCT("Temporada para un producto", 5);
    int value;
    String message;

    PromotionType(String message, int value){
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return this.value;
    }


    @Override
    public String toString() {
        return message;
    }
}

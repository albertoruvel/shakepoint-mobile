package com.shakepoint.mobile.data.internal;

public enum SecurityRole {
    ADMIN("ADMIN"), MEMBER("MEMBER"), PARTNER("PARTNER"), TRAINER("TRAINER");
    String value;

    SecurityRole(String value) {
        this.value = value;
    }

    public static SecurityRole fromString(String v) {
        if (v.equals(ADMIN.value)) {
            return ADMIN;
        } else if (v.equals(MEMBER.value)) {
            return MEMBER;
        } else if (v.equals(PARTNER.value)) {
            return PARTNER;
        } else if (v.equals(TRAINER.value)) {
            return TRAINER;
        } return null;
    }

}

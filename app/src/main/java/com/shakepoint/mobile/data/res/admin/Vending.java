package com.shakepoint.mobile.data.res.admin;

public class Vending {
    private String id;
    private String name;
    private String description;
    private String partnerName;
    private int workingPort;
    private boolean active;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public int getWorkingPort() {
        return workingPort;
    }

    public void setWorkingPort(int workingPort) {
        this.workingPort = workingPort;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

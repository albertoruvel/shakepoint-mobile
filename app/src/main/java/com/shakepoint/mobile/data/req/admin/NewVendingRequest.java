package com.shakepoint.mobile.data.req.admin;

public class NewVendingRequest {
    private String name;
    private String description;
    private String technicianId;
    private String location;

    public NewVendingRequest(String name, String description, String technicianId, String location) {
        this.name = name;
        this.description = description;
        this.technicianId = technicianId;
        this.location = location;
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

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

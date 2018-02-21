package com.shakepoint.mobile.data.res;

/**
 * Created by jose.rubalcaba on 02/14/2018.
 */

public class MachineSearchResponse {
    private String machineId;
    private String machineName;
    private double distance;

    public MachineSearchResponse() {
        super();
        // TODO Auto-generated constructor stub
    }
    public String getMachineId() {
        return machineId;
    }
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
    public String getMachineName() {
        return machineName;
    }
    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}

package com.github.tavalin.orvibo.devices;

import java.util.ArrayList;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.messages.request.PowerStatusRequest;

public class Socket extends OrviboDevice {

    public Socket() {
        super(DeviceType.SOCKET);
    }

    private ArrayList<SocketStateListener> stateListeners = new ArrayList<SocketStateListener>();

    public interface SocketStateListener {
        public void socketDidChangeLabel(Socket socket, String label);

        public void socketDidChangePowerState(Socket socket, PowerState powerState);

        public void socketDidInitialisation(Socket socket);
    }
    
    public void addSocketStateListener(SocketStateListener listener) {
        if (!stateListeners.contains(listener)) {
            stateListeners.add(listener);
        }
    }

    public void removeAllSocketStateListeners() {
        stateListeners.clear();
    }

    public void removeSocketStateListener(SocketStateListener listener) {
        stateListeners.remove(listener);
    }

    private PowerState powerState;

    public void on() {
        setPowerState(PowerState.ON);
    }

    public void off() {
        setPowerState(PowerState.OFF);
    }

    private void setPowerState(PowerState powerState) {
        PowerStatusRequest request = new PowerStatusRequest();
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(request, true);
    }

    public PowerState getPowerState() {
        if (powerState == null) {
            return PowerState.OFF;
        }
        return powerState;
    }

    /*
     * private void notifyListenerLabelDidChange(String label) {
     * for (SocketStateListener aListener : stateListeners) {
     * aListener.socketDidChangeLabel(this, label);
     * }
     * }
     */



    public void powerDidChangeTo(PowerState powerState) {
        if (this.powerState == null || !this.powerState.equals(powerState)) {
            notifyListenersPowerStateDidChange(powerState);
        }
        this.powerState = powerState;
        // updateInitActions(InitActions.POWER);
    }
    
    private void notifyListenersPowerStateDidChange(PowerState powerState) {
        for (SocketStateListener aListener : stateListeners) {
            aListener.socketDidChangePowerState(this, powerState);
        }
    }



    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("DeviceId: ");
        sb.append(getDeviceId());
        sb.append(", ");
        sb.append("Label: ");
        sb.append(getLabel());
        sb.append(", ");
        sb.append("Power: ");
        sb.append(getPowerState());
        // sb.append(", ");
        // sb.append("Init Actions Remaining: ");
        // sb.append(initList.size());
        // sb.append(", ");
        // sb.append("isInitialised: ");
        // sb.append(isInitialised());
        sb.append("]");
        return sb.toString();
    }

}

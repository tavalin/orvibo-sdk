package com.github.tavalin.orvibo.network.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.DeviceType;
import com.github.tavalin.orvibo.devices.OrviboDevice;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.messages.response.EmitResponse;
import com.github.tavalin.orvibo.messages.response.GlobalDiscoveryResponse;
import com.github.tavalin.orvibo.messages.response.LearnResponse;
import com.github.tavalin.orvibo.messages.response.LocalDiscoveryResponse;
import com.github.tavalin.orvibo.messages.response.PowerStatusResponse;
import com.github.tavalin.orvibo.messages.response.SocketDataResponse;
import com.github.tavalin.orvibo.messages.response.SubscriptionResponse;
import com.github.tavalin.orvibo.messages.response.TableDataResponse;

public class ResponseHandler extends IoHandlerAdapter {

    private OrviboClient client = null;

    public ResponseHandler(OrviboClient client) {
        this.client = client;
    }

    private final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    @Override
    public void messageReceived(IoSession session, Object message) {
        logger.debug("Message received...{}", session.toString());
        
        //TODO: update routing table
        
        if (message instanceof GlobalDiscoveryResponse) {
            handleGlobalDiscoveryResponse((GlobalDiscoveryResponse) message);
        } else if (message instanceof LocalDiscoveryResponse) {
            handleLocalDiscoveryResponse((LocalDiscoveryResponse) message);
        } else if (message instanceof SubscriptionResponse) {
            handleSubscriptionResponse((SubscriptionResponse) message);
        } else if (message instanceof PowerStatusResponse) {

        } else if (message instanceof SocketDataResponse) {

        } else if (message instanceof TableDataResponse) {

        } else if (message instanceof LearnResponse) {

        } else if (message instanceof EmitResponse) {

        }
    }

    private void handleSubscriptionResponse(SubscriptionResponse message) {
        OrviboDevice device = client.getAllDevices().get(message.getDeviceId());
        if (device != null) {
            if (device instanceof Socket) {
                Socket socket = (Socket)device;
                socket.powerDidChangeTo(message.getPowerState());
            } else if (device instanceof AllOne) {
                // nothing to do...
            }
        }
    }

    private void handleLocalDiscoveryResponse(LocalDiscoveryResponse message) {
        OrviboDevice device = client.getAllDevices().get(message.getDeviceId());
        if (device != null) {
            if (device instanceof Socket) {
                Socket socket = (Socket)device;
                socket.powerDidChangeTo(message.getPowerState());
            } else if (device instanceof AllOne) {
                // nothing to do...
            }
        }
    }

    private void handleGlobalDiscoveryResponse(GlobalDiscoveryResponse response) {
        DeviceType type = response.getDeviceType();
        if (DeviceType.SOCKET.equals(type)) {
            client.socketWithDeviceId(response.getDeviceId());
        } else if (DeviceType.ALLONE.equals(type)) {
            client.allOneWithDeviceId(response.getDeviceId());
        }
    }

}

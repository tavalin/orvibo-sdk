package com.github.tavalin.orvibo.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.firewall.BlacklistFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.DeviceType;
import com.github.tavalin.orvibo.devices.OrviboDevice;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.entities.DeviceMapping;
import com.github.tavalin.orvibo.messages.OrviboMessage;
import com.github.tavalin.orvibo.messages.request.RetryableMessage;
import com.github.tavalin.orvibo.messages.response.EmitResponse;
import com.github.tavalin.orvibo.messages.response.GlobalDiscoveryResponse;
import com.github.tavalin.orvibo.messages.response.LearnResponse;
import com.github.tavalin.orvibo.messages.response.LocalDiscoveryResponse;
import com.github.tavalin.orvibo.messages.response.PowerStatusResponse;
import com.github.tavalin.orvibo.messages.response.SocketDataResponse;
import com.github.tavalin.orvibo.messages.response.SubscriptionResponse;
import com.github.tavalin.orvibo.messages.response.TableDataResponse;
import com.github.tavalin.orvibo.network.mina.MessageCodecFactory;
import com.google.common.base.Predicates;

/**
 * The Class TransportManager.
 */
public class TransportManager extends IoHandlerAdapter {

    private OrviboClient client = null;

    /** The Constant logger. */
    private final Logger logger = LoggerFactory.getLogger(TransportManager.class);

    private final RoutingTable routingTable = new RoutingTable();

    /** The Constant BROADCAST_PORT. */
    public final static int BROADCAST_PORT = 10000;

    /** The Constant REMOTE_PORT. */
    public final static int REMOTE_PORT = 10000;

    /** The Constant LISTEN_PORT. */
    public final static int LISTEN_PORT = 10000;

    public final static int DISCONNECT_TIMEOUT = 30000;

    private final NioDatagramAcceptor accepter = new NioDatagramAcceptor();

    private final Map<SocketAddress, IoSession> sessions = new HashMap<SocketAddress, IoSession>();

    /**
     * Instantiates a new transport manager.
     *
     * @param s20Client the s20 client
     * @throws SocketException the socket exception
     */
    public TransportManager(OrviboClient s20Client) {
        client = s20Client;
    }

    public void startServer() throws IOException {

        // NioDatagramAcceptor accepter = new NioDatagramAcceptor();
        accepter.getSessionConfig().setReuseAddress(true);
        accepter.setHandler(this);
        // accepter.setHandler(new ResponseHandler(client));
        BlacklistFilter filter = new BlacklistFilter();
        filter.block(InetAddress.getLocalHost());
        // filter.block(InetAddress.getByName("localhost"));
        // accepter.getFilterChain().addLast("duplicate", new ConnectionThrottleFilter(10000)); // filters out duplicate
        // high-level message
        // objects TODO: Find
        // appropriate filter
        accepter.getFilterChain().addLast("loopback", filter); // filters out any data from the loopback address
        accepter.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new MessageCodecFactory())); // constructs
                                                                                                           // high-level
                                                                                                           // message
                                                                                                           // objects
                                                                                                           // from byte
                                                                                                           // array
        accepter.bind(new InetSocketAddress(LISTEN_PORT));
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.debug("Session opened...{}", session.toString());
        synchronized (sessions) {
            session.getConfig().setUseReadOperation(true);
            sessions.put(session.getRemoteAddress(), session);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.debug("Session closed...{}", session.toString());
        synchronized (sessions) {
            sessions.remove(session.getRemoteAddress());
        }
    }
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        logger.error("Error with...{}", session.toString(), cause);
    }

    /*
     * @Override
     * public void messageReceived(IoSession session, Object message) {
     * logger.debug("Message received...{}", session.toString());
     * 
     * if (message instanceof GlobalDiscoveryResponse) {
     * GlobalDiscoveryResponse response = (GlobalDiscoveryResponse) message;
     * OrviboClient client = OrviboClient.getInstance();
     * DeviceType type = response.getDeviceType();
     * if (DeviceType.SOCKET.equals(type)) {
     * client.socketWithDeviceId(response.getDeviceId());
     * } else if (DeviceType.ALLONE.equals(type)) {
     * client.allOneWithDeviceId(response.getDeviceId());
     * }
     * } else if(message instanceof LocalDiscoveryResponse) {
     * 
     * } else if (message instanceof SubscriptionResponse) {
     * 
     * } else if (message instanceof PowerStatusResponse) {
     * 
     * } else if (message instanceof SocketDataResponse) {
     * 
     * } else if (message instanceof TableDataResponse) {
     * 
     * } else if (message instanceof LearnResponse) {
     * 
     * } else if (message instanceof EmitResponse) {
     * 
     * }
     * }
     */



    /*
     * private void send(SocketAddress address, final byte[] data) {
     * 
     * IoSession session = sessions.get(address);
     * if (session == null || !session.isConnected()) {
     * logger.debug("Session not found...");
     * NioDatagramConnector connector = new NioDatagramConnector();
     * connector.setHandler(this);
     * ConnectFuture connectFuture = connector.connect(address);
     * connectFuture.addListener(new IoFutureListener<ConnectFuture>() {
     * public void operationComplete(ConnectFuture future) {
     * if (future.isConnected()) {
     * IoSession newSession = future.getSession();
     * sendToSession(newSession, data);
     * }
     * }
     * });
     * } else {
     * logger.debug("Session found...");
     * sendToSession(session, data);
     * }
     * }
     */

    /*
     * private synchronized void sendToSession(IoSession session, byte[] data) {
     * IoBuffer buffer = IoBuffer.allocate(data.length);
     * buffer.put(data);
     * buffer.flip();
     * session.write(buffer);
     * }
     */

    /*
     * private void send(SocketAddress address, final OrviboMessage message) {
     * 
     * IoSession session = sessions.get(address);
     * if (session == null || !session.isConnected()) {
     * logger.debug("Session not found...");
     * NioDatagramConnector connector = new NioDatagramConnector();
     * connector.getSessionConfig().setReuseAddress(true);
     * connector.setHandler(this);
     * connector.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new MessageCodecFactory()));
     * // ConnectFuture connectFuture = connector.connect(address);
     * // connector.getSessionConfig().setReuseAddress(true);
     * // dcfg.setReuseAddress(true);
     * ConnectFuture connectFuture = connector.connect(address);
     * 
     * connectFuture.addListener(new IoFutureListener<ConnectFuture>() {
     * public void operationComplete(ConnectFuture future) {
     * Throwable cause = future.getException();
     * if (future.isConnected()) {
     * IoSession newSession = future.getSession();
     * sendToSession(newSession, message);
     * }
     * }
     * });
     * 
     * connectFuture.awaitUninterruptibly();
     * Throwable t = connectFuture.getException();
     * session = connectFuture.getSession();
     * sendToSession(session, message);
     * } else {
     * logger.debug("Session found...");
     * sendToSession(session, message);
     * }
     * }
     */



    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
        return accepter.isActive();
    }

    /**
     * Connect.
     */
    public void connect() throws IOException {
        if (!isConnected()) {
            startServer();
        }
    }

    /**
     * Disconnect.
     */
    public void disconnect() {
        if (isConnected()) {
            for (IoSession s : sessions.values()) {
                s.closeOnFlush();
            }
            accepter.dispose();
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        logger.debug("Message received...{}", session.toString());
        if (message instanceof OrviboMessage) {
            String deviceId = ((OrviboMessage) message).getDeviceId();
            routingTable.updateDeviceMapping(deviceId, (InetSocketAddress) session.getRemoteAddress());
            if (message instanceof GlobalDiscoveryResponse) {
                handleGlobalDiscoveryResponse((GlobalDiscoveryResponse) message);
            } else if (message instanceof LocalDiscoveryResponse) {
                handleLocalDiscoveryResponse((LocalDiscoveryResponse) message);
            } else if (message instanceof SubscriptionResponse) {
                handleSubscriptionResponse((SubscriptionResponse) message);
            } else if (message instanceof PowerStatusResponse) {
                // TODO: implement
            } else if (message instanceof SocketDataResponse) {
                // TODO: implement
            } else if (message instanceof TableDataResponse) {
                // TODO: implement
            } else if (message instanceof LearnResponse) {
                // TODO: implement
            } else if (message instanceof EmitResponse) {
                // TODO: implement
            }
        }
    }

    public void testSend(OrviboMessage message, boolean retry) {
        InetSocketAddress address = null;
        try {
            DeviceMapping mapping = routingTable.getDeviceMappingForDevice(message.getDeviceId());
            if (mapping == null || message.getDeviceId() == null) {
                logger.debug("No routing table entry found, sending message as broadcast.");
                address = NetworkUtils.getBroadcastAddress(REMOTE_PORT);
            } else {
                logger.debug("Routing table entry found.");
                address = mapping.getAddress();
            }

            IoSession session = getSession(address);
            logger.debug("Sending to {}", session.toString());
            if (session.getService() instanceof IoAcceptor) {
                // if (retry) {
                sendWithRetry(session, message);
            } else {
                session.write(message);
            }

        } catch (ExecutionException | RetryException | SocketException e) {
            logger.error(e.getMessage());
        }

    }

    private IoSession getSession(InetSocketAddress address) {
        Map<Long, IoSession> managedSessions = accepter.getManagedSessions();
        for (IoSession thisSession : managedSessions.values()) {
            if (thisSession.getRemoteAddress().equals(address)) {
                return thisSession;
            }
        }
        // else create connection
        NioDatagramConnector connector = new NioDatagramConnector();
        connector.getSessionConfig().setUseReadOperation(true);
        // connector.setHandler();
        connector.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new MessageCodecFactory()));
        ConnectFuture connectFuture = connector.connect(address);
        connectFuture.awaitUninterruptibly();
        return connectFuture.getSession();
    }

    public void sendWithRetry(IoSession session, OrviboMessage message) throws ExecutionException, RetryException {

        RetryableMessage r = new RetryableMessage(session, message, 1000, TimeUnit.MILLISECONDS);
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean> newBuilder().retryIfResult(Predicates.<Boolean> isNull())
                .retryIfResult(Predicates.<Boolean> equalTo(Boolean.FALSE))
                .withStopStrategy(StopStrategies.stopAfterAttempt(10)).build();
        retryer.call(r);
    }

    private void handleSubscriptionResponse(SubscriptionResponse message) {
        OrviboDevice device = client.getAllDevices().get(message.getDeviceId());
        if (device != null) {
            if (device instanceof Socket) {
                Socket socket = (Socket) device;
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
                Socket socket = (Socket) device;
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
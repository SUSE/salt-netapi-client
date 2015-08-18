package com.suse.saltstack.netapi.event;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.suse.saltstack.netapi.utils.ClientUtils;

/**
 * This class is intended to emulate the Server WebSocket
 * EndPoint where EventStream is connected to, and where events came from.
 */
@ServerEndpoint(value = "/token")
public class WebSocketServerSalt {

    /**
     * Note: The event stream message is a String delimited by "\n\n". The resource file
     * events_stream.txt contains some events separated by "\n\n".
     */
    static final String[] TEXT_EVENT_STREAM_MESSAGES =
            ClientUtils.streamToString(WebSocketServerSalt.class
                    .getResourceAsStream("/events_stream.txt")).split("\n\n");

    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            for (String s : TEXT_EVENT_STREAM_MESSAGES) {
                session.getBasicRemote().sendText(s);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    }
}

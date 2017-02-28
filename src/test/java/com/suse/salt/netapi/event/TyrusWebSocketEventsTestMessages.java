package com.suse.salt.netapi.event;

import com.suse.salt.netapi.utils.ClientUtils;

import javax.websocket.server.ServerEndpoint;
import java.util.stream.Stream;

/**
 * Mocked WebSocket endpoint returning messages from a file.
 */
@ServerEndpoint(value = "/token")
public class TyrusWebSocketEventsTestMessages extends MockingWebSocket {
    @Override
    public Stream<Message> messages() {
        return Stream.of(ClientUtils.streamToString(MockingWebSocket.class
                .getResourceAsStream("/events_stream.txt")).split("\n\n"))
                .map(Message::new);
    }
}

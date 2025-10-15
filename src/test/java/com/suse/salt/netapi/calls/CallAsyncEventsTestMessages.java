package com.suse.salt.netapi.calls;

import com.suse.salt.netapi.event.MockingWebSocket;
import com.suse.salt.netapi.utils.ClientUtils;

import jakarta.websocket.server.ServerEndpoint;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Mocked WebSocket endpoint returning messages from a file.
 */
@ServerEndpoint(value = "/token")
public class CallAsyncEventsTestMessages extends MockingWebSocket {
    @Override
    public Stream<Message> messages() {
        String[] split = ClientUtils.streamToString(MockingWebSocket.class
                .getResourceAsStream("/events/delayed.txt")).split("\n\n");
        return IntStream.range(0, split.length).mapToObj(
                i -> new Message(split[i], 2000)
        );
    }
}

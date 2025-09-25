package com.suse.salt.netapi.event;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Server endpoint to emulate the WebSocket based stream of Salt events.
 */
@ServerEndpoint(value = "/token")
public abstract class MockingWebSocket {

    /**
     * A single message to be sent.
     */
    public static class Message {
        private String message;
        private Optional<Long> delay;

        public Message(String message, long delay) {
            this.message = message;
            this.delay = Optional.of(delay);
        }

        public Message(String message) {
            this.message = message;
            this.delay = Optional.empty();
        }

        public Optional<Long> getDelay() {
            return delay;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * @return the messages to be sent
     */
    public abstract Stream<Message> messages();

    public MockingWebSocket() {
    }

    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        messages().forEach(m -> {
            m.getDelay().ifPresent(s -> {
                try {
                    Thread.sleep(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            try {
                session.getBasicRemote().sendText(m.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        });
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
    }
}

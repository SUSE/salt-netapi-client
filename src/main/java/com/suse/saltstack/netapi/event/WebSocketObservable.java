package com.suse.saltstack.netapi.event;

import org.glassfish.tyrus.core.CloseReasons;
import org.glassfish.tyrus.core.CloseReasons.*;
import rx.*;

import javax.websocket.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class WebSocketObservable {

    @ClientEndpoint
    public static class Websocket extends Endpoint implements MessageHandler.Whole<String> {

        final URI uri;
        final Subscriber<? super String> subscriber;
        Session session;

        public Websocket(URI uri, Subscriber<? super String> subscriber) {
            this.uri = uri;
            this.subscriber = subscriber;
        }

        @OnOpen
        public void onOpen(Session session, EndpointConfig config) {
            try {
                session.getBasicRemote().sendText("websocket client ready");
                subscriber.onStart();
                session.addMessageHandler(this);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }

        @OnMessage
        public void onMessage(String event) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(event);
            }
        }

        public void onError(Session session, Throwable thr) {
            subscriber.onError(thr);
        }

        @OnClose
        public void onClose(Session session, CloseReason closeReason) {
            //TODO: check close reason and call onComplete or onError accordingly
            System.out.println("closed " + closeReason);
            if (closeReason.getCloseCode() == CloseReason.CloseCodes.CLOSED_ABNORMALLY) {
                subscriber.onError(null);
            } else {
                subscriber.onCompleted();
            }
        }
    }

    static class OnSubscribeWebsocket implements Observable.OnSubscribe<String> {

        final WebSocketContainer websocketContainer = ContainerProvider.getWebSocketContainer();
        final URI uri;

        public OnSubscribeWebsocket(URI uri) {
            this.uri = uri;
        }

        @Override
        public void call(Subscriber<? super String> subscriber) {
            try {
                System.out.println("subscribe");
                Websocket ws = new Websocket(uri, subscriber);
                Session session = websocketContainer.connectToServer(ws, uri);
                session.setMaxIdleTimeout(0);
            } catch (Throwable e) {
                subscriber.onError(e);
            }
        }
    }

    public static Observable<String> websocket(URI uri) {
        return Observable.create(new OnSubscribeWebsocket(uri));
    }


}

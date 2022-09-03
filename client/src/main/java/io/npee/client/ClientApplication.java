package io.npee.client;

import java.net.URI;
import java.time.Duration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

public class ClientApplication {

    public static void main(String[] args) {
        WebSocketClient client = new ReactorNettyWebSocketClient();

        client.execute(
            URI.create("ws://localhost:8889/event-emitter"),
            session -> session.send(
                Mono.just(session.textMessage("event-spring-reactive-client-websocket")))
                              .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText).log())
                              .then())
              .block(Duration.ofSeconds(10L));
    }

}

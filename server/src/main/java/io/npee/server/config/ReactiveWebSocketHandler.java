package io.npee.server.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.npee.server.domain.Event;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private static final ObjectMapper json = new ObjectMapper();

    private Flux<String> eventFlux = Flux.generate(sink -> {
        Event event = new Event(UUID.randomUUID().toString(), LocalDateTime.now().toString());
        try {
            sink.next(json.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            sink.error(e);
        }
    });

    private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L)).zipWith(eventFlux, (time, event) -> event);

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(intervalFlux.map(session::textMessage))
                      .and(session.receive().map(WebSocketMessage::getPayloadAsText).log());
    }
}

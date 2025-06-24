package com.example.userservice.service;

import com.example.shared.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventPublisher {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String userEventsTopic;

    public void publishUserCreatedEvent(String email, String name) {
        UserEvent event = new UserEvent(UserEvent.EventType.CREATED, email, name);
        log.info("Sent event to Kafka: {}", event);
        kafkaTemplate.send(userEventsTopic, event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("🟢 Sent to Kafka successfully: {}", result.getProducerRecord());
            } else {
                log.error("🔴 Failed to send event to Kafka", ex);
            }
        });

    }

    public void publishUserDeletedEvent(String email, String name) {
        UserEvent event = new UserEvent(UserEvent.EventType.DELETED, email, name);
        kafkaTemplate.send(userEventsTopic, event);
    }
}
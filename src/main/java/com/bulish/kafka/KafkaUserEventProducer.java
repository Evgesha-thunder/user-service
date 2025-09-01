package com.bulish.kafka;

import com.bulish.dto.UserOperationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaUserEventProducer {
    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper;

    public void sendEvent(String topic, String key, UserOperationEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, eventJson);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("error while send event {}", exception.getMessage());
                } else {
                    log.debug("event {} is send with key {} to topic {}", eventJson, key, metadata.topic());
                }
            });
        } catch (JsonProcessingException exception) {
            log.error("Error while serialization {}", exception.getMessage());
        }
    }
}

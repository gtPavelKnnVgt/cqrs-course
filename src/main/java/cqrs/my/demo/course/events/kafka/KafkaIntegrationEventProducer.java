package cqrs.my.demo.course.events.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class KafkaIntegrationEventProducer {
    private final static String TOPIC_NAME = "INTEGRATION_EVENTS_APPOINTMENT_ADDED";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Producer<String, String> producer;

    public KafkaIntegrationEventProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
    }

    public void send(IntegrationEvent integrationEvent) {
        String resp;
        try {
            resp = OBJECT_MAPPER.writeValueAsString(integrationEvent);
        } catch (Exception e) {
            log.error("Failed to serialize integration event: {}", integrationEvent, e.getMessage());
            return;
        }
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, resp);
        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                log.info("Successfully sent event to Kafka topic: {}", record.topic());
            } else {
                log.error("Failed to send event to Kafka topic: {}", record.topic(), exception);
            }
        });
    }
}

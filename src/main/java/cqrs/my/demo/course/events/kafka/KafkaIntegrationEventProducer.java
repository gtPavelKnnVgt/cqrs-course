package cqrs.my.demo.course.events.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class KafkaIntegrationEventProducer {
    private final Producer<String, String> producer;
    private final ObjectMapper jacksonObjectMapper;

    public KafkaIntegrationEventProducer(ObjectMapper jacksonObjectMapper) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public void send(IntegrationEvent integrationEvent, String topicName) {
        String resp;
        try {
            resp = jacksonObjectMapper.writeValueAsString(integrationEvent);
        } catch (Exception e) {
            log.error("Failed to serialize integration event: {}", integrationEvent, e.getMessage());
            return;
        }
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, resp);
        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                log.info("Successfully sent event: {} to Kafka topic: {}", integrationEvent, record.topic());
            } else {
                log.error("Failed to send event: {} to Kafka topic: {}", integrationEvent, record.topic(), exception);
            }
        });
    }
}

package com.example.kafka;

import com.example.BaseTest;
import com.example.rabbit.RabbitMqProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaProducer {
    private final Producer<String, String> producer;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(String bootstrapServers) {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.put(ProducerConfig.LINGER_MS_CONFIG, 150);
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(producerProps);
    }

    public void sendMessages(String topic, int count, String message) {
        for (int i = 0; i < count; i++) {
            producer.send(new ProducerRecord<>(topic, "key-" + i, message));
        }
    }

    public void close() {
        producer.close();
    }
}
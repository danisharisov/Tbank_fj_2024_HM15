package com.example.kafka;

import com.example.BaseTest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumer {
    private final org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;

    public KafkaConsumer(String bootstrapServers, String groupId, String topic) {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(consumerProps);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public ConsumerRecords<String, String> pollMessages(Duration duration) {
        return consumer.poll(duration);
    }

    public void close() {
        consumer.close();
    }
}
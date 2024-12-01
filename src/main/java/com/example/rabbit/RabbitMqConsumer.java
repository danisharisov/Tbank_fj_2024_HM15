package com.example.rabbit;

import com.example.BaseTest;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMqConsumer extends BaseTest {
    private static final String QUEUE_NAME = "test_queue";
    private final Channel channel;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConsumer.class);

    public RabbitMqConsumer() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1000);
        logger.info("Consumer connected to queue: {}", QUEUE_NAME);
    }

    public String consumeMessage() throws IOException {
        final String[] result = new String[1];
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            result[0] = message;
            logger.info("Received message: {}", message);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

        return result[0];
    }

    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                logger.info("Consumer channel closed successfully.");
            }
        } catch (IOException | TimeoutException e) {
            logger.error("Error closing consumer channel: {}", e.getMessage());
        }
    }
}

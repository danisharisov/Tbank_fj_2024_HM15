package com.example.rabbit;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMqConsumerWithConfirmation {
    private static final String QUEUE_NAME = "test_queue";
    private final Channel channel;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConsumerWithConfirmation.class);

    public RabbitMqConsumerWithConfirmation() throws IOException, TimeoutException {
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

            try {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                logger.info("Consumer acknowledged message: {}", message);
            } catch (IOException e) {
                logger.error("Error acknowledging message: {}", message);
            }
        };
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});

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

package com.example.rabbit;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMqProducerWithConfirmation {
    private static final String QUEUE_NAME = "test_queue";
    private final Channel channel;
    private final int index;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqProducerWithConfirmation.class);

    public RabbitMqProducerWithConfirmation(int index) throws IOException, TimeoutException {
        this.index = index;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1000);
        logger.info("Producer {} connected to queue: {}", index, QUEUE_NAME);

        channel.confirmSelect();
        logger.info("Producer {} enabled message confirmation.", index);
    }

    public int getIndex() {
        return index;
    }

    public void sendMessage(String message) throws IOException {
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        logger.info("Producer {} sent message: {}", index, message);

        try {
            if (!channel.waitForConfirms()) {
                logger.error("Producer {} failed to confirm message delivery: {}", index, message);
            } else {
                logger.info("Producer {} received confirmation for message: {}", index, message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Producer {} was interrupted while waiting for confirmation for message: {}", index, message, e);
        }
    }

    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                logger.info("Producer {} channel closed successfully.", index);
            }
        } catch (IOException | TimeoutException e) {
            logger.error("Error closing producer {} channel: {}", index, e.getMessage());
        }
    }
}

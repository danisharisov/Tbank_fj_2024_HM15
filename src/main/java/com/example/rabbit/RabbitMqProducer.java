package com.example.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMqProducer {
    private static final String QUEUE_NAME = "test_queue";
    private final Channel channel;
    private final int index;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqProducer.class);

    public RabbitMqProducer(int index) throws IOException, TimeoutException {
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
    }

    public int getIndex() {
        return index;
    }

    public void sendMessage(String message) throws IOException {
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        logger.info("Producer {} sent message: {}", index, message);
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

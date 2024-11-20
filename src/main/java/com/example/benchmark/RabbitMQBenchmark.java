package com.example.benchmark;

import com.rabbitmq.client.*;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class RabbitMQBenchmark {

    private static final String QUEUE_NAME = "test_queue";
    private static final String SMALL_MESSAGE = "small_message"; // Маленькое сообщение
    private static final int MESSAGE_COUNT = 1000;

    private Connection connection;
    private Channel channel;
    private static final AtomicInteger messageCount = new AtomicInteger(0);

    @Setup
    public void setup() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    @TearDown
    public void tearDown() throws Exception {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }

    @Benchmark
    public void testSingleProducerSingleConsumer() throws Exception {
        produceMessages(MESSAGE_COUNT);
        consumeMessages();
    }

    @Benchmark
    public void testMultipleProducersSingleConsumer() throws Exception {
        for (int i = 0; i < 3; i++) {
            produceMessages(MESSAGE_COUNT / 3);
        }
        consumeMessages();
    }

    @Benchmark
    public void testSingleProducerMultipleConsumers() throws Exception {
        produceMessages(MESSAGE_COUNT);
        for (int i = 0; i < 3; i++) {
            consumeMessages();
        }
    }

    @Benchmark
    public void testMultipleProducersMultipleConsumers() throws Exception {
        for (int i = 0; i < 3; i++) {
            produceMessages(MESSAGE_COUNT / 3);
        }
        for (int i = 0; i < 3; i++) {
            consumeMessages();
        }
    }

    @Benchmark
    public void testStressTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            produceMessages(MESSAGE_COUNT / 10);
        }
        for (int i = 0; i < 10; i++) {
            consumeMessages();
        }
    }

    private void produceMessages(int count) throws IOException {
        for (int i = 0; i < count; i++) {
            channel.basicPublish("", QUEUE_NAME, null, SMALL_MESSAGE.getBytes());
        }
    }

    private void consumeMessages() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            messageCount.incrementAndGet();
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }
}

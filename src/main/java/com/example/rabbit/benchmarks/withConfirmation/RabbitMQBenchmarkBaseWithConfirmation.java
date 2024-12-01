package com.example.rabbit.benchmarks.withConfirmation;

import com.example.BaseTest;
import com.example.rabbit.RabbitMqConsumerWithConfirmation;
import com.example.rabbit.RabbitMqProducerWithConfirmation;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class RabbitMQBenchmarkBaseWithConfirmation extends BaseTest {
    private List<RabbitMqProducerWithConfirmation> producers;
    private List<RabbitMqConsumerWithConfirmation> consumers;

    protected final int numberOfProducers;
    protected final int numberOfConsumers;

    public RabbitMQBenchmarkBaseWithConfirmation(int numberOfProducers, int numberOfConsumers) {
        this.numberOfProducers = numberOfProducers;
        this.numberOfConsumers = numberOfConsumers;
    }

    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
        producers = new ArrayList<>();
        consumers = new ArrayList<>();

        for (int i = 0; i < numberOfProducers; i++) {
            producers.add(new RabbitMqProducerWithConfirmation(i));
        }

        for (int i = 0; i < numberOfConsumers; i++) {
            consumers.add(new RabbitMqConsumerWithConfirmation());
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        consumers.forEach(RabbitMqConsumerWithConfirmation::close);
        producers.forEach(RabbitMqProducerWithConfirmation::close);
    }

    @Benchmark
    public void rabbitProducerConsumerWithConfirmation(Blackhole blackhole) {
        producers.forEach(producer -> {
            try {
                String message = "Test" + producer.getIndex();
                producer.sendMessage(message);
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        consumers.forEach(consumer -> {
            try {
                String message = consumer.consumeMessage();
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

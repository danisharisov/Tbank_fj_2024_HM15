package com.example.kafka.benchmarks;

import com.example.BaseTest;
import com.example.kafka.KafkaConsumer;
import com.example.kafka.KafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class KafkaBenchmarkBase extends BaseTest {

    private List<KafkaProducer> producers;
    private List<KafkaConsumer> consumers;

    protected final int numberOfProducers;
    protected final int numberOfConsumers;

    public KafkaBenchmarkBase(int numberOfProducers, int numberOfConsumers) {
        this.numberOfProducers = numberOfProducers;
        this.numberOfConsumers = numberOfConsumers;
    }

    @Setup(Level.Trial)
    public void setup() throws TimeoutException {
        producers = new ArrayList<>();
        consumers = new ArrayList<>();

        for (int i = 0; i < numberOfProducers; i++) {
            producers.add(new KafkaProducer("localhost:9092"));
        }

        for (int i = 0; i < numberOfConsumers; i++) {
            consumers.add(new KafkaConsumer("localhost:9092", "test-group", "test-topic"));
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        consumers.forEach(KafkaConsumer::close);
        producers.forEach(KafkaProducer::close);
    }

    @Benchmark
    public void kafkaProducerConsumer(Blackhole blackhole) {
        producers.forEach(producer -> {
            try {
                String message = "Test" + producer.hashCode();
                producer.sendMessages("test-topic", 1, message);
                blackhole.consume(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        consumers.forEach(consumer -> {
            try {
                ConsumerRecords<String, String> records = consumer.pollMessages(Duration.ofMillis(1000));
                records.forEach(record -> blackhole.consume(record.value()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
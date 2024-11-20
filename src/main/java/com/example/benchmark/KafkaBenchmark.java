package com.example.benchmark;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class KafkaBenchmark {

    private Producer<String, String> producer;
    private List<Consumer<String, String>> consumers;
    private static final String TOPIC = "benchmark_topic";
    private static final String SMALL_MESSAGE = "small_message";

    @Setup(Level.Trial)
    public void setup() {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Гарантия доставки
        producerProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        this.producer = new KafkaProducer<>(producerProps);

        this.consumers = new ArrayList<>();
    }

    private Properties getConsumerProperties(String groupId) {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return consumerProps;
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        producer.close();
        consumers.forEach(Consumer::close);
    }

    @Benchmark
    public void testSingleProducerSingleConsumer() {
        setupConsumers(1);
        sendMessages(1);
        consumeMessages();
    }

    @Benchmark
    public void testMultipleProducersSingleConsumer() {
        setupConsumers(1);
        sendMessages(3);
        consumeMessages();
    }

    @Benchmark
    public void testSingleProducerMultipleConsumers() {
        setupConsumers(3);
        sendMessages(1);
        consumeMessages();
    }

    @Benchmark
    public void testMultipleProducersMultipleConsumers() {
        setupConsumers(3);
        consumeMessages();
    }

    @Benchmark
    public void testStressTest() {
        setupConsumers(10);
        sendMessages(10);
        consumeMessages();
    }

    private void setupConsumers(int consumersCount) {
        consumers.clear();
        for (int i = 0; i < consumersCount; i++) {
            Consumer<String, String> consumer = new KafkaConsumer<>(getConsumerProperties("group-" + i));
            consumer.subscribe(Collections.singletonList(TOPIC));
            consumers.add(consumer);
        }
    }

    // Метод для отправки сообщений
    private void sendMessages(int producersCount) {
        for (int i = 0; i < producersCount; i++) {
            producer.send(new ProducerRecord<>(TOPIC, "key-" + i, SMALL_MESSAGE), (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                }
            });
        }
    }
    private void consumeMessages() {
        consumers.forEach(consumer -> {
            consumer.poll(java.time.Duration.ofMillis(100)).forEach(this::processMessage);
            consumer.commitSync();
        });
    }

    private void processMessage(ConsumerRecord<String, String> record) {
        simulateProcessing();
    }

    private void simulateProcessing() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

package com.example.rabbit.benchmarks;

public class SingleProducerSingleConsumer extends RabbitMQBenchmarkBase {

    public SingleProducerSingleConsumer() {
        super(1, 1);
    }
}
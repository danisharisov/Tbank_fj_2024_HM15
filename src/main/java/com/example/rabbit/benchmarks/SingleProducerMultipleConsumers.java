package com.example.rabbit.benchmarks;

public class SingleProducerMultipleConsumers extends RabbitMQBenchmarkBase {

    public SingleProducerMultipleConsumers() {
        super(1, 3);
    }
}
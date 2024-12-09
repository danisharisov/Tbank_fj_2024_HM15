package com.example.rabbit.benchmarks;

public class MultipleProducersSingleConsumer extends RabbitMQBenchmarkBase {

    public MultipleProducersSingleConsumer() {
        super(3, 1);
    }
}
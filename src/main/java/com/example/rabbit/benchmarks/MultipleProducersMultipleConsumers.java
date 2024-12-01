package com.example.rabbit.benchmarks;

public class MultipleProducersMultipleConsumers extends RabbitMQBenchmarkBase {

    public MultipleProducersMultipleConsumers() {
        super(3, 3);
    }
}
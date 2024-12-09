package com.example.rabbit.benchmarks.withConfirmation;

public class MultipleProducersSingleConsumerWithConfirmation extends RabbitMQBenchmarkBaseWithConfirmation {
    public MultipleProducersSingleConsumerWithConfirmation() {
        super(3, 1);
    }
}
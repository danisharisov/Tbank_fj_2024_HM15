package com.example.rabbit.benchmarks.withConfirmation;

public class SingleProducerSingleConsumerWithConfirmation extends RabbitMQBenchmarkBaseWithConfirmation {
    public SingleProducerSingleConsumerWithConfirmation() {
        super(1, 1);
    }
}

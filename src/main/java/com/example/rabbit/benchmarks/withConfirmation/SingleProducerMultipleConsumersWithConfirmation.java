package com.example.rabbit.benchmarks.withConfirmation;

public class SingleProducerMultipleConsumersWithConfirmation extends RabbitMQBenchmarkBaseWithConfirmation {
    public SingleProducerMultipleConsumersWithConfirmation() {
        super(1, 3);
    }
}


package com.example.kafka.benchmarks;

public class SingleProducerMultipleConsumers extends KafkaBenchmarkBase {

    public SingleProducerMultipleConsumers() {
        super(1, 3);
    }
}

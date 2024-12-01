package com.example.kafka.benchmarks;

public class SingleProducerSingleConsumer extends KafkaBenchmarkBase {

    public SingleProducerSingleConsumer() {
        super(1, 1);
    }
}
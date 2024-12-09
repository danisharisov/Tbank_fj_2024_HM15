package com.example.kafka.benchmarks;

public class MultipleProducersSingleConsumer extends KafkaBenchmarkBase {

    public MultipleProducersSingleConsumer() {
        super(3, 1);
    }
}
package com.example.rabbit.benchmarks;

import com.example.rabbit.benchmarks.withConfirmation.RabbitMQBenchmarkBaseWithConfirmation;

public class StressTestWithConfirmation extends RabbitMQBenchmarkBaseWithConfirmation {
    public StressTestWithConfirmation() {
        super(10,10);
    }
}

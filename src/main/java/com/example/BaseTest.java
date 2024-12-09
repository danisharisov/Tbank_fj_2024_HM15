package com.example;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 2)
@State(Scope.Thread)
@Measurement(iterations = 10, time = 2)
@Fork(1)
public abstract class BaseTest {
}
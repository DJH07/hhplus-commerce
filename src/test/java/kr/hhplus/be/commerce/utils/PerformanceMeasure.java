package kr.hhplus.be.commerce.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;
@Slf4j
public class PerformanceMeasure {
    public static <T> T measure(String label, Supplier<T> supplier) {
        long startTime = System.currentTimeMillis();
        T result = supplier.get();
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("{} Execution Time: {} ms", label, elapsedTime);
        return result;
    }
}

package edu.java.bot.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProcessedTgMessagesMetric {

    private final Counter processedTgMessagesCounter;

    public ProcessedTgMessagesMetric(MeterRegistry meterRegistry) {
        processedTgMessagesCounter = Counter.builder("processed_tg_messages.counter").register(meterRegistry);
    }

    public void increment() {
        processedTgMessagesCounter.increment();
    }

}

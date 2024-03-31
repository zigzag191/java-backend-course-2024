package edu.java.common.client;

import edu.java.common.exception.HttpRequestException;
import java.time.Duration;
import java.util.function.Predicate;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class LinearRetry extends Retry {

    private final int maxRetries;
    private final Duration step;
    private final Predicate<HttpStatusCode> filter;

    public LinearRetry(
        int maxRetries,
        Duration step,
        Predicate<HttpStatusCode> filter
    ) {
        this.maxRetries = maxRetries;
        this.step = step;
        this.filter = filter;
    }

    @Override
    public Publisher<?> generateCompanion(Flux<RetrySignal> flux) {
        return flux.flatMap(this::determineNextRetry);
    }

    private Mono<Long> determineNextRetry(RetrySignal retrySignal) {
        if (!(retrySignal.failure() instanceof HttpRequestException failure)) {
            return Mono.error(retrySignal.failure());
        }
        long currentRetry = retrySignal.totalRetries() + 1;
        if (!filter.test(failure.getStatusCode())) {
            return Mono.error(failure);
        }
        if (currentRetry > maxRetries) {
            return Mono.error(failure);
        }
        var delay = step.multipliedBy(currentRetry);
        return Mono.delay(delay).thenReturn(retrySignal.totalRetries());
    }

}

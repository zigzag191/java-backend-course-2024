package edu.java.common.client;

import edu.java.common.exception.HttpRequestException;
import java.time.Duration;
import java.util.function.Predicate;
import org.springframework.http.HttpStatusCode;
import reactor.util.retry.Retry;

public abstract class CustomRetrySpecBuilder {

    protected int maxRetries = 0;
    protected Duration step = Duration.ZERO;
    protected Predicate<HttpStatusCode> statusCodeFilter = HttpStatusCode::is5xxServerError;

    public CustomRetrySpecBuilder withStatusCodeFilter(Predicate<HttpStatusCode> statusCodeFilter) {
        this.statusCodeFilter = statusCodeFilter;
        return this;
    }

    public CustomRetrySpecBuilder withMaxReties(int maxRetries) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries must be >= 0");
        }
        this.maxRetries = maxRetries;
        return this;
    }

    public CustomRetrySpecBuilder withStep(Duration step) {
        this.step = step;
        return this;
    }

    public abstract Retry build();

    public static class Linear extends CustomRetrySpecBuilder {
        @Override
        public Retry build() {
            return new LinearRetry(maxRetries, step, statusCodeFilter);
        }
    }

    public static class Constant extends CustomRetrySpecBuilder {
        @Override
        public Retry build() {
            return Retry
                .fixedDelay(maxRetries, step)
                .filter(t -> {
                    if (t instanceof HttpRequestException e) {
                        return statusCodeFilter.test(e.getStatusCode());
                    }
                    return false;
                })
                .onRetryExhaustedThrow((spec, retrySignal) -> {
                    if (retrySignal.failure() instanceof HttpRequestException e) {
                        throw e;
                    }
                    throw new RuntimeException(retrySignal.failure());
                });
        }
    }

    public static class Exponential extends CustomRetrySpecBuilder {
        @Override
        public Retry build() {
            return Retry
                .backoff(maxRetries, step)
                .filter(t -> {
                    if (t instanceof HttpRequestException e) {
                        return statusCodeFilter.test(e.getStatusCode());
                    }
                    return false;
                })
                .onRetryExhaustedThrow((spec, retrySignal) -> {
                    if (retrySignal.failure() instanceof HttpRequestException e) {
                        throw e;
                    }
                    throw new RuntimeException(retrySignal.failure());
                });
        }
    }

}

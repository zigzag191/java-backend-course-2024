package edu.java.common.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

@Log4j2
public class ThrottlingFilter implements Filter {

    private final int bucketCapacity;
    private final Duration refillInterval;
    private final Cache<String, Bucket> cache;

    public ThrottlingFilter(Duration cacheExpirationDuration, int bucketCapacity, Duration refillInterval) {
        this.bucketCapacity = bucketCapacity;
        this.refillInterval = refillInterval;
        this.cache = Caffeine.newBuilder()
            .expireAfterAccess(cacheExpirationDuration)
            .build();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        var ip = extractIpFromRequest((HttpServletRequest) servletRequest);
        var bucket = cache.get(ip, key -> createBucket());
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            var httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setContentType("text/plain");
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().append("Too many requests");
        }
    }

    private String extractIpFromRequest(HttpServletRequest servletRequest) {
        var ip = servletRequest.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            return servletRequest.getRemoteAddr();
        }
        return ip.contains(",") ? ip.split(",")[0] : ip;
    }

    private Bucket createBucket() {
        return Bucket.builder()
            .addLimit(limit -> limit
                .capacity(bucketCapacity)
                .refillIntervally(bucketCapacity, refillInterval)
                .initialTokens(bucketCapacity)
            )
            .build();
    }

}

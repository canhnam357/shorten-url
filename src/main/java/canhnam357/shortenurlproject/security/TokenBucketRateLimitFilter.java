package canhnam357.shortenurlproject.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class TokenBucketRateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX   = "rate_limit:";
    private static final long   CAPACITY     = 1024;
    private static final double REFILL_RATE  = 20.0;
    private static final long   TTL_SECONDS  = 60;

    private static final DefaultRedisScript<Long> TOKEN_BUCKET_SCRIPT;

    static {
        TOKEN_BUCKET_SCRIPT = new DefaultRedisScript<>();
        TOKEN_BUCKET_SCRIPT.setResultType(Long.class);
        TOKEN_BUCKET_SCRIPT.setScriptText("""
            local key          = KEYS[1]
            local now          = tonumber(ARGV[1])
            local capacity     = tonumber(ARGV[2])
            local refill_rate  = tonumber(ARGV[3])
            local ttl          = tonumber(ARGV[4])
            local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')
            local tokens      = tonumber(bucket[1])
            local last_refill = tonumber(bucket[2])
            if tokens == nil or last_refill == nil then
                tokens      = capacity
                last_refill = now
            end
            local elapsed       = math.max(0, now - last_refill)
            local refilled      = elapsed * refill_rate
            local new_tokens    = math.min(capacity, tokens + refilled)
            local allowed = 0
            if new_tokens >= 1 then
                new_tokens = new_tokens - 1
                last_refill = now
                allowed = 1
            end
            redis.call('HSET', key,
                'tokens',      string.format("%.4f", new_tokens),
                'last_refill', tostring(last_refill))
            redis.call('EXPIRE', key, ttl)
            return allowed
        """);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("Request received: " + request.getRequestURI());

        String key = KEY_PREFIX + request.getRequestURI();

        long nowMs         = System.currentTimeMillis();
        double refillPerMs = REFILL_RATE / 1000.0;

        Long allowed = redisTemplate.execute(
                TOKEN_BUCKET_SCRIPT,
                Collections.singletonList(key),
                String.valueOf(nowMs),
                String.valueOf(CAPACITY),
                String.format("%.6f", refillPerMs),
                String.valueOf(TTL_SECONDS)
        );

        if (allowed == 0) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "error": "Too many login attempts"
                }
                """);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
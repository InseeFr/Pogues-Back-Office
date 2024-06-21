package fr.insee.pogues.configuration.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(name = "feature.cache.enabled", havingValue = "true")
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${feature.cache.retention-time-in-days.ddi-as}")
    private long ddiAsCacheRetentionTime;

    @Value("${feature.cache.retention-time-in-days.magma}")
    private long magmaCacheRetentionTime;

    @Value("${feature.cache.retention-time-in-days.pogues.stamps}")
    private long poguesStampsCacheRetentionTime;

    @Bean
    protected CaffeineCache unitsCacheCache() {
        return new CaffeineCache(CacheName.UNITS,
                Caffeine.newBuilder()
                        .maximumSize(10)
                        .expireAfterWrite(ddiAsCacheRetentionTime, TimeUnit.DAYS)
                        .build());
    }

    @Bean
    protected CaffeineCache seriesCache() {
        return new CaffeineCache(CacheName.SERIES,
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(magmaCacheRetentionTime, TimeUnit.DAYS)
                        .build());
    }
    @Bean
    protected CaffeineCache serieCache() {
        return new CaffeineCache(CacheName.SERIE,
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(magmaCacheRetentionTime, TimeUnit.DAYS)
                        .build());
    }

    @Bean
    protected CaffeineCache operationsCache() {
        return new CaffeineCache(CacheName.OPERATIONS,
                Caffeine.newBuilder()
                        .maximumSize(10000)
                        .expireAfterWrite(magmaCacheRetentionTime, TimeUnit.DAYS)
                        .build());
    }
    @Bean
    protected CaffeineCache operationCache() {
        return new CaffeineCache(CacheName.OPERATION,
                Caffeine.newBuilder()
                        .maximumSize(10000)
                        .expireAfterWrite(magmaCacheRetentionTime, TimeUnit.DAYS)
                        .build());
    }

    @Bean
    protected CaffeineCache stampsCache() {
        return new CaffeineCache(CacheName.STAMPS,
                Caffeine.newBuilder()
                        .maximumSize(10000)
                        .expireAfterWrite(poguesStampsCacheRetentionTime, TimeUnit.DAYS)
                        .build());
    }

}

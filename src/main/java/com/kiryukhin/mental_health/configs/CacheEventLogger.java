package com.kiryukhin.mental_health.configs;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheEventLogger implements CacheEventListener<String, Object> {

    @Override
    public void onEvent(CacheEvent<? extends String, ? extends Object> cacheEvent) {
        log.info("Cache Event: Type = {}, Key = {}, Old Value = {}, New Value = {}",
                cacheEvent.getType(),
                cacheEvent.getKey(),
                cacheEvent.getOldValue(),
                cacheEvent.getNewValue());
    }
}

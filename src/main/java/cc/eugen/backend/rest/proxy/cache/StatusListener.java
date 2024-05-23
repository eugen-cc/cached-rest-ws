package cc.eugen.backend.rest.proxy.cache;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Configuration
public class StatusListener implements CacheEventListener<Object, Object> {


    @Override
    @EventListener
    public void onEvent(CacheEvent<?, ?> event) {
        log.info("onEvent");
        if (event.getType() == EventType.UPDATED) {
            var cacheStatus = event.getOldValue() != null ? "HIT" : "MISS";
            var value = event.getNewValue();
            if (value instanceof ResponseEntity<?> responseEntity) {
                responseEntity.getHeaders().add("Cache-Status", cacheStatus);
            }
        }
    }
}

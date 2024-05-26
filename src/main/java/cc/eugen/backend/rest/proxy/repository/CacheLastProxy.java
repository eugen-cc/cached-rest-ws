package cc.eugen.backend.rest.proxy.repository;

import cc.eugen.backend.rest.proxy.model.ProxyResourceEntity;
import cc.eugen.backend.ws.generated.GetXDataByIdRequest;
import cc.eugen.backend.ws.generated.GetXDataByMetadataRequest;
import cc.eugen.backend.ws.generated.MetadataType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
@EnableCaching
@ConditionalOnProperty(name = "cache.strategy", havingValue = "cache-last", matchIfMissing = true)
public class CacheLastProxy implements IDataRepository {

    private CacheManager cacheManager;

    private WebServiceTemplate client;

    private DomainMapper mapper;

    /**
     * findAll() operation that caches the result like defined in ehcache.xml
     *
     * @return a list of all Entity Objects
     */
    @CircuitBreaker(name = "backendCB", fallbackMethod = "getAllFallback")
    public List<ProxyResourceEntity> getAll() {
        final var request = new cc.eugen.backend.ws.generated.GetXDataRequest();
        final var response = (cc.eugen.backend.ws.generated.GetXDataResponse) client.marshalSendAndReceive(request);
        final var entityList = response.getXData().stream()
                .map(xData -> mapper.toResourceEntity(xData, response.getResponseTime()))
                .collect(Collectors.toList());
        cacheManager.getCache("allCache").put("allCache", entityList);
        return entityList;

    }

    /**
     * @param t Error that occured
     * @return a cached list or an empty list
     */
    public List<ProxyResourceEntity> getAllFallback(Throwable t) {
        return List.of();
    }


    /**
     * Caches objects for a range of IDs
     *
     * @param id id of a ProxyResourceEntity
     * @return a (possibly cached) ProxyResourceEntity
     */
    @CircuitBreaker(name = "backendCB", fallbackMethod = "byIdFallback")
    public Optional<ProxyResourceEntity> getById(long id) {
        final var request = new GetXDataByIdRequest();
        request.setId(id);
        final var response = (cc.eugen.backend.ws.generated.GetXDataByIdResponse) client.marshalSendAndReceive(request);
        final var xData = response.getXData();
        final var entity = mapper.toResourceEntity(xData, response.getResponseTime());
        final var cache = cacheManager.getCache("byIdCache");
        cache.put(id, entity);
        return Optional.ofNullable(entity);
    }


    /**
     * @param id cacheKey
     * @param t  Error that occured
     * @return cached entity if available
     */
    public Optional<ProxyResourceEntity> byIdFallback(long id, Throwable t) {
        log.info("Fallback:  GetById {}", id);
        final var cache = cacheManager.getCache("byIdCache");
        if (cache != null) {
            final var entry = cache.get(id);
            if (entry != null) {
                log.debug("Return entity from cache!");
                return Optional.ofNullable((ProxyResourceEntity) entry.get());
            }
        }
        return Optional.empty();
    }

    /**
     * @param key   metadata key
     * @param value metadata value
     * @return a list of ProxyResourceEntities having matching metadata
     */
    @CircuitBreaker(name = "backendCB", fallbackMethod = "byMetadataFallback")
    public List<ProxyResourceEntity> getByMetadata(String key, String value) {
        final var request = new GetXDataByMetadataRequest();
        final var metadata = new MetadataType();
        metadata.setKey(key);
        metadata.setValue(value);
        request.setXMetadata(metadata);
        final var response = (cc.eugen.backend.ws.generated.GetXDataByMetadataResponse) client.marshalSendAndReceive(request);
        final var responseList = response.getXData().stream()
                .map(xData -> mapper.toResourceEntity(xData, response.getResponseTime()))
                .collect(Collectors.toList());
        cacheManager.getCache("byMetadataCache").put(key + "/" + value, responseList);
        return responseList;
    }

    public List<ProxyResourceEntity> byMetadataFallback(String key, String value, Throwable t) {
        log.debug("could not hit backend");
        log.debug("Check for data in cache. Key: {}", key + "/" + value);
        final var entry = cacheManager.getCache("byMetadataCache").get(key + "/" + value);
        if (entry == null) return List.of();
        return (List<ProxyResourceEntity>) entry.get();
    }

}

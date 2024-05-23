package cc.eugen.backend.rest.proxy.repository;

import cc.eugen.backend.rest.proxy.model.ProxyResourceEntity;
import cc.eugen.backend.ws.generated.GetXDataByIdRequest;
import cc.eugen.backend.ws.generated.GetXDataByMetadataRequest;
import cc.eugen.backend.ws.generated.MetadataType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
@EnableCaching
public class ProxyResourceRepository {

    private WebServiceTemplate client;

    /**
     * findAll() operation that caches the result like defined in ehcache.xml
     *
     * @return a list of all Entity Objects
     */
    @Cacheable(
            value = "allCache"
    )
    @CircuitBreaker(name = "backendCB", fallbackMethod = "fallback")
    public List<ProxyResourceEntity> getAll() {
        var request = new cc.eugen.backend.ws.generated.GetXDataRequest();
        var response = (cc.eugen.backend.ws.generated.GetXDataResponse) client.marshalSendAndReceive(request);
        return response.getXData().stream()
                .map(xData -> toResourceEntity(xData, response.getResponseTime()))
                .collect(Collectors.toList());
    }

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
        var request = new GetXDataByIdRequest();
        request.setId(id);
        var response = (cc.eugen.backend.ws.generated.GetXDataByIdResponse) client.marshalSendAndReceive(request);
        var xData = response.getXData();
        return Optional.ofNullable(toResourceEntity(xData, response.getResponseTime()));
    }

    public Optional<ProxyResourceEntity> byIdFallback(long id, Throwable t) {
        log.info("Fallback:  GetById {}", id);
        return Optional.empty();
    }

    /**
     * @param key   metadata key
     * @param value metadata value
     * @return a list of ProxyResourceEntities having matching metadata
     */
    @Cacheable
            (
                    value = "byMetadataCache",
                    key = "#key+#value"
            )
    public List<ProxyResourceEntity> getByMetadata(String key, String value) {
        var request = new GetXDataByMetadataRequest();
        var metadata = new MetadataType();
        metadata.setKey(key);
        metadata.setValue(value);
        request.setXMetadata(metadata);
        var response = (cc.eugen.backend.ws.generated.GetXDataByMetadataResponse) client.marshalSendAndReceive(request);
        return response.getXData().stream()
                .map(xData -> toResourceEntity(xData, response.getResponseTime()))
                .collect(Collectors.toList());

    }

    /**
     * @return converts a xData object to a ProxyResourceEntity
     */
    private ProxyResourceEntity toResourceEntity(cc.eugen.backend.ws.generated.XData xData, XMLGregorianCalendar timestamp) {
        log.debug(xData.toString());
        return ProxyResourceEntity.builder()
                .id(xData.getId())
                .name(xData.getName())
                .description(xData.getDescription())
                .timestamp(timestamp.toGregorianCalendar().getTime())
                .metadata(xData.getXMetadata()
                        .stream().map(kv -> new ProxyResourceEntity.KeyValuePair(kv.getKey(), kv.getValue())).toList())
                .build();
    }
}

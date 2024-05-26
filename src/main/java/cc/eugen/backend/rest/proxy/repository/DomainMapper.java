package cc.eugen.backend.rest.proxy.repository;

import cc.eugen.backend.rest.proxy.model.ProxyResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class DomainMapper {
    /**
     * @return converts a xData object to a ProxyResourceEntity
     */
    public ProxyResourceEntity toResourceEntity(cc.eugen.backend.ws.generated.XData xData, XMLGregorianCalendar timestamp) {
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

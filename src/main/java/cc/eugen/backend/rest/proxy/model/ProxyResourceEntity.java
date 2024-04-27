package cc.eugen.backend.rest.proxy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class ProxyResourceEntity implements Serializable {
    private long id;
    private String name;
    private String description;
    private Date timestamp;
    private List<KeyValuePair> metadata;

    @AllArgsConstructor
    @Getter
    public static class KeyValuePair implements Serializable {
        private String key;
        private String value;
    }
}

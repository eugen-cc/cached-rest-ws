package cc.eugen.backend.rest.proxy.repository;

import cc.eugen.backend.rest.proxy.model.ProxyResourceEntity;

import java.util.List;
import java.util.Optional;

public interface IDataRepository {

    List<ProxyResourceEntity> getAll();

    Optional<ProxyResourceEntity> getById(long id);

    List<ProxyResourceEntity> getByMetadata(String key, String value);
}

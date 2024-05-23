package cc.eugen.backend.rest.proxy.service;

import cc.eugen.backend.rest.proxy.model.ProxyResourceEntity;
import cc.eugen.backend.rest.proxy.repository.ProxyResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ProxyDataService {


    private ProxyResourceRepository repo;
    private ProxyResourceRepository cachedRepo;

    public List<ProxyResourceEntity> getAllResourceEntities() {
        return repo.getAll();
    }

    public Optional<ProxyResourceEntity> getResourceEntityById(long id) {
        return repo.getById(id);
    }

    public List<ProxyResourceEntity> getByMetadata(String key, String value) {
        return repo.getByMetadata(key, value);
    }
}

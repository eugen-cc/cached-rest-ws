package cc.eugen.backend.rest.proxy.controller;

import cc.eugen.backend.rest.proxy.model.ProxyResourceEntity;
import cc.eugen.backend.rest.proxy.service.ProxyDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "XData Controller", description = "XData Operations")
@RestController
@RequestMapping("/proxy/xdata")
@AllArgsConstructor
@Slf4j
public class DataServiceController {

    private ProxyDataService dataService;

    @GetMapping
    @Operation(summary = "Retrieve all data objects from backend")
    @ApiResponse(responseCode = "200", description = "List of XData objects")
    public ResponseEntity<List<ProxyResourceEntity>> getAll() {
        log.debug("get all resources!");
        var entities = dataService.getAllResourceEntities();
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a data object by ID")
    @ApiResponse(responseCode = "200", description = "Requested Object")
    @ApiResponse(responseCode = "404", description = "Object with this ID not found")
    public ResponseEntity<ProxyResourceEntity> getById(@PathVariable("id") long id) {
        log.debug("by id {}", id);
        var optionalEntity = dataService.getResourceEntityById(id);
        return optionalEntity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/metadata")
    @Operation(summary = "Retrieve a List of objects with metadata corresponding to key and value parameter")
    @ApiResponse(responseCode = "200", description = "List of Objects")
    public ResponseEntity<List<ProxyResourceEntity>> getByMetadata(@RequestParam String key, @RequestParam String value) {
        log.debug("by metadata, key={} , value={}", key, value);
        var entities = dataService.getByMetadata(key, value);
        return ResponseEntity.ok(entities);
    }
}

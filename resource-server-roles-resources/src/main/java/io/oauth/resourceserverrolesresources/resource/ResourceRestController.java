package io.oauth.resourceserverrolesresources.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth.resourceserverrolesresources.web.page.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static io.oauth.resourceserverrolesresources.web.utils.ApiUtils.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/resources")
public class ResourceRestController {
    private static final String DEFAULT_PATH = "/api/resources/";
    private final ResourceService resourceService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> addResource(@RequestBody String resource, @RequestParam("role-id") String roleId) throws JsonProcessingException, URISyntaxException {
        Resource resourceForAdd = objectMapper.readValue(resource, Resource.class);

        Resource result = resourceService.addResource(resourceForAdd, roleId);

        return successCreated(DEFAULT_PATH + result.getId());
    }

    @PutMapping
    public ResponseEntity<Void>  updateResource(@RequestBody String resource, @RequestParam("role-id") String roleId) throws JsonProcessingException {
        Resource resourceForUpdate = objectMapper.readValue(resource, Resource.class);

        resourceService.updateResource(resourceForUpdate, roleId);

        return successNoContent();
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable String resourceId){
        resourceService.deleteResource(resourceId);

        return successNoContent();
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<String> findResourceById(@PathVariable String resourceId) throws JsonProcessingException {
        Resource resource = resourceService.findById(resourceId);

        String result = objectMapper.writeValueAsString(resource);

        return successOk(result, MediaType.APPLICATION_JSON);
    }

    @GetMapping
    public ResponseEntity<String> findResources(Pageable pageable) throws JsonProcessingException {
        String result = null;

        if(pageable == null){
            List<Resource> resources = resourceService.findAll();
            result = objectMapper.writeValueAsString(resources);
        }else{
            Map<String, Object> resourcesWithTotalCount = resourceService.findResources(pageable.getOffset(), pageable.getSize());
            result = objectMapper.writeValueAsString(resourcesWithTotalCount);
        }

        return successOk(result, MediaType.APPLICATION_JSON);
    }

}

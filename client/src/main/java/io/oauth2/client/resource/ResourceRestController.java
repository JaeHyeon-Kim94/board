package io.oauth2.client.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth2.client.resource.dto.ResourceRequestDto;
import io.oauth2.client.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.oauth2.client.web.page.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.oauth2.client.web.utils.ApiUtils.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/resources")
public class ResourceRestController {
    private static final String DEFAULT_PATH = "/api/resources/";
    private final ResourceService resourceService;
    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> addResource(@RequestBody @Validated ResourceRequestDto dto) throws JsonProcessingException, URISyntaxException {
        Resource result = resourceService.addResource(dto);
        urlFilterInvocationSecurityMetadataSource.reload();
        return successCreated(DEFAULT_PATH + result.getId());
    }

    @PutMapping("/{resourceId}")
    public ResponseEntity<Void>  updateResource(@RequestBody @Validated ResourceRequestDto dto, @PathVariable Long resourceId) throws JsonProcessingException {
        dto.setId(resourceId);
        resourceService.updateResource(dto);
        urlFilterInvocationSecurityMetadataSource.reload();
        return successNoContent();
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId){
        resourceService.deleteResource(resourceId);
        urlFilterInvocationSecurityMetadataSource.reload();
        return successNoContent();
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<Resource> findResourceById(@PathVariable Long resourceId) throws JsonProcessingException {
        Resource resource = resourceService.findById(resourceId);

        return successOk(resource);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> findResources(Pageable pageable) throws JsonProcessingException {
        Map<String, Object> result = null;

        if(pageable == null){
            result = Map.of("resources", resourceService.findAll());
        }else{
            result = resourceService.findResources(pageable.getOffset(), pageable.getSize());
        }

        return successOk(result);
    }

}

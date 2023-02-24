package io.oauth.resourceserverrolesresources.repository.impl;

import io.oauth.resourceserverrolesresources.repository.ResourceRepository;
import io.oauth.resourceserverrolesresources.repository.impl.mapper.ResourceMapper;
import io.oauth.resourceserverrolesresources.web.domain.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ResourceRepositoryMybatis implements ResourceRepository {

    private final ResourceMapper resourceMapper;

    @Override
    public List<Resource> findAll() {
        return resourceMapper.findAll();
    }
}

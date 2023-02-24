package io.oauth.resourceserverboard.repository.impl;

import io.oauth.resourceserverboard.repository.ResourceRepository;
import io.oauth.resourceserverboard.repository.impl.mapper.ResourceMapper;
import io.oauth.resourceserverboard.web.domain.Resource;
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

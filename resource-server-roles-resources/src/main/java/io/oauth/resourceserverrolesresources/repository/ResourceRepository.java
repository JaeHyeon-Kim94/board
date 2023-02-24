package io.oauth.resourceserverrolesresources.repository;


import io.oauth.resourceserverrolesresources.web.domain.Resource;

import java.util.List;

public interface ResourceRepository {

    List<Resource> findAll();

}

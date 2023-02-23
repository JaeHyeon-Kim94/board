package io.oauth.resourceserverroleresource.repository;


import io.oauth.resourceserverroleresource.web.domain.Resource;

import java.util.List;

public interface ResourceRepository {

    List<Resource> findAll();

}

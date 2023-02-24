package io.oauth.resourceserverboard.repository;


import io.oauth.resourceserverboard.web.domain.Resource;

import java.util.List;

public interface ResourceRepository {

    List<Resource> findAll();

}

package io.oauth2.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@Disabled
@ActiveProfiles("test")
public abstract class BaseTest {

    @Autowired
    protected ObjectMapper om;
//    @Autowired
//    protected TestRestTemplate rt;

    @Autowired
    protected MockMvc mvc;

    protected String url;

    protected HttpHeaders header;


}

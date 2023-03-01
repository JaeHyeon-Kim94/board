package io.oauth2.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@Disabled
@ActiveProfiles("test")
public abstract class BaseTest {

    @Autowired
    protected ObjectMapper om;

    @Autowired
    protected MockMvc mvc;

    protected <T> T mapJsonToObject(String body, Class<T> type) throws JsonProcessingException {
        T t = om.readValue(body, type);
        return t;
    }
}

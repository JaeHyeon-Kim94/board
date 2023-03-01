package io.oauth2.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@Disabled
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseTest {

    @Autowired
    protected ObjectMapper om;

    @Autowired
    protected MockMvc mvc;

}

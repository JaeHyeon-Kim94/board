package io.oauth2.client.user;

import io.oauth2.client.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class UserRestControllerTest extends BaseTest {

    public UserRestControllerTest() {
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setAccept(List.of(MediaType.APPLICATION_JSON));

        url = "/api/users";
    }

    @Test
    void test() throws Exception {
        System.out.println();
    }

}

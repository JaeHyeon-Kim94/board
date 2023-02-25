package io.oauth.resourceserverrolesresources.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth.resourceserverrolesresources.web.page.SimplePageRequestArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class AppConfig implements WebMvcConfigurer {

    private final SimplePageRequestArgumentResolver simplePageRequestArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(simplePageRequestArgumentResolver);
    }
}

package io.oauth.resourceserverrolesresources.security.init;


import io.oauth.resourceserverrolesresources.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SecurityInitializer implements ApplicationRunner {

    private final RoleService roleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        roleService.setRoleHierarchy();
    }
}

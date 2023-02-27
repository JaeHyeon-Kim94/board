package io.oauth2.client.resource.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.oauth2.client.resource.Resource;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//for Create, Update
@Data
public class ResourceRequestDto {

    private Long id;
    @NotBlank
    private String type;
    @NotNull
    private Long level;
    @NotBlank
    private String value;

    @NotBlank
    @JsonProperty("role_id")
    private String roleId;

    @JsonProperty("http_method")
    private String httpMethod;


    public static Resource toResource(ResourceRequestDto dto){
        return Resource.builder()
                .id(dto.getId())
                .type(dto.getType())
                .level(dto.getLevel())
                .value(dto.getValue())
                .httpMethod(dto.getHttpMethod())
                    .build();
    }
}

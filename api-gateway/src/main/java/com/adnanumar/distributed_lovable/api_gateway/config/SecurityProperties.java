package com.adnanumar.distributed_lovable.api_gateway.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.security")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityProperties {

    List<String> publicRoutes;

}

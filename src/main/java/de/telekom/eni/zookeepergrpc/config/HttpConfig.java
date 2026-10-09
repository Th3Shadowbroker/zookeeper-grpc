package de.telekom.eni.zookeepergrpc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("http")
@Getter
@Setter
public class HttpConfig {

    private boolean enabled;

}

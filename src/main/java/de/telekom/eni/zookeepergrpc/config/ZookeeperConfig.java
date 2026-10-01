package de.telekom.eni.zookeepergrpc.config;

import de.telekom.eni.zookeepergrpc.ZookeeperGrpcApplication;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties("zookeeper")
@Getter
@Setter
@Slf4j
public class ZookeeperConfig {

    String uri;

    ZookeeperBackoffConfig backoff;

    @Bean
    public CuratorFramework curatorClient(ZookeeperConfig config) {
        var backoff = new ExponentialBackoffRetry(config.backoff.baseSleepMs, config.backoff.maxRetries);
        var client = CuratorFrameworkFactory.newClient(config.getUri(), backoff);
        client.getConnectionStateListenable().addListener((c, state) -> {
            if (state == ConnectionState.LOST) {
                log.info("Zookeeper connection lost. Terminating...");
                SpringApplication.exit(ZookeeperGrpcApplication.getApplicationContext());
            }
        });

        client.start();
        try {
            client.blockUntilConnected(5, TimeUnit.SECONDS);
            return client;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

}

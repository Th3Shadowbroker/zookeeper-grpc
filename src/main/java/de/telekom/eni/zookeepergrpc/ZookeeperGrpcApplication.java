package de.telekom.eni.zookeepergrpc;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ZookeeperGrpcApplication {

    @Getter
    private static ApplicationContext applicationContext;

    static void main(String[] args) {
         applicationContext = SpringApplication.run(ZookeeperGrpcApplication.class, args);
    }

}

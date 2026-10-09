package de.telekom.eni.zookeepergrpc.rest;

import de.telekom.eni.zookeepergrpc.service.ZkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnExpression("${http.enabled}")
@DependsOn({"zkService"})
@Slf4j
public class ZkHttpController {

    private final ZkService zkService;

    public ZkHttpController(ZkService zkService) {
        this.zkService = zkService;
    }

    @PutMapping("/write")
    public ResponseEntity<RestResponse> write(
            @RequestBody String body,
            @RequestParam String zNode
    ) {
        try {
            zkService.write(zNode, body);

            log.info("Successfully updated z-node {}: {}", zNode, body);
            return new ResponseEntity<>(
                    new RestResponse(true, null),
                    HttpStatus.OK
            );
        } catch (Exception e) {

            log.error("Error while writing to zookeeper", e);
            return new ResponseEntity<>(
                    new RestResponse(false, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}

package de.telekom.eni.zookeepergrpc.rest;

import de.telekom.eni.zookeepergrpc.service.ZkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ZkHttpControllerTest {

    private ZkService zkService;
    private ZkHttpController zkHttpController;

    @BeforeEach
    void setUp() {
        zkService = mock(ZkService.class);
        zkHttpController = new ZkHttpController(zkService);
    }

    @Test
    void testWriteSuccess() throws Exception {
        var zNode = "/test/node";
        var payload = "same payload";

        ResponseEntity<RestResponse> response = zkHttpController.write(payload, zNode);

        verify(zkService).write(zNode, payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().message()).isNull();
    }

    @Test
    void testWriteError() throws Exception {
        var zNode = "/test/error-node";
        var payload = "fail";

        doThrow(new RuntimeException("ZK connection error"))
                .when(zkService).write(zNode, payload);

        ResponseEntity<RestResponse> response = zkHttpController.write(payload, zNode);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
        assertThat(response.getBody().message()).isEqualTo("ZK connection error");
    }
}

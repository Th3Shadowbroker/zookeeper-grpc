package de.telekom.eni.zookeepergrpc.service;

import de.telekom.eni.zookeepergrpc.proto.WriteRequest;
import de.telekom.eni.zookeepergrpc.proto.WriteResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ZkGrpcServiceTest {

    private ZkService zkService;
    private ZkGrpcService zkGrpcService;

    @BeforeEach
    void setUp() {
        zkService = mock(ZkService.class);
        zkGrpcService = new ZkGrpcService(zkService);
    }

    @Test
    void testWriteSuccess() throws Exception {
        var payload = "same payload";
        WriteRequest request = WriteRequest.newBuilder()
                .setZnode("/test/node")
                .setData(payload)
                .build();

        StreamObserver<WriteResponse> responseObserver = mock(StreamObserver.class);

        zkGrpcService.write(request, responseObserver);

        verify(zkService).write("/test/node", payload);

        ArgumentCaptor<WriteResponse> responseCaptor = ArgumentCaptor.forClass(WriteResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        WriteResponse response = responseCaptor.getValue();
        assertThat(response.getSuccess()).isTrue();
    }

    @Test
    void testWriteError() throws Exception {
        doThrow(new RuntimeException("ZK connection error"))
                .when(zkService).write(anyString(), anyString());

        WriteRequest request = WriteRequest.newBuilder()
                .setZnode("/test/error-node")
                .setData("fail")
                .build();

        StreamObserver<WriteResponse> responseObserver = mock(StreamObserver.class);

        zkGrpcService.write(request, responseObserver);

        ArgumentCaptor<WriteResponse> responseCaptor = ArgumentCaptor.forClass(WriteResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        WriteResponse response = responseCaptor.getValue();
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("ZK connection error");
    }
}

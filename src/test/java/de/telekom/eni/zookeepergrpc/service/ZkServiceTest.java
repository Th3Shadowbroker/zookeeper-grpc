package de.telekom.eni.zookeepergrpc.service;

import com.google.protobuf.ByteString;
import de.telekom.eni.zookeepergrpc.proto.WriteRequest;
import de.telekom.eni.zookeepergrpc.proto.WriteResponse;
import io.grpc.stub.StreamObserver;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModePathAndBytesable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ZkServiceTest {

    private CuratorFramework curator;
    private ZkService zkService;

    @BeforeEach
    void setUp() {
        curator = mock(CuratorFramework.class);
        zkService = new ZkService(curator);
    }

    @Test
    void testWriteSuccess() throws Exception {
        CreateBuilder createBuilder = mock(CreateBuilder.class, RETURNS_DEEP_STUBS);
        when(curator.create()).thenReturn(createBuilder);

        var payload = "same payload";
        WriteRequest request = WriteRequest.newBuilder()
                .setZnode("/test/node")
                .setData(payload)
                .build();

        StreamObserver<WriteResponse> responseObserver = mock(StreamObserver.class);

        zkService.write(request, responseObserver);

        verify(createBuilder.orSetData()).forPath("/test/node", payload.getBytes());

        ArgumentCaptor<WriteResponse> responseCaptor = ArgumentCaptor.forClass(WriteResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        WriteResponse response = responseCaptor.getValue();
        assertThat(response.getSuccess()).isTrue();
    }

    @Test
    void testWriteError() throws Exception {
        CreateBuilder createBuilder = mock(CreateBuilder.class, RETURNS_DEEP_STUBS);
        when(curator.create()).thenReturn(createBuilder);
        when(createBuilder.orSetData().forPath(anyString(), any(byte[].class)))
                .thenThrow(new RuntimeException("ZK connection error"));

        WriteRequest request = WriteRequest.newBuilder()
                .setZnode("/test/error-node")
                .setData("fail")
                .build();

        StreamObserver<WriteResponse> responseObserver = mock(StreamObserver.class);

        zkService.write(request, responseObserver);

        ArgumentCaptor<WriteResponse> responseCaptor = ArgumentCaptor.forClass(WriteResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        WriteResponse response = responseCaptor.getValue();
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("ZK connection error");
    }
}

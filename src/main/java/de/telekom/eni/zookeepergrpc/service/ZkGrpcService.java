package de.telekom.eni.zookeepergrpc.service;

import de.telekom.eni.zookeepergrpc.proto.WriteRequest;
import de.telekom.eni.zookeepergrpc.proto.WriteResponse;
import de.telekom.eni.zookeepergrpc.proto.ZookeeperServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.DependsOn;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@ConditionalOnExpression("${grpc.enabled}")
@DependsOn("curatorClient")
@Slf4j
public class ZkGrpcService extends ZookeeperServiceGrpc.ZookeeperServiceImplBase {

    private final ZkService zkService;

    public ZkGrpcService(ZkService zkService) {
        this.zkService = zkService;
    }

    @Override
    public void write(WriteRequest request, StreamObserver<WriteResponse> responseObserver) {
        var zNode = request.getZnode();
        var data = request.getData();

        try {
            zkService.write(zNode, data);
            log.info("Successfully updated z-node {}: {}", zNode, data);

            var reply = WriteResponse
                    .newBuilder()
                    .setSuccess(true)
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error while writing to zookeeper", e);
            var reply = WriteResponse
                    .newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

}

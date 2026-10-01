package de.telekom.eni.zookeepergrpc.service;

import de.telekom.eni.zookeepergrpc.proto.WriteRequest;
import de.telekom.eni.zookeepergrpc.proto.WriteResponse;
import de.telekom.eni.zookeepergrpc.proto.ZookeeperServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class ZkService extends ZookeeperServiceGrpc.ZookeeperServiceImplBase {

    private final CuratorFramework curator;

    public ZkService(CuratorFramework curator) {
        this.curator = curator;
    }

    @Override
    public void write(WriteRequest request, StreamObserver<WriteResponse> responseObserver) {
        var zNode = request.getZnode();
        var data = request.getData();

        try {
            curator.create().orSetData().forPath(zNode, data.getBytes());
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

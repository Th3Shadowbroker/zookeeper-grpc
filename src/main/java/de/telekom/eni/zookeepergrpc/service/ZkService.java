package de.telekom.eni.zookeepergrpc.service;

import de.telekom.eni.zookeepergrpc.proto.WriteRequest;
import de.telekom.eni.zookeepergrpc.proto.WriteResponse;
import de.telekom.eni.zookeepergrpc.proto.ZookeeperServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class ZkService extends ZookeeperServiceGrpc.ZookeeperServiceImplBase {

    @Override
    public void write(WriteRequest request, StreamObserver<WriteResponse> responseObserver) {

    }

}

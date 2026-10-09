package de.telekom.eni.zookeepergrpc.service;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

@Service
public class ZkService {

    private final CuratorFramework curator;

    public ZkService(CuratorFramework curator) {
        this.curator = curator;
    }

    public void write(String zNode, String value) throws Exception {
        curator.create().orSetData().creatingParentsIfNeeded().forPath(zNode, value.getBytes());
    }

}

package com.test;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ZookeeperWatchMore {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperWatchMore.class);

    private static final String CONNECT_URL ="192.168.0.101:2181";

    private static final String PATH = "/zm-v8";

    private String newValue = "";
    private String oldValue = "";

    public ZooKeeper startZk(){
        try {
            return new ZooKeeper(CONNECT_URL, 1000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    String path = watchedEvent.getPath();

                    System.out.println(path);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createNode(ZooKeeper zk,String path,String data) throws KeeperException,InterruptedException {

        zk.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }


    public String getNode(ZooKeeper zk,String path) throws KeeperException,InterruptedException{

        byte[] result = zk.getData(path, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                getTriggerValues(zk,path);
            }
        }, new Stat());

        oldValue = new String(result);

        return new String(result);
    }

    private void getTriggerValues(ZooKeeper zk,String path){
        try {
            byte[] result = zk.getData(path, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    getTriggerValues(zk,path);
                }
            }, new Stat());

            newValue = new String(result);

            if(newValue.equals(oldValue)){
                System.out.println("zzzzzzzzzzzzzzzzzzzz:no change");
            }else {
                System.out.println("zzzzzzzzzzzzzzzzzzzz:oldvalue:"+oldValue+"newVaule:"+newValue);
                oldValue = newValue;
            }

            System.out.println("zzzzzzzzzzzzzzzzzzzz:"+new String(result));
        }catch (Exception e){
            logger.error("zzzzzzzzzzzzzzzz:error");
        }
    }

    public static void main(String[] args) throws InterruptedException,KeeperException{
        ZookeeperWatchMore zookeeperWatchOne = new ZookeeperWatchMore();

        ZooKeeper zk = zookeeperWatchOne.startZk();

        if(zk.exists(PATH,false) == null){
            zookeeperWatchOne.createNode(zk,PATH,"zhangmeng");

            String result = zookeeperWatchOne.getNode(zk,PATH);

            System.out.printf("result:"+result);

        }else {
            System.out.println("this node is exist!");
        }

        Thread.sleep(Long.MAX_VALUE);
    }
}

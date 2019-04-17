package com.test;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class Application {

    private static final String CONNECT_URL ="192.168.0.101:2181";

    private static final String PATH = "/zm-v4";

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

    public void stopZk(ZooKeeper zk) throws InterruptedException{

        if(zk != null){
            zk.close();
        }
    }

    public void createNode(ZooKeeper zk,String path,String data) throws KeeperException,InterruptedException {

        zk.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public String getNode(ZooKeeper zk,String path) throws KeeperException,InterruptedException{

        byte[] result = zk.getData(path,false,new Stat());

        return result.toString();
    }

    public static void main(String[] args) throws InterruptedException,KeeperException{
        Application application = new Application();

        ZooKeeper zooKeeper = application.startZk();

        if(zooKeeper.exists(PATH,false) == null){
            application.createNode(zooKeeper,PATH,"zhangmeng");

            String result = application.getNode(zooKeeper,PATH);

            System.out.printf("result:"+result);
        }else {
            System.out.println("this node is exist!");
        }


    }
}

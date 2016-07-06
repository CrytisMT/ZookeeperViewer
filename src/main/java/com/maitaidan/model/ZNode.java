package com.maitaidan.model;

import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by Crytis on 2016/6/30.
 * Just test.
 */
public class ZNode {
    private String path;
    private String nodeData;
    private List<ACL> acls;
    private Stat stat;


    public ZNode(String path, String nodeData, List<ACL> acls,Stat stat) {
        this.acls = acls;
        this.nodeData = nodeData;
        this.path = path;
        this.stat = stat;
    }

    public ZNode(String path, byte[] nodeData, List<ACL> acls,Stat stat) {
        this.acls = acls;
        this.nodeData = new String(nodeData);
        this.path = path;
        this.stat = stat;

    }

    public List<ACL> getAcls() {
        return acls;
    }

    public void setAcls(List<ACL> acls) {
        this.acls = acls;
    }

    public String getNodeData() {
        return nodeData;
    }

    public void setNodeData(String data) {
        nodeData = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }
}

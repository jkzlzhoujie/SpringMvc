package com.example.demo.filter;

import org.springframework.stereotype.Service;

import java.util.concurrent.CopyOnWriteArrayList;

@Service("roleCache")
public class RoleCache {
    private static CopyOnWriteArrayList<String> resourceList = new CopyOnWriteArrayList<>();

    public RoleCache() throws Exception {
    }

    public boolean contains(String url) {
        return resourceList.contains(url);
    }

    public void addRes(String res) {
        synchronized (resourceList) {
            resourceList.add(res);
        }
    }

    public void removeRes(String res) {
        synchronized (resourceList) {
            resourceList.remove(res);
        }
    }

    public void loadRes(CopyOnWriteArrayList<String> resList) {
        resourceList = resList;
    }
}

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.access;

import ish.oncourse.configuration.Configuration;
import ish.util.SecurityUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ZKSessionManager implements  ISessionManager {

 
    private static final int zkClientTimeOut = 20000;

    private static Logger logger = LogManager.getLogger();

    /**
     * root portal node
     */
    private static final String PORTAL_NODE = "%s/willow/sessions";
    private static final String CONTACT_NODE = "/%s";
    private static final String SESSION_NODE = "/%s/%s";
    private static final String CHILD_NODE = "/%s/%s/%s";

    private static final String RECURSIVELY_NODE = "%s/%s";
    
    private ZooKeeper zooKeeper;
    private boolean dead = false;

    public ZKSessionManager() {
        zooKeeper = createZk();
    }
    
    private ZooKeeper getZk() throws InterruptedException {
        if (dead) {
            zooKeeper.close();
            zooKeeper = createZk();
            dead = false;
        } 
        return zooKeeper;
    }
    
    private ZooKeeper createZk(){
        String zkHost = System.getProperty(Configuration.ZK_HOST_PROPERTY);

        if (zkHost == null) {
            throw new IllegalStateException("Zookeeper host property undefined");
        }
        try {
            return new ZooKeeper(String.format(PORTAL_NODE, zkHost), zkClientTimeOut, event -> {
                switch (event.getState()) {
                    case Expired:
                    case Disconnected:
                        dead = true; 
                        break;
                    default:
                        break;
                }
            });
        } catch (IOException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }
    }


    public Long getSelectedChildId(String sessionId, String contactId) {
        try {
            String path = String.format(SESSION_NODE, contactId, sessionId);
            List<String> children = getZk().getChildren(path, false);
            if (children.size() == 1) {
                return Long.valueOf(children.get(0));
            } else if (children.size() > 1) {
                //remove invalid session
                remove(path);               
            }
            return null;
        } catch (KeeperException | InterruptedException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }
    }

    public void selectChild(String sessionId, String contactId, String newChildId) {

        try {
            Long oldChildId = getSelectedChildId(sessionId, contactId);
            if (oldChildId != null) {
                remove(String.format(CHILD_NODE, contactId, sessionId, oldChildId));
            }
            getZk().create(String.format(CHILD_NODE, contactId, sessionId, newChildId), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }
        
    }
    
    public String createContactSession(String contactId) {
        String sessionId = SecurityUtil.generateRandomPassword(20);
        try {
            if (getZk().exists(String.format(CONTACT_NODE, contactId), false) == null) {
                getZk().create(String.format(CONTACT_NODE, contactId), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            getZk().create(String.format(SESSION_NODE, contactId, sessionId), SerializationUtils.serialize(LocalDateTime.now()), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            
        } catch (KeeperException | InterruptedException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        } 
        return sessionId;
    }

    public boolean validSession(String contactId, String sessionId) {
        try {
            String path = String.format(SESSION_NODE, contactId, sessionId);
            return getZk().exists(path, false) != null;
        } catch (KeeperException | InterruptedException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }
    }
    
    private void remove(String path) throws KeeperException, InterruptedException {
        List<String> children = getZk().getChildren(path, false);

        for (String child : children) {
            remove(String.format(RECURSIVELY_NODE, path, child));

        }
        getZk().delete(path, -1);
    }
}

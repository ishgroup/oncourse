/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.access;

import ish.oncourse.configuration.Configuration;
import ish.util.SecurityUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ZKService implements  IZKService {

 
    private static final int zkClientTimeOut = 20000;
    /**
     * 4  hours timeout for logged user
     */
    private static final int portalSessionTimeOut = 14400000;

    private static Logger logger = LogManager.getLogger();

    /**
     * root portal node
     */
    private static final String PORTAL_NODE = "%s/portal/sessions";
    private static final String SESSION_NODE = "/%s";
    private static final String CONTACT_NODE = "/%s/%d";
    private static final String SELECTED_CHILD_NODE = "/%s/%d/%d";

    private static final String RECURSIVELY_NODE = "%s/%s";
    
    private ZooKeeper zooKeeper;
    private boolean dead = false;

    public ZKService() {
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


    public Long getSelectedChildId(String sessionId, Long contactId) {
        try {
            String path = String.format(CONTACT_NODE, sessionId, contactId);
            List<String> children = getZk().getChildren(path, false);
            if (children.size() == 1) {
                //modify to keep node alive
                Long childId = Long.valueOf(children.get(0));
                getZk().setData(String.format(SELECTED_CHILD_NODE, sessionId, contactId, childId), new byte[0], -1);
                return Long.valueOf(children.get(0));
            } else if (children.size() > 1) {
                destroySession(sessionId);               
            }
            return null;
        } catch (KeeperException | InterruptedException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }
    }

    public void selectChild(String sessionId, Long contactId, Long childId) {

        try {
            Long selectedChildId = getSelectedChildId(sessionId, contactId);
            if (selectedChildId != null) {
                remove(String.format(SELECTED_CHILD_NODE, sessionId, contactId, selectedChildId));
            }
            byte[] timestamp = SerializationUtils.serialize(LocalDateTime.now());
            getZk().create(String.format(SELECTED_CHILD_NODE, sessionId, contactId, childId), timestamp, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }
        
    }
    
    public String createContactSession(Long contactId) {
        String sessionId = SecurityUtil.generateRandomPassword(20);
        try {
            byte[] timestamp = SerializationUtils.serialize(LocalDateTime.now());
            getZk().create(String.format(SESSION_NODE, sessionId), timestamp, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            getZk().create(String.format(CONTACT_NODE, sessionId, contactId), timestamp, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        } 
        return sessionId;
    }

    public Long getContactId(String sessionId) {
        List<String> children;
        try {
            String path = String.format(SESSION_NODE, sessionId);
            if (getZk().exists(path, false) != null) {
                children = getZk().getChildren(path, false);
                if (children != null && children.size() == 1) {
                    //modify to keep node alive
                    Long contactId = Long.valueOf(children.get(0));
                    getZk().setData(String.format(CONTACT_NODE, sessionId, contactId), new byte[0], -1);
                    return contactId;
                } else {
                    remove(path);
                    return null;
                }
            } else {
                return null;
            }
        } catch (KeeperException | InterruptedException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }

       
    }

    public void destroySession(String sessionId) {
        if (sessionId != null) {
            try {
                String path = String.format(SESSION_NODE, sessionId);
                if (getZk().exists(path, false) != null) {
                    remove(path);
                }
            } catch (KeeperException | InterruptedException e) {
                logger.catching(e);
                throw new RuntimeException(e);
            }
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

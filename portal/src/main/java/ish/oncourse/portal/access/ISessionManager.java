/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.access;

public interface ISessionManager {

    String createContactSession(String contactId);
    
    boolean validSession(String contactId, String sessionId);
    
    Long getSelectedChildId(String contactId, String sessionId);
    
    void selectChild(String contactId, String sessionId, String newChildId);
}

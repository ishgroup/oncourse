/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.access;

public interface IZKService {

    String createContactSession(Long contactId);
    
    Long getContactId(String sessionId);
    
    Long getSelectedChildId(String sessionId, Long contactId);
    
    void selectChild(String sessionId, Long contactId, Long childId);

    void destroySession(String sessionId); 
}

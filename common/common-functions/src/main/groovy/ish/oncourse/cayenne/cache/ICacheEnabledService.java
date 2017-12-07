/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne.cache;

public interface ICacheEnabledService {
    
    boolean isCacheEnabled();
    void setCacheEnabled(Boolean enabled);

}

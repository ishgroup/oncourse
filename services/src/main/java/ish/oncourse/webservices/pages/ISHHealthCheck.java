/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.pages;


import static ish.oncourse.configuration.Configuration.API_VERSION;

public class ISHHealthCheck {
    
    public String getVersion() {
        return  System.getProperty(API_VERSION);
    }
}

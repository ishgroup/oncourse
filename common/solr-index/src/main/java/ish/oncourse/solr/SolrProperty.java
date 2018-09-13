/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import ish.oncourse.configuration.IProperty;

public enum SolrProperty implements IProperty {

    WEBAPP_LOCATION("webApp_location", "web.app.location");


    private String key;
    private String systemProperty;

    private SolrProperty(String key, String systemProperty) {
        this.key = key;
        this.systemProperty = systemProperty;
    }

    public String getKey() {
        return key;
    }

    public String getSystemProperty() {
        return systemProperty;
    }

}
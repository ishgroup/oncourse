/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services;

import ish.oncourse.configuration.IProperty;

public enum WebProperty implements IProperty {
    
    EDITOR_VERSION("editor_version", "editor.version"),
    CHECKOUT_VERSION("checkout_version", "checkout.version");
    
    private String key;
    private String systemProperty;

    WebProperty(String key, String systemProperty) {
        this.key = key;
        this.systemProperty = systemProperty;
    }


    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getSystemProperty() {
        return systemProperty;
    }
}

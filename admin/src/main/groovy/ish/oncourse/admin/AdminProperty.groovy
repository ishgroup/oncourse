package ish.oncourse.admin

import ish.oncourse.configuration.IProperty

enum AdminProperty implements IProperty {

    S_ROOT('admin_files', 's.root')

    private String key
    private String systemProperty

    private AdminProperty(String key, String systemProperty) {
        this.key = key
        this.systemProperty = systemProperty
    }

    String getKey() {
        key
    }

    String getSystemProperty() {
        systemProperty
    }
    }

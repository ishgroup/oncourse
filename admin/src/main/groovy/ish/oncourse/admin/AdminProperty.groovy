package ish.oncourse.admin

import ish.oncourse.configuration.IProperty

enum AdminProperty implements IProperty {

    S_ROOT('editor_files', 's.root'),
    STORAGE_ACCESS_ID('storage_access_id', 'storage.access.id'),
    STORAGE_ACCESS_KEY('storage_access_key', 'storage.access.key')
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

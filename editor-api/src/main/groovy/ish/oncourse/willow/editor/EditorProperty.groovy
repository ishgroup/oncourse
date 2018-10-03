package ish.oncourse.willow.editor

import ish.oncourse.configuration.IProperty

enum EditorProperty implements IProperty {
    EDIT_SCRIPT_PATH('editor_edit', 'editor.script.edit'),
    DEPLOY_SCRIPT_PATH('editor_deploy', 'editor.script.deploy'),
    S_ROOT('editor_files', 's.root'),
    SERVICES_LOCATION('services_location', 'services.app.location')


    private String key
    private String systemProperty

    private EditorProperty(String key, String systemProperty) {
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
package ish.oncourse.configuration

trait IProperty {
    
    String key
    String systemProperty

    String getKey() {
        key
    }

    String getSystemProperty() {
        systemProperty
    }

    boolean init(Properties props) {
        if (props.get(key)) {
            System.setProperty(systemProperty, props.get(key) as String)
            return true
        }
        return false
    }
    
    String getValue() {
        System.getProperty(systemProperty)
    }
}
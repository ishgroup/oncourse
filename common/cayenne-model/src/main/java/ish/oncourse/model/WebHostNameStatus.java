package ish.oncourse.model;

import org.apache.cayenne.ExtendedEnumeration;

public enum WebHostNameStatus implements ExtendedEnumeration {
    
    NOT_VERIFIED(0),
    ACTIVE(1),
    PRIMARY(2);
    
    
    private Integer value;
    
    WebHostNameStatus(Integer value) {
        this.value = value;
    }
    
    @Override
    public Object getDatabaseValue() {
        return this.value;
    }
}

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration

enum YesNoOptions  implements DisplayableExtendedEnumeration<Integer> {
    
    NOT_DEFINED(0,'Not stated', null),
    NO(1,"Yes", Boolean.TRUE),
    YES(2,"No", Boolean.FALSE)


    private Integer value
    private Boolean booleanValue 
    private String displayName

    private YesNoOptions(Integer value, String displayName, Boolean booleanValue) {
        this.value = value
        this.displayName = displayName
        this.booleanValue = booleanValue
    }
    
    @Override
    String getDisplayName() {
        return displayName
    }

    @Override
    Integer getDatabaseValue() {
        return value
    }

    Boolean getBooleanValue() {
        return booleanValue 
    }
}

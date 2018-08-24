package ish.oncourse.specialpages;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

public enum RequestMatchType implements DisplayableExtendedEnumeration<Integer> {

    @API
    EXACT(1),

    @API
    STARTS_WITH(2);

    private int value;

    RequestMatchType(int value) {
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return this.getDisplayName();
    }

    @Override
    public Integer getDatabaseValue() {
        return this.value;
    }
}

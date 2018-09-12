package ish.oncourse.specialpages;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Request path handling policy
 */
public enum RequestMatchType implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Request path should be identical to pattern
     *
     * Database value: 1
     */
    @API
    EXACT(1),

    /**
     * Request path should be starts with pattern
     *
     * Database value: 2
     */
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

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

enum RequestMatchType implements DisplayableExtendedEnumeration<Integer> {

    @API
    EXACT(1),

    @API
    STARTS_WITH(2)

    private int value

    RequestMatchType(int value) {
        this.value = value
    }

    @Override
    String getDisplayName() {
        return this.displayName
    }

    @Override
    Integer getDatabaseValue() {
        return this.value
    }
}

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

@API
enum Gender implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 0
     */
    @API
    FEMALE(0, "Female"),

    /**
     * Database value: 1
     */
    @API
    MALE(1, "Male"),

    /**
     * Database value: 2
     */
    @API
    OTHER_GENDER(2, "Other")


    private String displayName
    private int value

    private Gender(int value, String displayName) {
        this.value = value
        this.displayName = displayName
    }

    @Override
    Integer getDatabaseValue() {
        return this.value
    }

    @Override
    String getDisplayName() {
        return this.displayName
    }

    @Override
    String toString() {
        return getDisplayName()
    }
}
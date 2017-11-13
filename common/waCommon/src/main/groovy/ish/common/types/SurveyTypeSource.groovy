package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

@API
enum SurveyTypeSource implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 1
     */
    @API
    ONCOURSE(1, 'onCourse'),

    /**
     * Database value: 2
     */
    @API
    INTEGRATION(2, 'Integration'),

    /**
     * Database value: 10
     */
    @API
    OTHER(10, 'Other')

    private String displayName
    private int value

    private SurveyTypeSource(int value, String displayName) {
        this.value = value
        this.displayName = displayName
    }

    @Override
    String getDisplayName() {
        return displayName
    }

    @Override
    Integer getDatabaseValue() {
        return value
    }
}
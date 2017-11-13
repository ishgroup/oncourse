package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

@API
enum DeliverySchedule implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 1
     */
    @API
    ON_ENROL(1, 'On enrol'),
    
    /**
     * Database value: 2
     */
    @API
    ON_START(2, 'On start'),

    /**
     * Database value: 3
     */
    @API
    MIDWAY(3, 'Midway'),

    /**
     * Database value: 4
     */
    @API
    AT_COMPLETION(4, 'At completion'),

    /**
     * Database value: 5
     */
    @API
    POST_COMPLETION(5, 'Post completion')

    private String displayName
    private int value

    private DeliverySchedule(int value, String displayName) {
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
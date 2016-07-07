package ish.oncourse.portal.services.payment

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * User: akoiro
 * Date: 2/07/2016
 */
enum Action {
    init,
    make,
    update


    @JsonCreator
    public static Action forValue(String value) {
        return valueOf(value)
    }

    @JsonValue
    public String toValue() {
        return name()
    }
}

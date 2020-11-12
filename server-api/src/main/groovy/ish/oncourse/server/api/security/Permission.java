/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    /**
     * String definition of permission mask. All masks defined in {@code Mask}. By default used Mask.NONE.
     * @return mask of permission
     */
    String mask() default "none";

    /**
     * String definition of permission key code. All key codes defined in {@code KeyCode}.
     * @return key code of permission
     */
    String keyCode();

    /**
     * Error message which will packed in response. Not required.
     * @return error message of permission
     */
    String errorMessage() default "";

    String licenseCode() default "";

    String licenseErrorMessage() default "";

    String chain() default "";

    String custom() default "";
}

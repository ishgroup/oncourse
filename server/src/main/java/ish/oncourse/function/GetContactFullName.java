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

package ish.oncourse.function;

import ish.oncourse.server.cayenne.Contact;
import ish.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

public class GetContactFullName {

    private String firstName;
    private String middleName;
    private String lastName;
    private boolean isCompany;
    private boolean firstNameFirst;

    private GetContactFullName() {

    }

    public static GetContactFullName valueOf(Contact contact) {
        return valueOf(contact, true);
    }

    public static GetContactFullName valueOf(Contact contact, boolean firstNameFirst) {
        return valueOf(contact.getFirstName(), contact.getMiddleName(), contact.getLastName(), contact.getIsCompany(), firstNameFirst);
    }

    public static GetContactFullName valueOf(String firstName, String middleName, String lastName, boolean isCompany, boolean firstNameFirst) {
        GetContactFullName function = new GetContactFullName();
        function.firstName = firstName;
        function.middleName = middleName;
        function.lastName = lastName;
        function.isCompany = isCompany;
        function.firstNameFirst = firstNameFirst;
        return function;
    }

    public String get() {
        if (isCompany || StringUtils.equals(firstName, lastName)) {
            return StringUtils.trimToEmpty(lastName);
        }

        StringBuilder builder = new StringBuilder();

        if (firstNameFirst) {
            if (StringUtils.isNotBlank(firstName)) { builder.append(firstName); }
            if (StringUtils.isNotBlank(middleName)) { builder.append(StringUtils.SPACE).append(middleName); }
            if (StringUtils.isNotBlank(lastName)) { builder.append(StringUtils.SPACE).append(lastName); }
        } else {
            if (StringUtils.isNotBlank(lastName)) { builder.append(lastName); }
            if (StringUtils.isNotBlank(firstName)) { builder.append(StringUtil.COMMA_CHARACTER).append(StringUtils.SPACE).append(firstName); }
            if (StringUtils.isNotBlank(middleName)) { builder.append(StringUtils.SPACE).append(middleName); }
        }

        return builder.toString();
    }
}

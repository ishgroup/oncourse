/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server;

import ish.math.Country;

public interface IPreferenceController {

    String getOncourseServerDefaultTimezone();

    String getEmailFromAddress();

    String getEmailFromName();

    boolean getReplicationEnabled();

    Long getDefaultAccountId(String preferenceName);

    void setDefaultAccountId(String preferenceName, Long value);

    Object getValueForKey(String key);

    void setValueForKey(String key, Object value);

    boolean hasSurveyForm();
}

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

package ish.oncourse.server.cayenne

import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._FieldConfigurationScheme

import javax.annotation.Nonnull
import java.util.ArrayList
import java.util.Date
import java.util.List

/**
 * Data collection rules {@link ish.oncourse.cayenne.FieldConfigurationScheme} comprise several FieldConfigurations:<br>
 *
 * 1. waiting list<br>
 * 2. enrolment<br>
 * 3. application<br>
 * 4. survey<br>
 *
 * A {@link ish.oncourse.cayenne.FieldConfiguration} can be reused as part of several different schemes. Although called
 * {@link ish.oncourse.cayenne.FieldConfigurationScheme} here in the API, the user interface refers only to Data Collection Rules.
 *
 * Each course is linked to a single scheme which then determines how forms are displayed to a student.
 *
 * FieldConfiguration contains both FieldHeading and Field in a sorted order.
 */
@QueueableEntity @API
class FieldConfigurationScheme extends _FieldConfigurationScheme implements Queueable {


    /**
	 * @return the date and time this record was created
	 */
	@Nonnull @Override @API
    Date getCreatedOn() {
		return super.getCreatedOn()
    }

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull @Override @API
    Date getModifiedOn() {
		return super.getModifiedOn()
    }

	/**
	 * This is displayed in the UI when attaching a course to a scheme but never shown to students.
	 *
	 * @return The name of the scheme.
	 */
	@Nonnull @Override @API
    String getName() {
		return super.getName()
    }

	/**
	 * @return a list of all courses attached. Careful, this could be a long list and isn't paginated.
	 */
	@Nonnull @Override @API
    List<Course> getCourses() {
		return super.getCourses()
    }


	/**
	 * @return All the field configurations used in this scheme. Typically there will be one of each of the four types.
	 */
	List<FieldConfiguration> getConfigurations() {
        List<FieldConfiguration> result = new ArrayList<>()
        for (FieldConfigurationLink link : getFieldConfigurationLinks()) {
			result.add(link.getFieldConfiguration())
        }
        return result
    }
}

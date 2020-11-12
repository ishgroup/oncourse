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
import ish.oncourse.server.cayenne.glue._FieldHeading

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.util.Date
import java.util.List

/**
 * Data collection rules {@link FieldConfigurationScheme} have zero or more headings under which
 * the Fields are grouped.
 */
@QueueableEntity
@API
class FieldHeading extends _FieldHeading implements Queueable, Comparable<FieldHeading> {



    /**
     * @return the date and time this record was created
     */
    @Nonnull @Override @API
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * The description is usually shown in the UI under the name, within some sort of border or panel
     *
     * @return a description: it might be several paragraphs
     */
    @Nullable @Override @API
    String getDescription() {
        return super.getDescription()
    }

    /**
     * @return the date and time this record was modified
     */
    @Nonnull @Override @API
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * FieldHeadings are grouped into FieldConfigurations
     *
     * @return
     */
    @Nonnull @Override @API
    FieldConfiguration getFieldConfiguration() {
        return super.getFieldConfiguration()
    }

    /**
     * @return a sorted list of the fields under this heading
     */
    @Nonnull @Override @API
    List<Field> getFields() {
        return super.getFields()
    }

    /**
     * @return the name of the heading is usually shown in a large font in the UI
     */
    @Nonnull @Override @API
    String getName() {
        return super.getName()
    }

    @Override
    int compareTo(FieldHeading o) {
        return getFieldOrder() - o.getFieldOrder()
    }
}




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

import ish.common.types.DataType
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._PortalWebsite
import org.apache.cayenne.query.ObjectSelect

@QueueableEntity
class PortalWebsite extends _PortalWebsite implements Queueable {

    @Override
    boolean isAsyncReplicationAllowed() {
        return false
    }

    @Override
    protected void preRemove() {
        def customFields = ObjectSelect.query(CustomField)
                .where(CustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.DATA_TYPE).eq(DataType.PORTAL_SUBDOMAIN)
                        .andExp(CustomField.VALUE.eq(this.subDomain)))
                .select(context)

        if (!customFields.empty)
            context.deleteObjects(customFields)
    }
}

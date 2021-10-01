/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.server.cayenne.glue._LeadNoteRelation

class LeadNoteRelation extends _LeadNoteRelation {

    @Override
    String getEntityIdentifier() {
        return Lead.class.getSimpleName()
    }

    @Override
    void setNotableEntity(NotableTrait entity) {
        super.setNotedLead((Lead) entity)
    }

    @Override
    NotableTrait getNotableEntity() {
        return super.getNotedLead()
    }
}

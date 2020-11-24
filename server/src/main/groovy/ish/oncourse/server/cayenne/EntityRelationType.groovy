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

import ish.common.types.EntityRelationCartAction
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._EntityRelationType

import javax.annotation.Nonnull

@API
//@QueueableEntity
class EntityRelationType extends _EntityRelationType implements Queueable {

    final static Long DEFAULT_SYSTEM_TYPE_ID = -1l

    /**
     * @return the date and time this record was created
     */
    @API
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * @return the date and time this record was modified
     */
    @API
    @Override
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * @return the name of relation type
     */
    @Nonnull
    @API
    @Override
    String getName() {
        return super.getName()
    }

    /**
     * @return the type name of entity record on the right side of the relation
     */
    @Nonnull
    @API
    @Override
    String getToName() {
        return super.getToName()
    }

    /**
     * @return the type name of entity record on the left side of the relation
     */
    @Nonnull
    @API
    @Override
    String getFromName() {
        return super.getFromName()
    }

    /**
     * @return the text description of this relation type
     */
    @API
    @Override
    String getDescription() {
        return super.getDescription()
    }

    /**
     * @return true if relation type is shown on web
     */
    @API
    @Override
    Boolean getIsShownOnWeb() {
        return super.getIsShownOnWeb()
    }

    /**
     * @return true if need to consider history
     */
    @API
    @Override
    Boolean getConsiderHistory() {
        return super.getConsiderHistory()
    }

    /**
     * @see ish.common.types.EntityRelationCartAction
     * @return the cart action which should be done
     */
    @API
    @Override
    EntityRelationCartAction getShoppingCart() {
        return super.getShoppingCart()
    }

    /**
     * @return the discount which is applied if item from left side of the relation would be selected
     */
    @API
    Discount getDiscount() {
        return super.getEntityRelationTypeDiscount()
    }

    /**
     * @return the list of relationships
     */
    @Nonnull
    @API
    @Override
    List<EntityRelation> getEntityRelations() {
        return super.getEntityRelations()
    }




}

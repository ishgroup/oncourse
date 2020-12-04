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
import javax.annotation.Nullable

@API
@QueueableEntity
class EntityRelationType extends _EntityRelationType implements Queueable {

    final static Long DEFAULT_SYSTEM_TYPE_ID = -1l

    /**
     * @return the date and time this record was created
     */
    @Nonnull @API @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * @return the date and time this record was modified
     */
    @Nonnull @API @Override
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * @return the name of relation type
     */
    @Nonnull @API @Override
    String getName() {
        return super.getName()
    }

    /**
     * @return the label name of record on the right side of the relation
     */
    @Nonnull @API @Override
    String getToName() {
        return super.getToName()
    }

    /**
     * @return the label name of record on the left side of the relation
     */
    @Nonnull @API @Override
    String getFromName() {
        return super.getFromName()
    }

    /**
     * @return the text description of this relation type
     */
    @Nullable @API @Override
    String getDescription() {
        return super.getDescription()
    }

    /**
     * @return true if relation type is shown on web
     */
    @Nonnull @API @Override
    Boolean getIsShownOnWeb() {
        return super.getIsShownOnWeb()
    }

    /**
     * When considering shopping cart actions, do we need to consider only the current shopping cart
     * or should be look at all of the user's history of purchases and enrolments?
     *
     * @return true if we should consider history
     */
    @Nonnull @API @Override
    Boolean getConsiderHistory() {
        return super.getConsiderHistory()
    }

    /**
     * During a checkout process, we might need to apply certain rules.
     *
     * @return the cart action which should be applied to the record on the right
     */
    @Nonnull @API @Override
    EntityRelationCartAction getShoppingCart() {
        return super.getShoppingCart()
    }

    /**
     * @return a discount to apply to the record on the right side of the relation
     */
    @Nullable @API
    Discount getDiscount() {
        return super.getEntityRelationTypeDiscount()
    }

    /**
     * @return the list of relationships
     */
    @Nonnull @API @Override
    List<EntityRelation> getEntityRelations() {
        return super.getEntityRelations()
    }

}

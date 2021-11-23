/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.cayenne.Taggable
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.cayenne.glue._ProductTagRelation

import javax.annotation.Nonnull

class ProductTagRelation extends _ProductTagRelation{
    /**
     * @see TagRelation#getTaggableClassesIdentifier()
     */
    @Nonnull
    @Override
    TaggableClasses getTaggableClassesIdentifier() {
        return TaggableClasses.PRODUCT
    }

    /**
     * @see TagRelation#getTaggedRelation()
     */
    @Nonnull
    @Override
    Taggable getTaggedRelation() {
        return super.getTaggedProduct()
    }

    /**
     * @see TagRelation#setTaggedRelation(Taggable)
     */
    @Override
    void setTaggedRelation(Taggable object) {
        super.setTaggedProduct((Product) object)
    }
}

/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.server.cayenne.glue._VoucherProductAttachmentRelation

import javax.annotation.Nonnull

class VoucherProductAttachmentRelation extends _VoucherProductAttachmentRelation{

    /**
     * @see AttachmentRelation#setAttachedRelation(AttachableTrait)
     */
    @Override
    void setAttachedRelation(AttachableTrait attachable) {
        super.setAttachedVoucherProduct((VoucherProduct) attachable)
    }

    /**
     * @see AttachmentRelation#getAttachedRelation()
     */
    @Override
    AttachableTrait getAttachedRelation() {
        return super.getAttachedVoucherProduct()
    }

    /**
     * @see AttachmentRelation#getEntityIdentifier()
     */
    @Nonnull
    @Override
    String getEntityIdentifier() {
        return VoucherProduct.class.getSimpleName()
    }
}

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

import ish.common.types.ConfirmationStatus
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.cayenne.Taggable
import ish.oncourse.server.cayenne.glue._Article

import javax.annotation.Nonnull

/**
 * Represents an instance of generic product which has been sold through onCourse.
 */
@API
@QueueableEntity
class Article extends _Article implements ExpandableTrait, AttachableTrait {


    @Override
    Class<? extends CustomField> getCustomFieldClass() {
        return ArticleCustomField
    }

    @Override
    void addToAttachmentRelations(AttachmentRelation relation) {
        super.addToAttachmentRelations(relation as ArticleAttachmentRelation)
    }

    @Override
    void removeFromAttachmentRelations(AttachmentRelation relation) {
        super.removeFromAttachmentRelations(relation as ArticleAttachmentRelation)
    }

    @Override
    Class<? extends AttachmentRelation> getRelationClass() {
        return ArticleAttachmentRelation
    }

    /**
     * @return confirmation email sending status: not sent, sent or suppressed from sending
     */
    @Nonnull
    @API
    @Override
    ConfirmationStatus getConfirmationStatus() {
        return super.getConfirmationStatus()
    }

    /**
     * @return the date and time this record was created
     */
    @API
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * @return date when product item (e.g. membership) expires
     */
    @API
    @Override
    Date getExpiryDate() {
        return super.getExpiryDate()
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
     * @return current status of this product item: active, cancelled, credited, redeemed, expired or delivered
     */
    @API
    @Override
    ProductStatus getStatus() {
        return super.getStatus()
    }

    /**
     *
     * @return dollar value paid for product item
     */
    Money getPurchasePrice(){
        return super.getPurchasePrice()
    }



    /**
     * @return contact who owns this product item
     */
    @Nonnull
    @API
    @Override
    Contact getContact() {
        return super.getContact()
    }

    /**
     * @return purchase invoice line for this product item
     */
    @Nonnull
    @API
    @Override
    InvoiceLine getInvoiceLine() {
        return super.getInvoiceLine()
    }

    /**
     * @return product type of this item
     */
    @Nonnull
    @API
    @Override
    Product getProduct() {
        return super.getProduct()
    }
}

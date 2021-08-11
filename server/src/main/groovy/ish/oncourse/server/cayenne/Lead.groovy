/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.server.cayenne.glue._Lead

class Lead extends _Lead implements AttachableTrait, NotableTrait, ExpandableTrait, LeadTrait {

    @Override
    void addToAttachmentRelations(AttachmentRelation relation) {
        super.addToAttachmentRelations((LeadAttachmentRelation) relation)
    }

    @Override
    void removeFromAttachmentRelations(AttachmentRelation relation) {
        super.removeFromAttachmentRelations((LeadAttachmentRelation) relation)
    }

    @Override
    Class<? extends AttachmentRelation> getRelationClass() {
        return LeadAttachmentRelation.class
    }

    @Override
    Class<? extends CustomField> getCustomFieldClass() {
        return LeadCustomField.class
    }

    @Override
    List<Site> getSites() {
        return super.getSites()
    }

    @Override
    List<Course> getCourses() {
        return super.getCourses()
    }

    @Override
    List<Product> getProducts() {
        return super.getProducts()
    }

    List<Invoice> getInvoices() {
        return super.getInvoices()
    }

    List<Quote> getQuotes() {
        return super.getQuotes()
    }
}


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

package ish.oncourse.aql.model;

import ish.oncourse.server.cayenne.*;
import org.apache.cayenne.Persistent;

class PathAliasFactory {

    private final EntityFactory factory;

    PathAliasFactory(EntityFactory factory) {
        this.factory = factory;
    }

    /**
     * Must be in sync with the data for client here:
     *    /buildSrc/apidoc/src/main/resources/au/com/ish/queryLanguageModel/syntheticAttributes.xml
     */
    void init() {
        createAlias(AssessmentClass.class, "courseClass", "class", CourseClass.class);

        createAlias(ClassCost.class, "courseClass", "class", CourseClass.class);

        createAlias(Course.class, "courseClasses.isActive", "isActive");
        createAlias(Course.class, "courseClasses", "classes", CourseClass.class);

        createAlias(CourseClass.class, "invoiceLines.invoice", "invoices", Invoice.class);

        createAlias(Enrolment.class, "invoiceLines.invoice", "invoices", Invoice.class);
        createAlias(Enrolment.class, "courseClass", "class", CourseClass.class);

        createAlias(Invoice.class, "paymentOutLines.paymentOut", "paymentsOut", PaymentOut.class);
        createAlias(Invoice.class, "paymentInLines.paymentIn", "paymentsIn", PaymentIn.class);
        createAlias(Invoice.class, "invoiceLines.invoiceLineDiscounts.discount", "discounts", Discount.class);
        createAlias(Invoice.class, "invoiceLines.courseClass", "classes", CourseClass.class);
        createAlias(Invoice.class, "invoiceLines.enrolment", "enrolments", Enrolment.class);

        createAlias(PaymentIn.class, "paymentInLines.invoice", "invoices", Invoice.class);
        createAlias(PaymentOut.class, "paymentOutLines.invoice", "invoices", Invoice.class);

        createAlias(Room.class, "courseClasses", "classes", CourseClass.class);

        createAlias(Session.class, "courseClass", "class", CourseClass.class);

        createAlias(Application.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Assessment.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Contact.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Course.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Document.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Enrolment.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Faculty.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Invoice.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Lead.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Message.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Payslip.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Quote.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Report.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Room.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Site.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Student.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(Tutor.class, "taggingRelations.tag", "tags", Tag.class);
        createAlias(WaitingList.class, "taggingRelations.tag", "tags", Tag.class);

        createAlias(Application.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Assessment.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Contact.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Course.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Document.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Enrolment.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Invoice.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Lead.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Message.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Payslip.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Quote.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Report.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Room.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Site.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Student.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(Tutor.class, "taggingRelations.tag", "checklists", Tag.class);
        createAlias(WaitingList.class, "taggingRelations.tag", "checklists", Tag.class);
    }

    private void createAlias(Class<? extends Persistent> entityType,
                             String path, String alias) {
        createAlias(entityType, path, alias, null);
    }

    private void createAlias(Class<? extends Persistent> entityType,
                             String path, String alias, Class<? extends Persistent> nextEntity) {
        PathAliasDescriptor aliasDescriptor = new PathAliasDescriptor(factory, entityType, path, alias, nextEntity);
        factory.addSyntheticAttribute(entityType, aliasDescriptor);
    }
}
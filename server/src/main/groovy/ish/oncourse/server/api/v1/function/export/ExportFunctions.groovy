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

package ish.oncourse.server.api.v1.function.export

import ish.oncourse.aql.AqlService
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.server.api.v1.model.ColumnDTO
import ish.oncourse.server.api.v1.model.SortingDTO
import ish.oncourse.server.api.v1.model.TagGroupDTO
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.preference.DefaultUserPreference
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SortOrder

import static ish.oncourse.server.api.function.EntityFunctions.parseSearchQuery

class ExportFunctions {

    public static final String DEFAULT_TEMPLATE_PREFIX = 'ish.'
    public static final String TRAINEESHIP_CLASSES_ENTITY = 'traineeship'

    static final Map<String, List<String>> TRANSFORMATIONS_MAP = [
            (Account.simpleName.toLowerCase())           : [Account.simpleName, AccountTransaction.simpleName],
            (AccountTransaction.simpleName.toLowerCase()): [AccountTransaction.simpleName],
            (Application.simpleName.toLowerCase())       : [Application.simpleName],
            (ArticleProduct.simpleName.toLowerCase())    : [ArticleProduct.simpleName],
            (Audit.simpleName.toLowerCase())             : [Audit.simpleName],
            (Banking.simpleName.toLowerCase())           : [Banking.simpleName, PaymentInterface.simpleName],
            (Certificate.simpleName.toLowerCase())       : [Certificate.simpleName],
            (CourseClass.simpleName.toLowerCase())       : [CourseClass.simpleName,
                                                            Session.simpleName,
                                                            Student.simpleName,
                                                            ClassCost.simpleName,
                                                            CourseClassTutor.simpleName,
                                                            DiscountCourseClass.simpleName,
                                                            Outcome.simpleName,
                                                            Enrolment.simpleName,
                                                            TutorAttendance.simpleName],
            (Contact.simpleName.toLowerCase())           : [Contact.simpleName,
                                                            Student.simpleName,
                                                            Tutor.simpleName,
                                                            AccountTransaction.simpleName],
            (CorporatePass.simpleName.toLowerCase())     : [CorporatePass.simpleName],
            (Course.simpleName.toLowerCase())            : [Course.simpleName, Session.simpleName],
            (Enrolment.simpleName.toLowerCase())         : [Enrolment.simpleName],
            (Discount.simpleName.toLowerCase())          : [Discount.simpleName],
            (AbstractInvoice.simpleName.toLowerCase())   : [Invoice.simpleName, InvoiceLine.simpleName,
                                                            Quote.simpleName, QuoteLine.simpleName],
            (Message.simpleName.toLowerCase())           : [Message.simpleName],
            (PaymentIn.simpleName.toLowerCase())         : [PaymentIn.simpleName],
            (PaymentOut.simpleName.toLowerCase())        : [PaymentOut.simpleName],
            (Module.simpleName.toLowerCase())            : [Module.simpleName],
            (Payslip.simpleName.toLowerCase())           : [Payslip.simpleName, PayLine.simpleName],
            (ProductItem.simpleName.toLowerCase())       : [ProductItem.simpleName, Voucher.simpleName, Membership.simpleName, Article.simpleName],
            (Qualification.simpleName.toLowerCase())     : [Qualification.simpleName],
            (Room.simpleName.toLowerCase())              : [Room.simpleName, Session.simpleName],
            (Script.simpleName.toLowerCase())            : [Script.simpleName],
            (Site.simpleName.toLowerCase())              : [Site.simpleName, Session.simpleName],
            (TrainingPackage.simpleName.toLowerCase())   : [TrainingPackage.simpleName],
            (WaitingList.simpleName.toLowerCase())       : [WaitingList.simpleName],
            (VoucherProduct.simpleName.toLowerCase())    : [VoucherProduct.simpleName],
            (MembershipProduct.simpleName.toLowerCase()) : [MembershipProduct.simpleName],
            (Survey.simpleName.toLowerCase())            : [Survey.simpleName],
            (Outcome.simpleName.toLowerCase())           : [Outcome.simpleName],
            (Assessment.simpleName.toLowerCase())        : [Assessment.simpleName],
            (AssessmentSubmission.simpleName.toLowerCase()):[AssessmentSubmission.simpleName],
            (TRAINEESHIP_CLASSES_ENTITY)                 : [CourseClass.simpleName],
            (PriorLearning.simpleName.toLowerCase())     : [PriorLearning.simpleName],
            (Document.simpleName.toLowerCase())          : [Document.simpleName],
            (Lead.simpleName.toLowerCase())              : [Lead.simpleName]
    ]

    private static ObjectSelect<? extends CayenneDataObject> getSelectedRecordsObjectSelect(String entityName, String search, String filter, List<TagGroupDTO> tagGroups, List<SortingDTO> sorting, AqlService aqlService, ObjectContext context) {
        Class<? extends CayenneDataObject> clzz = EntityUtil.entityClassForName(entityName)
        ObjectSelect<CayenneDataObject> objectSelect = ObjectSelect.query(clzz)

        objectSelect = parseSearchQuery(objectSelect, context, aqlService, entityName, search, filter, tagGroups)

        // if are using sorting by multifield column (e.g. contact.full_name) then find sortField for him in his TableModel
        def tableModel = DefaultUserPreference.DEFAULT_MODEL_MAP.get(entityName)
        List<String> sortFields = null
        if (tableModel != null) {
            for (ColumnDTO column : tableModel.columns) {
                boolean equalSortings = true
                for (SortingDTO subSortings : sorting) {
                    equalSortings &= column.attribute.equals(subSortings.attribute)
                }
                if ((equalSortings) && (column.sortFields != null) && (column.sortFields.size() > 0)) {
                    sortFields = column.sortFields
                    break
                }
            }
        }

        // if were found sortFields for field - use them for sorting, sort ascending will get from first sortField
        if ((sortFields != null) && (sorting != null) && (sorting.size() > 0)) {
            sortFields.each {
                objectSelect.orderBy(it, sorting.get(0).ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING)
            }
        } else {
            sorting.each {
                objectSelect.orderBy(it.attribute, it.ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING)
            }
        }

        return objectSelect
    }

    static List<? extends CayenneDataObject> getSelectedRecords(String entityName, String search, List<SortingDTO> sorting, AqlService aqlService, ObjectContext context) {
        getSelectedRecordsObjectSelect(entityName, search, null, null, sorting, aqlService, context).select(context)
    }

    static List<? extends CayenneDataObject> getSelectedRecords(String entityName, String search, String filter, List<TagGroupDTO> tagGroups, List<SortingDTO> sorting, AqlService aqlService, ObjectContext context) {
        getSelectedRecordsObjectSelect(entityName, search, filter, tagGroups, sorting, aqlService, context).select(context)
    }

    static List<Long> getSelectedRecordIds(String entityName, String search, String filter, List<TagGroupDTO> tagGroups, List<SortingDTO> sorting, AqlService aqlService, ObjectContext context) {
        getSelectedRecordsObjectSelect(entityName, search, filter, tagGroups, sorting, aqlService, context).column(Property.create("id", Long)).select(context)
    }
}

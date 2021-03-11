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

package ish.oncourse.server.modules

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Scopes
import ish.oncourse.server.api.dao.AccountDao
import ish.oncourse.server.api.dao.ApiTokenDao
import ish.oncourse.server.api.dao.ApplicationDao
import ish.oncourse.server.api.dao.ArticleProductDao
import ish.oncourse.server.api.dao.AssessmentDao
import ish.oncourse.server.api.dao.AssessmentSubmissionDao
import ish.oncourse.server.api.dao.CertificateDao
import ish.oncourse.server.api.dao.CertificateOutcomeDao
import ish.oncourse.server.api.dao.ClassCostDao
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.ContactRelationTypeDao
import ish.oncourse.server.api.dao.CorporatePassDao
import ish.oncourse.server.api.dao.CorporatePassProductDao
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.api.dao.CourseClassTutorDao
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.api.dao.CourseModuleDao
import ish.oncourse.server.api.dao.DefinedTutorRoleDao
import ish.oncourse.server.api.dao.DiscountDao
import ish.oncourse.server.api.dao.DiscountMembershipDao
import ish.oncourse.server.api.dao.DiscountMembershipRelationTypeDao
import ish.oncourse.server.api.dao.DocumentDao
import ish.oncourse.server.api.dao.EmailTemplateDao
import ish.oncourse.server.api.dao.EnrolmentDao
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.api.dao.EntityRelationTypeDao
import ish.oncourse.server.api.dao.ExportTemplateDao
import ish.oncourse.server.api.dao.FieldConfigurationSchemeDao
import ish.oncourse.server.api.dao.ImportDao
import ish.oncourse.server.api.dao.InvoiceDao
import ish.oncourse.server.api.dao.InvoiceDueDateDao
import ish.oncourse.server.api.dao.InvoiceLineDao
import ish.oncourse.server.api.dao.MembershipProductDao
import ish.oncourse.server.api.dao.MessageDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.dao.NoteDao
import ish.oncourse.server.api.dao.OutcomeDao
import ish.oncourse.server.api.dao.PayRateDao
import ish.oncourse.server.api.dao.PaymentInDao
import ish.oncourse.server.api.dao.PayslipDao
import ish.oncourse.server.api.dao.PriorLearningDao
import ish.oncourse.server.api.dao.ProductDao
import ish.oncourse.server.api.dao.QualificationDao
import ish.oncourse.server.api.dao.ReportDao
import ish.oncourse.server.api.dao.ReportOverlayDao
import ish.oncourse.server.api.dao.ScriptDao
import ish.oncourse.server.api.dao.SurveyDao
import ish.oncourse.server.api.dao.TaxDao
import ish.oncourse.server.api.dao.UnavailableRuleDao
import ish.oncourse.server.api.dao.UserDao
import ish.oncourse.server.api.dao.VoucherProductCourseDao
import ish.oncourse.server.api.dao.VoucherProductDao
import ish.oncourse.server.api.dao.WaitingListDao

class ApiCayenneLayerModule implements Module {

    @Override
    void configure(Binder binder) {
        binder.bind(AccountDao).in(Scopes.SINGLETON)
        binder.bind(ApplicationDao).in(Scopes.SINGLETON)
        binder.bind(ArticleProductDao).in(Scopes.SINGLETON)
        binder.bind(AssessmentDao).in(Scopes.SINGLETON)
        binder.bind(AssessmentSubmissionDao).in(Scopes.SINGLETON)


        binder.bind(CertificateDao).in(Scopes.SINGLETON)
        binder.bind(CertificateOutcomeDao).in(Scopes.SINGLETON)
        binder.bind(ContactDao).in(Scopes.SINGLETON)
        binder.bind(ContactRelationTypeDao).in(Scopes.SINGLETON)
        binder.bind(CorporatePassDao).in(Scopes.SINGLETON)
        binder.bind(CorporatePassProductDao).in(Scopes.SINGLETON)
        binder.bind(CourseDao).in(Scopes.SINGLETON)
        binder.bind(CourseModuleDao).in(Scopes.SINGLETON)
        binder.bind(CourseClassDao).in(Scopes.SINGLETON)

        binder.bind(DefinedTutorRoleDao).in(Scopes.SINGLETON)
        binder.bind(DiscountDao).in(Scopes.SINGLETON)
        binder.bind(DiscountMembershipDao).in(Scopes.SINGLETON)
        binder.bind(DiscountMembershipRelationTypeDao).in(Scopes.SINGLETON)
        binder.bind(DocumentDao).in(Scopes.SINGLETON)

        binder.bind(EmailTemplateDao).in(Scopes.SINGLETON)
        binder.bind(EnrolmentDao).in(Scopes.SINGLETON)
        binder.bind(EntityRelationDao).in(Scopes.SINGLETON)
        binder.bind(EntityRelationTypeDao).in(Scopes.SINGLETON)
        binder.bind(ExportTemplateDao).in(Scopes.SINGLETON)

        binder.bind(FieldConfigurationSchemeDao).in(Scopes.SINGLETON)

        binder.bind(ImportDao).in(Scopes.SINGLETON)
        binder.bind(InvoiceDao).in(Scopes.SINGLETON)
        binder.bind(InvoiceDueDateDao).in(Scopes.SINGLETON)
        binder.bind(InvoiceLineDao).in(Scopes.SINGLETON)

        binder.bind(MembershipProductDao).in(Scopes.SINGLETON)
        binder.bind(MessageDao).in(Scopes.SINGLETON)
        binder.bind(ModuleDao).in(Scopes.SINGLETON)

        binder.bind(OutcomeDao).in(Scopes.SINGLETON)

        binder.bind(PaymentInDao).in(Scopes.SINGLETON)
        binder.bind(PayRateDao).in(Scopes.SINGLETON)
        binder.bind(PriorLearningDao).in(Scopes.SINGLETON)
        binder.bind(ProductDao).in(Scopes.SINGLETON)
        binder.bind(PayslipDao).in(Scopes.SINGLETON)

        binder.bind(QualificationDao).in(Scopes.SINGLETON)

        binder.bind(SurveyDao).in(Scopes.SINGLETON)
        binder.bind(ScriptDao).in(Scopes.SINGLETON)

        binder.bind(TaxDao).in(Scopes.SINGLETON)

        binder.bind(VoucherProductCourseDao).in(Scopes.SINGLETON)
        binder.bind(VoucherProductDao).in(Scopes.SINGLETON)
        binder.bind(ReportDao).in(Scopes.SINGLETON)
        binder.bind(ReportOverlayDao).in(Scopes.SINGLETON)
        binder.bind(NoteDao).in(Scopes.SINGLETON)
        binder.bind(CourseClassDao).in(Scopes.SINGLETON)
        binder.bind(CourseClassTutorDao).in(Scopes.SINGLETON)
        binder.bind(ClassCostDao).in(Scopes.SINGLETON)

        binder.bind(UnavailableRuleDao).in(Scopes.SINGLETON)
        binder.bind(UserDao).in(Scopes.SINGLETON)
        binder.bind(WaitingListDao).in(Scopes.SINGLETON)
        binder.bind(ApiTokenDao).in(Scopes.SINGLETON)

    }
}

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

import io.bootique.di.Binder
import io.bootique.di.BQModule
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
import ish.oncourse.server.api.dao.FacultyDao
import ish.oncourse.server.api.dao.FieldConfigurationSchemeDao
import ish.oncourse.server.api.dao.GradingTypeDao
import ish.oncourse.server.api.dao.ImportDao
import ish.oncourse.server.api.dao.InvoiceDao
import ish.oncourse.server.api.dao.InvoiceDueDateDao
import ish.oncourse.server.api.dao.InvoiceLineDao
import ish.oncourse.server.api.dao.LeadDao
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

class ApiCayenneLayerModule implements BQModule {

    @Override
    void configure(Binder binder) {
        binder.bind(AccountDao).inSingletonScope()
        binder.bind(ApplicationDao).inSingletonScope()
        binder.bind(ArticleProductDao).inSingletonScope()
        binder.bind(AssessmentDao).inSingletonScope()
        binder.bind(AssessmentSubmissionDao).inSingletonScope()


        binder.bind(CertificateDao).inSingletonScope()
        binder.bind(CertificateOutcomeDao).inSingletonScope()
        binder.bind(ContactDao).inSingletonScope()
        binder.bind(ContactRelationTypeDao).inSingletonScope()
        binder.bind(CorporatePassDao).inSingletonScope()
        binder.bind(CorporatePassProductDao).inSingletonScope()
        binder.bind(CourseDao).inSingletonScope()
        binder.bind(CourseModuleDao).inSingletonScope()
        binder.bind(CourseClassDao).inSingletonScope()

        binder.bind(DefinedTutorRoleDao).inSingletonScope()
        binder.bind(DiscountDao).inSingletonScope()
        binder.bind(DiscountMembershipDao).inSingletonScope()
        binder.bind(DiscountMembershipRelationTypeDao).inSingletonScope()
        binder.bind(DocumentDao).inSingletonScope()

        binder.bind(EmailTemplateDao).inSingletonScope()
        binder.bind(EnrolmentDao).inSingletonScope()
        binder.bind(EntityRelationDao).inSingletonScope()
        binder.bind(EntityRelationTypeDao).inSingletonScope()
        binder.bind(ExportTemplateDao).inSingletonScope()

        binder.bind(FacultyDao).inSingletonScope()
        binder.bind(FieldConfigurationSchemeDao).inSingletonScope()
        binder.bind(GradingTypeDao).inSingletonScope()

        binder.bind(ImportDao).inSingletonScope()
        binder.bind(InvoiceDao).inSingletonScope()
        binder.bind(InvoiceDueDateDao).inSingletonScope()
        binder.bind(InvoiceLineDao).inSingletonScope()

        binder.bind(LeadDao).inSingletonScope()
        binder.bind(MembershipProductDao).inSingletonScope()
        binder.bind(MessageDao).inSingletonScope()
        binder.bind(ModuleDao).inSingletonScope()

        binder.bind(OutcomeDao).inSingletonScope()

        binder.bind(PaymentInDao).inSingletonScope()
        binder.bind(PayRateDao).inSingletonScope()
        binder.bind(PriorLearningDao).inSingletonScope()
        binder.bind(ProductDao).inSingletonScope()
        binder.bind(PayslipDao).inSingletonScope()

        binder.bind(QualificationDao).inSingletonScope()

        binder.bind(SurveyDao).inSingletonScope()
        binder.bind(ScriptDao).inSingletonScope()

        binder.bind(TaxDao).inSingletonScope()

        binder.bind(VoucherProductCourseDao).inSingletonScope()
        binder.bind(VoucherProductDao).inSingletonScope()
        binder.bind(ReportDao).inSingletonScope()
        binder.bind(ReportOverlayDao).inSingletonScope()
        binder.bind(NoteDao).inSingletonScope()
        binder.bind(CourseClassDao).inSingletonScope()
        binder.bind(CourseClassTutorDao).inSingletonScope()
        binder.bind(ClassCostDao).inSingletonScope()

        binder.bind(UnavailableRuleDao).inSingletonScope()
        binder.bind(UserDao).inSingletonScope()
        binder.bind(WaitingListDao).inSingletonScope()
        binder.bind(ApiTokenDao).inSingletonScope()
    }
}

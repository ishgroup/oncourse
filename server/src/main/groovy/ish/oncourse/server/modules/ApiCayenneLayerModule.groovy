/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.modules

import io.bootique.di.BQModule
import io.bootique.di.Binder
import ish.oncourse.server.api.dao.*

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

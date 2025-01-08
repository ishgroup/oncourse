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

package ish.oncourse.server.api

import io.bootique.di.Binder
import io.bootique.ConfigModule
import ish.oncourse.server.api.cxf.CXFModule
import ish.oncourse.server.api.v1.service.*
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

class ServerApiModule extends ConfigModule {

    @Override
    void configure(Binder binder) {
        CXFModule.contributeResources(binder).add(AccessApi)
        CXFModule.contributeResources(binder).add(AccountApi)
        CXFModule.contributeResources(binder).add(ApplicationApi)
        CXFModule.contributeResources(binder).add(ArticleProductApi)
        CXFModule.contributeResources(binder).add(AssessmentApi)
        CXFModule.contributeResources(binder).add(AssessmentSubmissionApi)
        CXFModule.contributeResources(binder).add(AuditApi)
        CXFModule.contributeResources(binder).add(AuthenticationApi)
        CXFModule.contributeResources(binder).add(AvetmissExportApi)

        CXFModule.contributeResources(binder).add(BankingApi)

        CXFModule.contributeResources(binder).add(CertificateApi)
        CXFModule.contributeResources(binder).add(ConcessionApi)
        CXFModule.contributeResources(binder).add(ContactApi)
        CXFModule.contributeResources(binder).add(ContactMergeApi)
        CXFModule.contributeResources(binder).add(ContactInsightApi)
        CXFModule.contributeResources(binder).add(ControlApi)
        CXFModule.contributeResources(binder).add(CorporatePassApi)
        CXFModule.contributeResources(binder).add(CourseApi)
        CXFModule.contributeResources(binder).add(CourseClassApi)
        CXFModule.contributeResources(binder).add(FacultyApi)
        CXFModule.contributeResources(binder).add(ExportApi)
        CXFModule.contributeResources(binder).add(CustomFieldApi)

        CXFModule.contributeResources(binder).add(DashboardApi)
        CXFModule.contributeResources(binder).add(DataCollectionApi)
        CXFModule.contributeResources(binder).add(DiscountApi)
        CXFModule.contributeResources(binder).add(DocumentApi)

        CXFModule.contributeResources(binder).add(EmailTemplateApi)
        CXFModule.contributeResources(binder).add(EnrolmentApi)
        CXFModule.contributeResources(binder).add(EntityApi)
        CXFModule.contributeResources(binder).add(ExportTemplateApi)

        CXFModule.contributeResources(binder).add(FilterApi)
        CXFModule.contributeResources(binder).add(FundingContractApi)
        CXFModule.contributeResources(binder).add(FundingUploadApi)

        CXFModule.contributeResources(binder).add(GradingApi)

        CXFModule.contributeResources(binder).add(HolidayApi)

        CXFModule.contributeResources(binder).add(IntegrationApi)
        CXFModule.contributeResources(binder).add(InvoiceApi)
        CXFModule.contributeResources(binder).add(ImportApi)

        CXFModule.contributeResources(binder).add(LeadApi)
        CXFModule.contributeResources(binder).add(MessageApi)
        CXFModule.contributeResources(binder).add(MembershipProductApi)
        CXFModule.contributeResources(binder).add(ModuleApi)

        CXFModule.contributeResources(binder).add(OutcomeApi)
        CXFModule.contributeResources(binder).add(ReportOverlayApi)

        CXFModule.contributeResources(binder).add(PaymentApi)
        CXFModule.contributeResources(binder).add(PaymentInApi)
        CXFModule.contributeResources(binder).add(PaymentOutApi)
        CXFModule.contributeResources(binder).add(PayrollApi)
        CXFModule.contributeResources(binder).add(PayslipApi)
        CXFModule.contributeResources(binder).add(PdfApi)
        CXFModule.contributeResources(binder).add(PdfTemplateApi)
        CXFModule.contributeResources(binder).add(PreferenceApi)
        CXFModule.contributeResources(binder).add(PriorLearningApi)
        CXFModule.contributeResources(binder).add(ProductItemApi)

        CXFModule.contributeResources(binder).add(QualificationApi)

        CXFModule.contributeResources(binder).add(RoomApi)
        CXFModule.contributeResources(binder).add(RoomValidationApi)

        CXFModule.contributeResources(binder).add(ScriptApi)
        CXFModule.contributeResources(binder).add(SiteApi)
        CXFModule.contributeResources(binder).add(SurveyApi)

        CXFModule.contributeResources(binder).add(TagApi)
        CXFModule.contributeResources(binder).add(TaxApi)
        CXFModule.contributeResources(binder).add(TransactionApi)
        CXFModule.contributeResources(binder).add(TutorRoleApi)

        CXFModule.contributeResources(binder).add(UserApi)
        CXFModule.contributeResources(binder).add(UserPreferenceApi)
        CXFModule.contributeResources(binder).add(UserRoleApi)

        CXFModule.contributeResources(binder).add(VoucherProductApi)

        CXFModule.contributeResources(binder).add(WaitingListApi)

        CXFModule.contributeResources(binder).add(TimetableApi)
        CXFModule.contributeResources(binder).add(FinalisePeriodApi)
        CXFModule.contributeResources(binder).add(NoteApi)
        CXFModule.contributeResources(binder).add(CourseClassTutorApi)
        CXFModule.contributeResources(binder).add(ClassCostApi)
        CXFModule.contributeResources(binder).add(AttendanceApi)
        CXFModule.contributeResources(binder).add(AssessmentClassApi)
        CXFModule.contributeResources(binder).add(SessionApi)
        CXFModule.contributeResources(binder).add(CheckoutApi)
        CXFModule.contributeResources(binder).add(EntityRelationTypeApi)
        CXFModule.contributeResources(binder).add(TokenApi)
        CXFModule.contributeResources(binder).add(LogsApi)

        //------
        CXFModule.contributeFeatures(binder).add(JAXRSBeanValidationFeature)
    }
}

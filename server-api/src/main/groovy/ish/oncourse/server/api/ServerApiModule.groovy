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

import com.google.inject.Binder
import io.bootique.ConfigModule
import ish.oncourse.server.api.cxf.CXFModule
import ish.oncourse.server.api.v1.service.*
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

class ServerApiModule extends ConfigModule {

    @Override
    void configure(Binder binder) {
        CXFModule.contributeResources(binder).addBinding().to(AccessApi)
        CXFModule.contributeResources(binder).addBinding().to(AccountApi)
        CXFModule.contributeResources(binder).addBinding().to(ApplicationApi)
        CXFModule.contributeResources(binder).addBinding().to(ArticleProductApi)
        CXFModule.contributeResources(binder).addBinding().to(AssessmentApi)
        CXFModule.contributeResources(binder).addBinding().to(AssessmentSubmissionApi)
        CXFModule.contributeResources(binder).addBinding().to(AuditApi)
        CXFModule.contributeResources(binder).addBinding().to(AuthenticationApi)
        CXFModule.contributeResources(binder).addBinding().to(AvetmissExportApi)

        CXFModule.contributeResources(binder).addBinding().to(BankingApi)

        CXFModule.contributeResources(binder).addBinding().to(CertificateApi)
        CXFModule.contributeResources(binder).addBinding().to(ConcessionApi)
        CXFModule.contributeResources(binder).addBinding().to(ContactApi)
        CXFModule.contributeResources(binder).addBinding().to(ContactMergeApi)
        CXFModule.contributeResources(binder).addBinding().to(ControlApi)
        CXFModule.contributeResources(binder).addBinding().to(CorporatePassApi)
        CXFModule.contributeResources(binder).addBinding().to(CourseApi)
        CXFModule.contributeResources(binder).addBinding().to(CourseClassApi)
        CXFModule.contributeResources(binder).addBinding().to(ExportApi)
        CXFModule.contributeResources(binder).addBinding().to(CustomFieldApi)

        CXFModule.contributeResources(binder).addBinding().to(DashboardApi)
        CXFModule.contributeResources(binder).addBinding().to(DataCollectionApi)
        CXFModule.contributeResources(binder).addBinding().to(DiscountApi)
        CXFModule.contributeResources(binder).addBinding().to(DocumentApi)
        CXFModule.contributeResources(binder).addBinding().to(DocumentExportApi)

        CXFModule.contributeResources(binder).addBinding().to(EmailTemplateApi)
        CXFModule.contributeResources(binder).addBinding().to(EnrolmentApi)
        CXFModule.contributeResources(binder).addBinding().to(EntityApi)
        CXFModule.contributeResources(binder).addBinding().to(ExportTemplateApi)

        CXFModule.contributeResources(binder).addBinding().to(FilterApi)
        CXFModule.contributeResources(binder).addBinding().to(FundingContractApi)
        CXFModule.contributeResources(binder).addBinding().to(FundingUploadApi)

        CXFModule.contributeResources(binder).addBinding().to(HolidayApi)

        CXFModule.contributeResources(binder).addBinding().to(IntegrationApi)
        CXFModule.contributeResources(binder).addBinding().to(InvoiceApi)
        CXFModule.contributeResources(binder).addBinding().to(ImportApi)

        CXFModule.contributeResources(binder).addBinding().to(MessageApi)
        CXFModule.contributeResources(binder).addBinding().to(MembershipProductApi)
        CXFModule.contributeResources(binder).addBinding().to(ModuleApi)

        CXFModule.contributeResources(binder).addBinding().to(OutcomeApi)
        CXFModule.contributeResources(binder).addBinding().to(ReportOverlayApi)

        CXFModule.contributeResources(binder).addBinding().to(PaymentApi)
        CXFModule.contributeResources(binder).addBinding().to(PaymentInApi)
        CXFModule.contributeResources(binder).addBinding().to(PaymentOutApi)
        CXFModule.contributeResources(binder).addBinding().to(PayrollApi)
        CXFModule.contributeResources(binder).addBinding().to(PayslipApi)
        CXFModule.contributeResources(binder).addBinding().to(PdfApi)
        CXFModule.contributeResources(binder).addBinding().to(PdfTemplateApi)
        CXFModule.contributeResources(binder).addBinding().to(PreferenceApi)
        CXFModule.contributeResources(binder).addBinding().to(PriorLearningApi)
        CXFModule.contributeResources(binder).addBinding().to(ProductItemApi)

        CXFModule.contributeResources(binder).addBinding().to(QualificationApi)

        CXFModule.contributeResources(binder).addBinding().to(RoomApi)
        CXFModule.contributeResources(binder).addBinding().to(RoomValidationApi)

        CXFModule.contributeResources(binder).addBinding().to(ScriptApi)
        CXFModule.contributeResources(binder).addBinding().to(SiteApi)
        CXFModule.contributeResources(binder).addBinding().to(SurveyApi)

        CXFModule.contributeResources(binder).addBinding().to(TagApi)
        CXFModule.contributeResources(binder).addBinding().to(TaxApi)
        CXFModule.contributeResources(binder).addBinding().to(TransactionApi)
        CXFModule.contributeResources(binder).addBinding().to(TutorRoleApi)

        CXFModule.contributeResources(binder).addBinding().to(UserApi)
        CXFModule.contributeResources(binder).addBinding().to(UserPreferenceApi)
        CXFModule.contributeResources(binder).addBinding().to(UserRoleApi)

        CXFModule.contributeResources(binder).addBinding().to(VoucherProductApi)

        CXFModule.contributeResources(binder).addBinding().to(WaitingListApi)

        CXFModule.contributeResources(binder).addBinding().to(TimetableApi)
        CXFModule.contributeResources(binder).addBinding().to(FinalisePeriodApi)
        CXFModule.contributeResources(binder).addBinding().to(NoteApi)
        CXFModule.contributeResources(binder).addBinding().to(CourseClassTutorApi)
        CXFModule.contributeResources(binder).addBinding().to(ClassCostApi)
        CXFModule.contributeResources(binder).addBinding().to(AttendanceApi)
        CXFModule.contributeResources(binder).addBinding().to(TutorAttendanceApi)
        CXFModule.contributeResources(binder).addBinding().to(AssessmentClassApi)
        CXFModule.contributeResources(binder).addBinding().to(SessionApi)
        CXFModule.contributeResources(binder).addBinding().to(CheckoutApi)
        CXFModule.contributeResources(binder).addBinding().to(EntityRelationTypeApi)
        CXFModule.contributeResources(binder).addBinding().to(TokenApi)

        //------
        CXFModule.contributeFeatures(binder).addBinding().to(JAXRSBeanValidationFeature)
    }
}

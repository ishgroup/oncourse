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
import ish.oncourse.server.api.v1.service.*
import ish.oncourse.server.api.v1.service.impl.*

class ApiImplementationModule implements BQModule {

    @Override
    void configure(Binder binder) {

        binder.bind(AccessApi).to(AccessApiImpl)
        binder.bind(AccountApi).to(AccountApiImpl)
        binder.bind(ApplicationApi).to(ApplicationApiImpl)
        binder.bind(ArticleProductApi).to(ArticleProductApiImpl)
        binder.bind(AssessmentApi).to(AssessmentApiImpl)
        binder.bind(AssessmentSubmissionApi).to(AssessmentSubmissionApiImpl)
        binder.bind(AuditApi).to(AuditApiImpl)
        binder.bind(AuthenticationApi).to(AuthenticationApiImpl)
        binder.bind(AvetmissExportApi).to(AvetmissExportApiImpl)

        binder.bind(BankingApi).to(BankingApiImpl)

        binder.bind(CertificateApi).to(CertificateApiImpl)
        binder.bind(ConcessionApi).to(ConcessionApiImpl)
        binder.bind(ContactApi).to(ContactApiImpl)
        binder.bind(ContactMergeApi).to(ContactMergeApiImpl)
        binder.bind(ControlApi).to(ControlApiImpl)
        binder.bind(CorporatePassApi).to(CorporatePassApiImpl)
        binder.bind(CourseApi).to(CourseApiImpl)
        binder.bind(CourseClassApi).to(CourseClassApiImpl)
        binder.bind(ExportApi).to(ExportApiImpl)
        binder.bind(CustomFieldApi).to(CustomFieldApiImpl)

        binder.bind(DashboardApi).to(DashboardApiImpl)
        binder.bind(DataCollectionApi).to(DataCollectionApiImpl)
        binder.bind(DiscountApi).to(DiscountApiImpl)
        binder.bind(DocumentApi).to(DocumentApiImpl)
        binder.bind(DocumentExportApi).to(DocumentExportApiImpl)

        binder.bind(EmailTemplateApi).to(EmailTemplateApiImpl)
        binder.bind(EnrolmentApi).to(EnrolmentApiImpl)
        binder.bind(EntityApi).to(EntityApiImpl)
        binder.bind(ExportTemplateApi).to(ExportTemplateApiImpl)

        binder.bind(FilterApi).to(FilterApiImpl)
        binder.bind(FundingContractApi).to(FundingContractApiImpl)
        binder.bind(FundingUploadApi).to(FundingUploadApiImpl)

        binder.bind(HolidayApi).to(HolidayApiImpl)

        binder.bind(ImportApi).to(ImportApiImpl)
        binder.bind(IntegrationApi).to(IntegrationApiImpl)
        binder.bind(InvoiceApi).to(InvoiceApiImpl)
        binder.bind(LeadApi).to(LeadApiImpl)
        binder.bind(MessageApi).to(MessageApiImpl)
        binder.bind(MembershipProductApi).to(MembershipProductApiImpl)
        binder.bind(ModuleApi).to(ModuleApiImpl)


        binder.bind(OutcomeApi).to(OutcomeApiImpl)
        binder.bind(ReportOverlayApi).to(ReportOverlayApiImpl)

        binder.bind(PaymentApi).to(PaymentApiImpl)
        binder.bind(PaymentInApi).to(PaymentInApiImpl)
        binder.bind(PaymentOutApi).to(PaymentOutApiImpl)
        binder.bind(PayrollApi).to(PayrollApiImpl)
        binder.bind(PayslipApi).to(PayslipApiImpl)
        binder.bind(PdfApi).to(PdfApiImpl)
        binder.bind(PdfTemplateApi).to(PdfTemplateApiImpl)
        binder.bind(PreferenceApi).to(PreferenceApiImpl)
        binder.bind(PriorLearningApi).to(PriorLearningApiImpl)
        binder.bind(ProductItemApi).to(ProductItemApiImpl)


        binder.bind(QualificationApi).to(QualificationApiImpl)

        binder.bind(RoomApi).to(RoomApiImpl)
        binder.bind(RoomValidationApi).to(RoomValidationApiImpl)

        binder.bind(ScriptApi).to(ScriptApiImpl)
        binder.bind(SiteApi).to(SiteApiImpl)
        binder.bind(SurveyApi).to(SurveyApiImpl)


        binder.bind(TagApi).to(TagApiImpl)
        binder.bind(TaxApi).to(TaxApiImpl)
        binder.bind(TimetableApi).to(TimetableApiImpl)
        binder.bind(TransactionApi).to(TransactionApiImpl)
        binder.bind(TutorRoleApi).to(TutorRoleApiImpl)

        binder.bind(UserApi).to(UserApiImpl)
        binder.bind(UserPreferenceApi).to(UserPreferenceApiImpl)
        binder.bind(UserRoleApi).to(UserRoleApiImpl)

        binder.bind(VoucherProductApi).to(VoucherProductApiImpl)

        binder.bind(WaitingListApi).to(WaitingListApiImpl)
        binder.bind(FinalisePeriodApi).to(FinalisePeriodApiImpl)
        binder.bind(NoteApi).to(NoteApiImpl)
        binder.bind(CourseClassTutorApi).to(CourseClassTutorApiImpl)
        binder.bind(ClassCostApi).to(ClassCostApiImpl)
        binder.bind(AttendanceApi).to(AttendanceApiImpl)
        binder.bind(TutorAttendanceApi).to(TutorAttendanceApiImpl)
        binder.bind(AssessmentClassApi).to(AssessmentClassApiImpl)
        binder.bind(SessionApi).to(SessionApiImpl)
        binder.bind(CheckoutApi).to(CheckoutApiImpl)
        binder.bind(EntityRelationTypeApi).to(EntityRelationTypeApiImpl)
        binder.bind(TokenApi).to(ApiTokenApiImpl)
        binder.bind(GradingApi).to(GradingApiImpl)
    }
}

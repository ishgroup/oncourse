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
import ish.oncourse.server.api.v1.AssessmentSubmissionApiImpl
import ish.oncourse.server.api.v1.service.AccessApi
import ish.oncourse.server.api.v1.service.AccountApi
import ish.oncourse.server.api.v1.service.ApplicationApi
import ish.oncourse.server.api.v1.service.ArticleProductApi
import ish.oncourse.server.api.v1.service.AssessmentApi
import ish.oncourse.server.api.v1.service.AssessmentClassApi
import ish.oncourse.server.api.v1.service.AssessmentSubmissionApi
import ish.oncourse.server.api.v1.service.AttendanceApi
import ish.oncourse.server.api.v1.service.AuditApi
import ish.oncourse.server.api.v1.service.AuthenticationApi
import ish.oncourse.server.api.v1.service.AvetmissExportApi
import ish.oncourse.server.api.v1.service.BankingApi
import ish.oncourse.server.api.v1.service.CertificateApi
import ish.oncourse.server.api.v1.service.CheckoutApi
import ish.oncourse.server.api.v1.service.ClassCostApi
import ish.oncourse.server.api.v1.service.ConcessionApi
import ish.oncourse.server.api.v1.service.ContactApi
import ish.oncourse.server.api.v1.service.ContactMergeApi
import ish.oncourse.server.api.v1.service.ControlApi
import ish.oncourse.server.api.v1.service.CorporatePassApi
import ish.oncourse.server.api.v1.service.CourseApi
import ish.oncourse.server.api.v1.service.CourseClassApi
import ish.oncourse.server.api.v1.service.CourseClassTutorApi
import ish.oncourse.server.api.v1.service.CustomFieldApi
import ish.oncourse.server.api.v1.service.DashboardApi
import ish.oncourse.server.api.v1.service.DataCollectionApi
import ish.oncourse.server.api.v1.service.DiscountApi
import ish.oncourse.server.api.v1.service.DocumentApi
import ish.oncourse.server.api.v1.service.DocumentExportApi
import ish.oncourse.server.api.v1.service.EmailTemplateApi
import ish.oncourse.server.api.v1.service.EnrolmentApi
import ish.oncourse.server.api.v1.service.EntityApi
import ish.oncourse.server.api.v1.service.EntityRelationTypeApi
import ish.oncourse.server.api.v1.service.ExportApi
import ish.oncourse.server.api.v1.service.ExportTemplateApi
import ish.oncourse.server.api.v1.service.FilterApi
import ish.oncourse.server.api.v1.service.FinalisePeriodApi
import ish.oncourse.server.api.v1.service.FundingContractApi
import ish.oncourse.server.api.v1.service.FundingUploadApi
import ish.oncourse.server.api.v1.service.HolidayApi
import ish.oncourse.server.api.v1.service.ImportApi
import ish.oncourse.server.api.v1.service.IntegrationApi
import ish.oncourse.server.api.v1.service.InvoiceApi
import ish.oncourse.server.api.v1.service.MembershipProductApi
import ish.oncourse.server.api.v1.service.MessageApi
import ish.oncourse.server.api.v1.service.ModuleApi
import ish.oncourse.server.api.v1.service.NoteApi
import ish.oncourse.server.api.v1.service.OutcomeApi
import ish.oncourse.server.api.v1.service.PriorLearningApi
import ish.oncourse.server.api.v1.service.ReportOverlayApi
import ish.oncourse.server.api.v1.service.PaymentApi
import ish.oncourse.server.api.v1.service.PaymentInApi
import ish.oncourse.server.api.v1.service.PaymentOutApi
import ish.oncourse.server.api.v1.service.PayrollApi
import ish.oncourse.server.api.v1.service.PayslipApi
import ish.oncourse.server.api.v1.service.PdfApi
import ish.oncourse.server.api.v1.service.PdfTemplateApi
import ish.oncourse.server.api.v1.service.PreferenceApi
import ish.oncourse.server.api.v1.service.ProductItemApi
import ish.oncourse.server.api.v1.service.QualificationApi
import ish.oncourse.server.api.v1.service.RoomApi
import ish.oncourse.server.api.v1.service.RoomValidationApi
import ish.oncourse.server.api.v1.service.ScriptApi
import ish.oncourse.server.api.v1.service.SessionApi
import ish.oncourse.server.api.v1.service.SiteApi
import ish.oncourse.server.api.v1.service.SurveyApi
import ish.oncourse.server.api.v1.service.TagApi
import ish.oncourse.server.api.v1.service.TaxApi
import ish.oncourse.server.api.v1.service.TimetableApi
import ish.oncourse.server.api.v1.service.TransactionApi
import ish.oncourse.server.api.v1.service.TutorAttendanceApi
import ish.oncourse.server.api.v1.service.TutorRoleApi
import ish.oncourse.server.api.v1.service.UserApi
import ish.oncourse.server.api.v1.service.UserPreferenceApi
import ish.oncourse.server.api.v1.service.UserRoleApi
import ish.oncourse.server.api.v1.service.VoucherProductApi
import ish.oncourse.server.api.v1.service.WaitingListApi
import ish.oncourse.server.api.v1.service.impl.AccessApiImpl
import ish.oncourse.server.api.v1.service.impl.AccountApiImpl
import ish.oncourse.server.api.v1.service.impl.ApplicationApiImpl
import ish.oncourse.server.api.v1.service.impl.ArticleProductApiImpl
import ish.oncourse.server.api.v1.service.impl.AssessmentApiImpl
import ish.oncourse.server.api.v1.service.impl.AssessmentClassApiImpl
import ish.oncourse.server.api.v1.service.impl.AttendanceApiImpl
import ish.oncourse.server.api.v1.service.impl.AuditApiImpl
import ish.oncourse.server.api.v1.service.impl.AuthenticationApiImpl
import ish.oncourse.server.api.v1.service.impl.AvetmissExportApiImpl
import ish.oncourse.server.api.v1.service.impl.BankingApiImpl
import ish.oncourse.server.api.v1.service.impl.CertificateApiImpl
import ish.oncourse.server.api.v1.service.impl.CheckoutApiImpl
import ish.oncourse.server.api.v1.service.impl.ClassCostApiImpl
import ish.oncourse.server.api.v1.service.impl.ConcessionApiImpl
import ish.oncourse.server.api.v1.service.impl.ContactApiImpl
import ish.oncourse.server.api.v1.service.impl.ContactMergeApiImpl
import ish.oncourse.server.api.v1.service.impl.ControlApiImpl
import ish.oncourse.server.api.v1.service.impl.CorporatePassApiImpl
import ish.oncourse.server.api.v1.service.impl.CourseApiImpl
import ish.oncourse.server.api.v1.service.impl.CourseClassApiImpl
import ish.oncourse.server.api.v1.service.impl.CourseClassTutorApiImpl
import ish.oncourse.server.api.v1.service.impl.CustomFieldApiImpl
import ish.oncourse.server.api.v1.service.impl.DashboardApiImpl
import ish.oncourse.server.api.v1.service.impl.DataCollectionApiImpl
import ish.oncourse.server.api.v1.service.impl.DiscountApiImpl
import ish.oncourse.server.api.v1.service.impl.DocumentApiImpl
import ish.oncourse.server.api.v1.service.impl.DocumentExportApiImpl
import ish.oncourse.server.api.v1.service.impl.EmailTemplateApiImpl
import ish.oncourse.server.api.v1.service.impl.EnrolmentApiImpl
import ish.oncourse.server.api.v1.service.impl.EntityApiImpl
import ish.oncourse.server.api.v1.service.impl.EntityRelationTypeApiImpl
import ish.oncourse.server.api.v1.service.impl.ExportApiImpl
import ish.oncourse.server.api.v1.service.impl.ExportTemplateApiImpl
import ish.oncourse.server.api.v1.service.impl.FilterApiImpl
import ish.oncourse.server.api.v1.service.impl.FinalisePeriodApiImpl
import ish.oncourse.server.api.v1.service.impl.FundingContractApiImpl
import ish.oncourse.server.api.v1.service.impl.FundingUploadApiImpl
import ish.oncourse.server.api.v1.service.impl.HolidayApiImpl
import ish.oncourse.server.api.v1.service.impl.ImportApiImpl
import ish.oncourse.server.api.v1.service.impl.IntegrationApiImpl
import ish.oncourse.server.api.v1.service.impl.InvoiceApiImpl
import ish.oncourse.server.api.v1.service.impl.MembershipProductApiImpl
import ish.oncourse.server.api.v1.service.impl.MessageApiImpl
import ish.oncourse.server.api.v1.service.impl.ModuleApiImpl
import ish.oncourse.server.api.v1.service.impl.NoteApiImpl
import ish.oncourse.server.api.v1.service.impl.OutcomeApiImpl
import ish.oncourse.server.api.v1.service.impl.PriorLearningApiImpl
import ish.oncourse.server.api.v1.service.impl.ReportOverlayApiImpl
import ish.oncourse.server.api.v1.service.impl.PaymentApiImpl
import ish.oncourse.server.api.v1.service.impl.PaymentInApiImpl
import ish.oncourse.server.api.v1.service.impl.PaymentOutApiImpl
import ish.oncourse.server.api.v1.service.impl.PayrollApiImpl
import ish.oncourse.server.api.v1.service.impl.PayslipApiImpl
import ish.oncourse.server.api.v1.service.impl.PdfApiImpl
import ish.oncourse.server.api.v1.service.impl.PdfTemplateApiImpl
import ish.oncourse.server.api.v1.service.impl.PreferenceApiImpl
import ish.oncourse.server.api.v1.service.impl.ProductItemApiImpl
import ish.oncourse.server.api.v1.service.impl.QualificationApiImpl
import ish.oncourse.server.api.v1.service.impl.RoomApiImpl
import ish.oncourse.server.api.v1.service.impl.RoomValidationApiImpl
import ish.oncourse.server.api.v1.service.impl.ScriptApiImpl
import ish.oncourse.server.api.v1.service.impl.SessionApiImpl
import ish.oncourse.server.api.v1.service.impl.SiteApiImpl
import ish.oncourse.server.api.v1.service.impl.SurveyApiImpl
import ish.oncourse.server.api.v1.service.impl.TagApiImpl
import ish.oncourse.server.api.v1.service.impl.TaxApiImpl
import ish.oncourse.server.api.v1.service.impl.TimetableApiImpl
import ish.oncourse.server.api.v1.service.impl.TransactionApiImpl
import ish.oncourse.server.api.v1.service.impl.TutorAttendanceApiImpl
import ish.oncourse.server.api.v1.service.impl.TutorRoleApiImpl
import ish.oncourse.server.api.v1.service.impl.UserApiImpl
import ish.oncourse.server.api.v1.service.impl.UserPreferenceApiImpl
import ish.oncourse.server.api.v1.service.impl.UserRoleApiImpl
import ish.oncourse.server.api.v1.service.impl.VoucherProductApiImpl
import ish.oncourse.server.api.v1.service.impl.WaitingListApiImpl
import ish.oncourse.server.cayenne.AssessmentSubmission

class ApiImplementationModule implements Module {

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
    }
}

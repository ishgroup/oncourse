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
import com.google.inject.multibindings.Multibinder
import ish.oncourse.server.api.service.AccountApiService
import ish.oncourse.server.api.service.ApiTokenApiService
import ish.oncourse.server.api.service.ApplicationApiService
import ish.oncourse.server.api.service.ArticleProductApiService
import ish.oncourse.server.api.service.AssessmentApiService
import ish.oncourse.server.api.service.AssessmentSubmissionApiService
import ish.oncourse.server.api.service.BulkChangeApiService
import ish.oncourse.server.api.service.CertificateApiService
import ish.oncourse.server.api.service.ClassCostApiService
import ish.oncourse.server.api.service.ContactApiService
import ish.oncourse.server.api.service.CourseApiService
import ish.oncourse.server.api.service.CourseClassApiService
import ish.oncourse.server.api.service.CourseClassTutorApiService
import ish.oncourse.server.api.service.DocumentApiService
import ish.oncourse.server.api.service.EmailTemplateApiService
import ish.oncourse.server.api.service.EnrolmentApiService
import ish.oncourse.server.api.service.EntityApiService
import ish.oncourse.server.api.service.EntityRelationTypeApiService
import ish.oncourse.server.api.service.ExportTemplateApiService
import ish.oncourse.server.api.service.FacultyApiService
import ish.oncourse.server.api.service.GradingApiService
import ish.oncourse.server.api.service.ImportApiService
import ish.oncourse.server.api.service.InvoiceApiService
import ish.oncourse.server.api.service.LeadApiService
import ish.oncourse.server.api.service.LogsApiService
import ish.oncourse.server.api.service.MembershipProductApiService
import ish.oncourse.server.api.service.MessageApiService
import ish.oncourse.server.api.service.ModuleApiService
import ish.oncourse.server.api.service.NoteApiService
import ish.oncourse.server.api.service.OutcomeApiService
import ish.oncourse.server.api.service.PayslipApiService
import ish.oncourse.server.api.service.PortalWebsiteService
import ish.oncourse.server.api.service.PriorLearningApiService
import ish.oncourse.server.api.service.QualificationApiService
import ish.oncourse.server.api.service.ReportApiService
import ish.oncourse.server.api.service.RoomApiService
import ish.oncourse.server.api.service.ScriptApiService
import ish.oncourse.server.api.service.SiteApiService
import ish.oncourse.server.api.service.SurveyApiService
import ish.oncourse.server.api.service.TutorRoleApiService
import ish.oncourse.server.api.service.VoucherProductApiService
import ish.oncourse.server.api.service.WaitingListApiService
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.checkout.CheckoutApiService
import ish.oncourse.server.checkout.gateway.eway.test.EWayTestPaymentAPI
import ish.oncourse.server.checkout.gateway.eway.test.EWayTestPaymentService
import ish.oncourse.server.checkout.gateway.offline.OfflinePaymentService
import ish.oncourse.server.dashboard.ClassSearchService
import ish.oncourse.server.dashboard.ContactSearchService
import ish.oncourse.server.dashboard.CourseSearchService
import ish.oncourse.server.dashboard.DashboardSearchManager
import ish.oncourse.server.dashboard.EnrolmentSearchService
import ish.oncourse.server.dashboard.InvoiceSearchService
import ish.oncourse.server.checkout.gateway.eway.EWayPaymentAPI
import ish.oncourse.server.checkout.gateway.eway.EWayPaymentService
import ish.oncourse.server.checkout.gateway.windcave.WindcavePaymentAPI
import ish.oncourse.server.checkout.gateway.windcave.WindcavePaymentService


class ApiServiceModule implements Module {

    @Override
    void configure(Binder binder) {

        binder.bind(ClassSearchService).in(Scopes.SINGLETON)
        binder.bind(ContactSearchService).in(Scopes.SINGLETON)
        binder.bind(CourseSearchService).in(Scopes.SINGLETON)
        binder.bind(EnrolmentSearchService).in(Scopes.SINGLETON)
        binder.bind(InvoiceSearchService).in(Scopes.SINGLETON)
        binder.bind(DashboardSearchManager).in(Scopes.SINGLETON)

        binder.bind(EntityValidator).in(Scopes.SINGLETON)

        def entityServicesBinder = Multibinder.newSetBinder(binder, EntityApiService.class)
        entityServicesBinder.addBinding().to(ApplicationApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(ArticleProductApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(AssessmentApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(AssessmentSubmissionApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(ContactApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(CourseApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(CourseClassApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(DocumentApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(EnrolmentApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(ExportTemplateApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(GradingApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(ImportApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(InvoiceApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(LeadApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(MembershipProductApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(MessageApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(ModuleApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(NoteApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(OutcomeApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(PayslipApiService).in(Scopes.NO_SCOPE)
        entityServicesBinder.addBinding().to(RoomApiService).in(Scopes.NO_SCOPE)
        entityServicesBinder.addBinding().to(SiteApiService).in(Scopes.NO_SCOPE)
        entityServicesBinder.addBinding().to(VoucherProductApiService).in(Scopes.SINGLETON)
        entityServicesBinder.addBinding().to(WaitingListApiService).in(Scopes.NO_SCOPE)
        entityServicesBinder.addBinding().to(FacultyApiService).in(Scopes.SINGLETON)

        binder.bind(AccountApiService).in(Scopes.SINGLETON)
        binder.bind(ApiTokenApiService).in(Scopes.NO_SCOPE)
        binder.bind(CertificateApiService).in(Scopes.SINGLETON)
        binder.bind(ClassCostApiService).in(Scopes.SINGLETON)
        binder.bind(CourseClassTutorApiService).in(Scopes.SINGLETON)
        binder.bind(EmailTemplateApiService).in(Scopes.SINGLETON)
        binder.bind(EntityRelationTypeApiService).in(Scopes.NO_SCOPE)
        binder.bind(ExportTemplateApiService).in(Scopes.SINGLETON)
        binder.bind(FacultyApiService).in(Scopes.SINGLETON)
        binder.bind(GradingApiService).in(Scopes.SINGLETON)
        binder.bind(ImportApiService).in(Scopes.SINGLETON)
        binder.bind(InvoiceApiService).in(Scopes.SINGLETON)
        binder.bind(LeadApiService).in(Scopes.SINGLETON)
        binder.bind(LogsApiService).in(Scopes.SINGLETON)
        binder.bind(CheckoutApiService).in(Scopes.SINGLETON)
        binder.bind(EWayPaymentAPI).in(Scopes.SINGLETON)
        binder.bind(EWayPaymentService).in(Scopes.SINGLETON)
        binder.bind(EWayTestPaymentAPI).in(Scopes.SINGLETON)
        binder.bind(EWayTestPaymentService).in(Scopes.SINGLETON)
        binder.bind(OfflinePaymentService).in(Scopes.SINGLETON)
        binder.bind(WindcavePaymentAPI).in(Scopes.SINGLETON)
        binder.bind(WindcavePaymentService).in(Scopes.SINGLETON)
        binder.bind(PriorLearningApiService).in(Scopes.SINGLETON)
        binder.bind(QualificationApiService).in(Scopes.SINGLETON)
        binder.bind(ReportApiService).in(Scopes.SINGLETON)
        binder.bind(RoomApiService).in(Scopes.NO_SCOPE)
        binder.bind(ScriptApiService).in(Scopes.SINGLETON)
        binder.bind(SurveyApiService).in(Scopes.SINGLETON)
        binder.bind(TutorRoleApiService).in(Scopes.SINGLETON)
        binder.bind(BulkChangeApiService).in(Scopes.SINGLETON)
        binder.bind(VoucherProductApiService).in(Scopes.SINGLETON)
        binder.bind(WaitingListApiService).in(Scopes.NO_SCOPE)
        binder.bind(PortalWebsiteService).in(Scopes.SINGLETON)

    }
}

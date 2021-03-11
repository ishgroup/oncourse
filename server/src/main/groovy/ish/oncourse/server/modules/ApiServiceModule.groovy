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
import ish.oncourse.server.api.service.ApiTokenApiService
import ish.oncourse.server.api.service.ApplicationApiService
import ish.oncourse.server.api.service.ArticleProductApiService
import ish.oncourse.server.api.service.AssessmentApiService
import ish.oncourse.server.api.service.AssessmentSubmissionApiService
import ish.oncourse.server.api.service.CertificateApiService
import ish.oncourse.server.api.service.ClassCostApiService
import ish.oncourse.server.api.service.ContactApiService
import ish.oncourse.server.api.service.CourseApiService
import ish.oncourse.server.api.service.CourseClassApiService
import ish.oncourse.server.api.service.CourseClassTutorApiService
import ish.oncourse.server.api.service.DocumentApiService
import ish.oncourse.server.api.service.EmailTemplateApiService
import ish.oncourse.server.api.service.EnrolmentApiService
import ish.oncourse.server.api.service.EntityRelationTypeApiService
import ish.oncourse.server.api.service.ExportTemplateApiService
import ish.oncourse.server.api.service.ImportApiService
import ish.oncourse.server.api.service.InvoiceApiService
import ish.oncourse.server.api.service.MembershipProductApiService
import ish.oncourse.server.api.service.MessageApiService
import ish.oncourse.server.api.service.NoteApiService
import ish.oncourse.server.api.service.OutcomeApiService
import ish.oncourse.server.api.service.PayslipApiService
import ish.oncourse.server.api.service.PriorLearningApiService
import ish.oncourse.server.api.service.ReportApiService
import ish.oncourse.server.api.service.RoomApiService
import ish.oncourse.server.api.service.ScriptApiService
import ish.oncourse.server.api.service.SiteApiService
import ish.oncourse.server.api.service.SurveyApiService
import ish.oncourse.server.api.service.TutorRoleApiService
import ish.oncourse.server.api.service.VoucherProductApiService
import ish.oncourse.server.api.service.WaitingListApiService
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.server.concurrent.ExecutorManager
import ish.oncourse.server.dashboard.ClassSearchService
import ish.oncourse.server.dashboard.ContactSearchService
import ish.oncourse.server.dashboard.CourseSearchService
import ish.oncourse.server.dashboard.DashboardSearchManager
import ish.oncourse.server.dashboard.EnrolmentSearchService
import ish.oncourse.server.dashboard.InvoiceSearchService
import ish.oncourse.server.windcave.PaymentService

class ApiServiceModule implements Module {

    @Override
    void configure(Binder binder) {
        binder.bind(ExecutorManager).in(Scopes.SINGLETON)

        binder.bind(ClassSearchService).in(Scopes.SINGLETON)
        binder.bind(ContactSearchService).in(Scopes.SINGLETON)
        binder.bind(CourseSearchService).in(Scopes.SINGLETON)
        binder.bind(EnrolmentSearchService).in(Scopes.SINGLETON)
        binder.bind(InvoiceSearchService).in(Scopes.SINGLETON)
        binder.bind(DashboardSearchManager).in(Scopes.SINGLETON)

        binder.bind(EntityValidator).in(Scopes.SINGLETON)

        binder.bind(ApplicationApiService).in(Scopes.SINGLETON)
        binder.bind(ArticleProductApiService).in(Scopes.SINGLETON)
        binder.bind(AssessmentApiService).in(Scopes.SINGLETON)
        binder.bind(AssessmentSubmissionApiService).in(Scopes.SINGLETON)
        binder.bind(CertificateApiService).in(Scopes.SINGLETON)
        binder.bind(ClassCostApiService).in(Scopes.SINGLETON)
        binder.bind(ContactApiService).in(Scopes.SINGLETON)
        binder.bind(CourseApiService).in(Scopes.SINGLETON)
        binder.bind(CourseClassApiService).in(Scopes.SINGLETON)
        binder.bind(CourseClassTutorApiService).in(Scopes.SINGLETON)
        binder.bind(DocumentApiService).in(Scopes.SINGLETON)
        binder.bind(EmailTemplateApiService).in(Scopes.SINGLETON)
        binder.bind(EnrolmentApiService).in(Scopes.SINGLETON)
        binder.bind(EntityRelationTypeApiService).in(Scopes.NO_SCOPE)
        binder.bind(ExportTemplateApiService).in(Scopes.SINGLETON)
        binder.bind(ImportApiService).in(Scopes.SINGLETON)
        binder.bind(InvoiceApiService).in(Scopes.SINGLETON)
        binder.bind(MembershipProductApiService).in(Scopes.SINGLETON)
        binder.bind(MessageApiService).in(Scopes.SINGLETON)
        binder.bind(NoteApiService).in(Scopes.SINGLETON)
        binder.bind(OutcomeApiService).in(Scopes.SINGLETON)
        binder.bind(PayslipApiService).in(Scopes.NO_SCOPE)
        binder.bind(PaymentService).in(Scopes.NO_SCOPE)
        binder.bind(PriorLearningApiService).in(Scopes.SINGLETON)
        binder.bind(ReportApiService).in(Scopes.SINGLETON)
        binder.bind(RoomApiService).in(Scopes.NO_SCOPE)
        binder.bind(ScriptApiService).in(Scopes.SINGLETON)
        binder.bind(SiteApiService).in(Scopes.NO_SCOPE)
        binder.bind(SurveyApiService).in(Scopes.SINGLETON)
        binder.bind(TutorRoleApiService).in(Scopes.SINGLETON)
        binder.bind(VoucherProductApiService).in(Scopes.SINGLETON)
        binder.bind(WaitingListApiService).in(Scopes.NO_SCOPE)
        binder.bind(ApiTokenApiService).in(Scopes.NO_SCOPE)

    }
}

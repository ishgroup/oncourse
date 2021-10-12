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
import ish.oncourse.server.api.service.GradingApiService
import ish.oncourse.server.api.service.ImportApiService
import ish.oncourse.server.api.service.InvoiceApiService
import ish.oncourse.server.api.service.LeadApiService
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
import ish.oncourse.server.concurrent.ExecutorManager
import ish.oncourse.server.dashboard.ClassSearchService
import ish.oncourse.server.dashboard.ContactSearchService
import ish.oncourse.server.dashboard.CourseSearchService
import ish.oncourse.server.dashboard.DashboardSearchManager
import ish.oncourse.server.dashboard.EnrolmentSearchService
import ish.oncourse.server.dashboard.InvoiceSearchService
import ish.oncourse.server.windcave.PaymentService

class ApiServiceModule implements BQModule {

    @Override
    void configure(Binder binder) {
        binder.bind(ExecutorManager).inSingletonScope()

        binder.bind(ClassSearchService).inSingletonScope()
        binder.bind(ContactSearchService).inSingletonScope()
        binder.bind(CourseSearchService).inSingletonScope()
        binder.bind(EnrolmentSearchService).inSingletonScope()
        binder.bind(InvoiceSearchService).inSingletonScope()
        binder.bind(DashboardSearchManager).inSingletonScope()

        binder.bind(EntityValidator).inSingletonScope()

        binder.bind(ApiTokenApiService).withoutScope()
        binder.bind(ApplicationApiService).inSingletonScope()
        binder.bind(ArticleProductApiService).inSingletonScope()
        binder.bind(AssessmentApiService).inSingletonScope()
        binder.bind(AssessmentSubmissionApiService).inSingletonScope()
        binder.bind(CertificateApiService).inSingletonScope()
        binder.bind(ClassCostApiService).inSingletonScope()
        binder.bind(ContactApiService).inSingletonScope()
        binder.bind(CourseApiService).inSingletonScope()
        binder.bind(CourseClassApiService).inSingletonScope()
        binder.bind(CourseClassTutorApiService).inSingletonScope()
        binder.bind(DocumentApiService).inSingletonScope()
        binder.bind(EmailTemplateApiService).inSingletonScope()
        binder.bind(EnrolmentApiService).inSingletonScope()
        binder.bind(EntityRelationTypeApiService).withoutScope()
        binder.bind(ExportTemplateApiService).inSingletonScope()
        binder.bind(GradingApiService).inSingletonScope()
        binder.bind(ImportApiService).inSingletonScope()
        binder.bind(InvoiceApiService).inSingletonScope()
        binder.bind(LeadApiService).inSingletonScope()
        binder.bind(MembershipProductApiService).inSingletonScope()
        binder.bind(MessageApiService).inSingletonScope()
        binder.bind(NoteApiService).inSingletonScope()
        binder.bind(OutcomeApiService).inSingletonScope()
        binder.bind(PayslipApiService).withoutScope()
        binder.bind(PaymentService).withoutScope()
        binder.bind(PriorLearningApiService).inSingletonScope()
        binder.bind(ReportApiService).inSingletonScope()
        binder.bind(RoomApiService).withoutScope()
        binder.bind(ScriptApiService).inSingletonScope()
        binder.bind(SiteApiService).withoutScope()
        binder.bind(SurveyApiService).inSingletonScope()
        binder.bind(TutorRoleApiService).inSingletonScope()
        binder.bind(VoucherProductApiService).inSingletonScope()
        binder.bind(WaitingListApiService).withoutScope()

    }
}

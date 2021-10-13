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
import ish.oncourse.server.api.service.*
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.concurrent.ExecutorManager
import ish.oncourse.server.dashboard.*
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

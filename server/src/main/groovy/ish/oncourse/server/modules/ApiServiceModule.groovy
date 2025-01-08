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

import io.bootique.di.BQModule
import io.bootique.di.Binder
import ish.oncourse.server.api.service.*
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.checkout.CheckoutApiService
import ish.oncourse.server.checkout.gateway.eway.EWayPaymentAPI
import ish.oncourse.server.checkout.gateway.eway.EWayPaymentService
import ish.oncourse.server.checkout.gateway.eway.test.EWayTestPaymentAPI
import ish.oncourse.server.checkout.gateway.eway.test.EWayTestPaymentService
import ish.oncourse.server.checkout.gateway.offline.OfflinePaymentService
import ish.oncourse.server.checkout.gateway.windcave.WindcavePaymentAPI
import ish.oncourse.server.checkout.gateway.windcave.WindcavePaymentService
import ish.oncourse.server.concurrent.ExecutorManager
import ish.oncourse.server.dashboard.*

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

        def entityServicesBinder = binder.bindSet(EntityApiService.class)
        entityServicesBinder.add(ApplicationApiService).inSingletonScope()
        entityServicesBinder.add(ArticleProductApiService).inSingletonScope()
        entityServicesBinder.add(AssessmentApiService).inSingletonScope()
        entityServicesBinder.add(AssessmentSubmissionApiService).inSingletonScope()
        entityServicesBinder.add(ContactApiService).inSingletonScope()
        entityServicesBinder.add(CourseApiService).inSingletonScope()
        entityServicesBinder.add(CourseClassApiService).inSingletonScope()
        entityServicesBinder.add(DocumentApiService).inSingletonScope()
        entityServicesBinder.add(EnrolmentApiService).inSingletonScope()
        entityServicesBinder.add(ExportTemplateApiService).inSingletonScope()
        entityServicesBinder.add(GradingApiService).inSingletonScope()
        entityServicesBinder.add(ImportApiService).inSingletonScope()
        entityServicesBinder.add(InvoiceApiService).inSingletonScope()
        entityServicesBinder.add(LeadApiService).inSingletonScope()
        entityServicesBinder.add(MembershipProductApiService).inSingletonScope()
        entityServicesBinder.add(MessageApiService).inSingletonScope()
        entityServicesBinder.add(ModuleApiService).inSingletonScope()
        entityServicesBinder.add(NoteApiService).inSingletonScope()
        entityServicesBinder.add(OutcomeApiService).inSingletonScope()
        entityServicesBinder.add(PayslipApiService)
        entityServicesBinder.add(RoomApiService)
        entityServicesBinder.add(SiteApiService)
        entityServicesBinder.add(VoucherProductApiService).inSingletonScope()
        entityServicesBinder.add(WaitingListApiService)
        entityServicesBinder.add(FacultyApiService).inSingletonScope()

        binder.bind(AccountApiService).inSingletonScope()
        binder.bind(ApiTokenApiService)
        binder.bind(CertificateApiService).inSingletonScope()
        binder.bind(ClassCostApiService).inSingletonScope()
        binder.bind(CourseClassTutorApiService).inSingletonScope()
        binder.bind(EmailTemplateApiService).inSingletonScope()
        binder.bind(EntityRelationTypeApiService)
        binder.bind(ExportTemplateApiService).inSingletonScope()
        binder.bind(FacultyApiService).inSingletonScope()
        binder.bind(GradingApiService).inSingletonScope()
        binder.bind(ImportApiService).inSingletonScope()
        binder.bind(InvoiceApiService).inSingletonScope()
        binder.bind(LeadApiService).inSingletonScope()
        binder.bind(LogsApiService).inSingletonScope()
        binder.bind(CheckoutApiService).inSingletonScope()
        binder.bind(EWayPaymentAPI).inSingletonScope()
        binder.bind(EWayPaymentService).inSingletonScope()
        binder.bind(EWayTestPaymentAPI).inSingletonScope()
        binder.bind(EWayTestPaymentService).inSingletonScope()
        binder.bind(OfflinePaymentService).inSingletonScope()
        binder.bind(WindcavePaymentAPI).inSingletonScope()
        binder.bind(WindcavePaymentService).inSingletonScope()
        binder.bind(PriorLearningApiService).inSingletonScope()
        binder.bind(QualificationApiService).inSingletonScope()
        binder.bind(ReportApiService).inSingletonScope()
        binder.bind(RoomApiService)
        binder.bind(ScriptApiService).inSingletonScope()
        binder.bind(SurveyApiService).inSingletonScope()
        binder.bind(TutorRoleApiService).inSingletonScope()
        binder.bind(BulkChangeApiService).inSingletonScope()
        binder.bind(VoucherProductApiService).inSingletonScope()
        binder.bind(WaitingListApiService)
        binder.bind(PortalWebsiteService).inSingletonScope()

    }
}

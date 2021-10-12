/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.modules

import groovy.transform.CompileStatic
import io.bootique.di.BQModule
import io.bootique.di.Binder
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.impl.AntlrAqlService
import ish.oncourse.entity.services.*
import ish.oncourse.server.AuditListener
import ish.oncourse.server.CayenneListenersService
import ish.oncourse.server.accounting.AccountTransactionService
import ish.oncourse.server.api.v1.function.SessionValidator
import ish.oncourse.server.db.SanityCheckService
import ish.oncourse.server.deduplication.ContactMergeService
import ish.oncourse.server.duplicate.DuplicateClassService
import ish.oncourse.server.duplicate.DuplicateCourseService
import ish.oncourse.server.entity.mixins.MixinHelper
import ish.oncourse.server.export.ExportService
import ish.oncourse.server.imports.ImportService
import ish.oncourse.server.lifecycle.InvoiceLineInitHelper
import ish.oncourse.server.messaging.MessageService
import ish.oncourse.server.quality.QualityService
import ish.oncourse.server.querying.QueryService
import ish.oncourse.server.report.IReportService
import ish.oncourse.server.report.ReportService
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.services.*
import ish.oncourse.server.upgrades.DataPopulation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.mockito.Mockito

@CompileStatic
class TestModule implements BQModule {

    private static final Logger logger = LogManager.getLogger()


    @Override
    void configure(Binder binder) {

        binder.bind(IReportService).to(ReportService).inSingletonScope()


        binder.bind(AccountTransactionService).inSingletonScope()
        binder.bind(AuditService).inSingletonScope()
        binder.bind(AuditListener).toInstance(Mockito.mock(AuditListener))

        binder.bind(MixinHelper).initOnStartup()

        binder.bind(InvoiceLineInitHelper)
        binder.bind(AqlService).to(AntlrAqlService)
        binder.bind(QueryService).inSingletonScope()
        binder.bind(GroovyScriptService).inSingletonScope()
        binder.bind(MessageService).inSingletonScope()

        // dummy scheduler service for tests
        binder.bind(ISchedulerService).toInstance(new TestSchedulerService())

        // entity service classes
        binder.bind(CourseClassService).inSingletonScope()
        binder.bind(StudentConcessionService).inSingletonScope()
        binder.bind(TagService).inSingletonScope()
        binder.bind(SessionService).inSingletonScope()
        binder.bind(CourseService).inSingletonScope()
        binder.bind(CertificateService).inSingletonScope()
        binder.bind(StudentService).inSingletonScope()
        binder.bind(InvoiceLineService).inSingletonScope()
        binder.bind(IAutoIncrementService).to(TestAutoIncrementService).inSingletonScope()
        binder.bind(CertificateServiceTest).inSingletonScope()
        binder.bind(ISystemUserService).to(TestSystemUserService).inSingletonScope()

        binder.bind(CustomFieldTypeService).inSingletonScope()
        binder.bind(SanityCheckService).inSingletonScope()
        binder.bind(QualityService).inSingletonScope()
        binder.bind(ExportService).inSingletonScope()
        binder.bind(DuplicateCourseService).inSingletonScope()
        binder.bind(DuplicateClassService).inSingletonScope()

        binder.bind(DataPopulation).inSingletonScope()
        binder.bind(ImportService).inSingletonScope()
        binder.bind(ContactMergeService).inSingletonScope()
        binder.bind(SessionValidator).inSingletonScope()

        binder.bind(CayenneListenersService).initOnStartup()

    }
}

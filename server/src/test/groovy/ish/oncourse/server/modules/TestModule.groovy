/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.modules

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Scopes
import groovy.transform.CompileStatic
import ish.oncourse.GoogleGuiceInjector
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
class TestModule implements Module {

    private static final Logger logger = LogManager.getLogger()

    
    @Override
    void configure(Binder binder) {

        binder.bind(IReportService.class).to(ReportService.class).in(Scopes.SINGLETON)


        binder.bind(AccountTransactionService.class).in(Scopes.SINGLETON)
        binder.bind(AuditService.class).in(Scopes.SINGLETON)
        binder.bind(AuditListener.class).toInstance(Mockito.mock(AuditListener.class))

        binder.bind(MixinHelper.class).asEagerSingleton()

        binder.bind(InvoiceLineInitHelper.class)
        binder.bind(AqlService.class).to(AntlrAqlService.class)
        binder.bind(QueryService.class).in(Scopes.SINGLETON)
        binder.bind(GroovyScriptService.class).in(Scopes.SINGLETON)
        binder.bind(MessageService.class).in(Scopes.SINGLETON)

        // dummy scheduler service for tests
        binder.bind(ISchedulerService.class).toInstance(new TestSchedulerService())

        binder.bind(GoogleGuiceInjector.class).asEagerSingleton()

        // entity service classes
        binder.bind(ContactService.class).in(Scopes.SINGLETON)
        binder.bind(CourseClassService.class).in(Scopes.SINGLETON)
        binder.bind(StudentConcessionService.class).in(Scopes.SINGLETON)
        binder.bind(TagService.class).in(Scopes.SINGLETON)
        binder.bind(SessionService.class).in(Scopes.SINGLETON)
        binder.bind(OutcomeService.class).in(Scopes.SINGLETON)
        binder.bind(CourseService.class).in(Scopes.SINGLETON)
        binder.bind(CertificateService.class).in(Scopes.SINGLETON)
        binder.bind(StudentService.class).in(Scopes.SINGLETON)
        binder.bind(InvoiceLineService.class).in(Scopes.SINGLETON)
        binder.bind(IAutoIncrementService.class).to(TestAutoIncrementService.class).in(Scopes.SINGLETON)
        binder.bind(CertificateServiceTest.class).in(Scopes.SINGLETON)
        binder.bind(ISystemUserService.class).to(TestSystemUserService.class).in(Scopes.SINGLETON)

        binder.bind(CustomFieldTypeService.class).in(Scopes.SINGLETON)
        binder.bind(SanityCheckService.class).in(Scopes.SINGLETON)
        binder.bind(QualityService.class).in(Scopes.SINGLETON)
        binder.bind(ExportService.class).in(Scopes.SINGLETON)
        binder.bind(DuplicateCourseService.class).in(Scopes.SINGLETON)
        binder.bind(DuplicateClassService.class).in(Scopes.SINGLETON)

        binder.bind(DataPopulation.class).in(Scopes.SINGLETON)
        binder.bind(ImportService.class).in(Scopes.SINGLETON)
        binder.bind(ContactMergeService.class).in(Scopes.SINGLETON)
        binder.bind(SessionValidator.class).in(Scopes.SINGLETON)

        binder.bind(CayenneListenersService.class).asEagerSingleton();

    }
}

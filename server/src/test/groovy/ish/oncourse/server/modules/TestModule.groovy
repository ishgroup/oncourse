package ish.oncourse.server.modules

import groovy.transform.CompileStatic
import io.bootique.di.BQModule
import io.bootique.di.Binder
import ish.oncourse.BootiqueInjector
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
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.services.*
import ish.oncourse.server.upgrades.DataPopulation
import org.mockito.Mockito

@CompileStatic
class TestModule implements BQModule {

    @Override
    void configure(Binder binder) {

        binder.bind(AccountTransactionService.class).inSingletonScope()
        binder.bind(AuditService.class).inSingletonScope()
        binder.bind(AuditListener.class).toInstance(Mockito.mock(AuditListener.class))

        binder.bind(MixinHelper.class).initOnStartup()

        binder.bind(InvoiceLineInitHelper.class)
        binder.bind(AqlService.class).to(AntlrAqlService.class)
        binder.bind(QueryService.class).inSingletonScope()
        binder.bind(GroovyScriptService.class).inSingletonScope()
        binder.bind(MessageService.class).inSingletonScope()

        // dummy scheduler service for tests
        binder.bind(ISchedulerService.class).toInstance(new TestSchedulerService())

        binder.bind(BootiqueInjector.class).initOnStartup()

        // entity service classes
        binder.bind(CourseClassService.class).inSingletonScope()
        binder.bind(StudentConcessionService.class).inSingletonScope()
        binder.bind(TagService.class).inSingletonScope()
        binder.bind(SessionService.class).inSingletonScope()
        binder.bind(CourseService.class).inSingletonScope()
        binder.bind(CertificateService.class).inSingletonScope()
        binder.bind(StudentService.class).inSingletonScope()
        binder.bind(InvoiceLineService.class).inSingletonScope()
        binder.bind(IAutoIncrementService.class).to(TestAutoIncrementService.class).inSingletonScope()
        binder.bind(CertificateServiceTest.class).inSingletonScope()
        binder.bind(ISystemUserService.class).to(TestSystemUserService.class).inSingletonScope()

        binder.bind(CustomFieldTypeService.class).inSingletonScope()
        binder.bind(SanityCheckService.class).inSingletonScope()
        binder.bind(QualityService.class).inSingletonScope()
        binder.bind(ExportService.class).inSingletonScope()
        binder.bind(DuplicateCourseService.class).inSingletonScope()
        binder.bind(DuplicateClassService.class).inSingletonScope()

        binder.bind(DataPopulation.class).inSingletonScope()
        binder.bind(ImportService.class).inSingletonScope()
        binder.bind(ContactMergeService.class).inSingletonScope()
        binder.bind(SessionValidator.class).inSingletonScope()

        binder.bind(CayenneListenersService.class).initOnStartup()

    }
}

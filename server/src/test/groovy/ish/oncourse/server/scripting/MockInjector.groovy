package ish.oncourse.server.scripting

import io.bootique.BQRuntime
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.export.ExportService
import ish.oncourse.server.imports.ImportService
import ish.oncourse.server.messaging.MessageService
import ish.oncourse.server.print.PrintService
import ish.oncourse.server.scripting.api.CollegePreferenceService
import ish.oncourse.server.scripting.api.EmailService
import ish.oncourse.server.scripting.api.TemplateService
import ish.oncourse.server.services.ISchedulerService
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Created by akoiro on 7/04/2016.
 */
class MockInjector {
    @Mock
    BQRuntime injector

    @Mock
    ICayenneService cayenneService

    @Mock
    ISchedulerService schedulerService

    @Mock
    PreferenceController preferenceController

    @Mock
    EmailService emailService

    @Mock
    CollegePreferenceService preferenceService

    @Mock
    ExportService exportService

    @Mock
    ImportService importService

    @Mock
    PrintService printService

    @Mock
    MessageService messageService

    @Mock
    DocumentService documentService

    @Mock
    TemplateService templateService

    void init() {
        MockitoAnnotations.initMocks(this)

        Mockito.when(injector.getInstance(EmailService.class)).thenReturn(emailService)
        Mockito.when(injector.getInstance(CollegePreferenceService.class)).thenReturn(preferenceService)
        Mockito.when(injector.getInstance(ExportService.class)).thenReturn(exportService)
        Mockito.when(injector.getInstance(ImportService.class)).thenReturn(importService)
        Mockito.when(injector.getInstance(PrintService.class)).thenReturn(printService)
        Mockito.when(injector.getInstance(MessageService.class)).thenReturn(messageService)
        Mockito.when(injector.getInstance(DocumentService.class)).thenReturn(documentService)
        Mockito.when()
    }
}

package ish.oncourse.server.messaging


import groovy.transform.CompileStatic
import ish.oncourse.server.scripting.api.TemplateService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@CompileStatic
class GetSubjectTest {

    private static final String SUBJECT_RESULT = 'this is a test subject'

    @Test
    void testGetSubjectTestFunction() {
        String subject = 'test subject'

        GetSubject getSubject1 = GetSubject.valueOf(subject)
        Assertions.assertEquals(subject, getSubject1.get())

        GetSubject getSubject2 = GetSubject.valueOf(null, null, null, subject)
        Assertions.assertEquals(subject, getSubject2.get())

        GetSubject getSubject3 = GetSubject.valueOf(templateService, 'test', Collections.emptyMap(), subject)
        Assertions.assertEquals(SUBJECT_RESULT, getSubject3.get())
    }

    private static TemplateService getTemplateService() {
        TemplateService service = Mockito.mock(TemplateService)
        Mockito.when(service.renderSubject(Mockito.isA(String), Mockito.isA(Map))).thenReturn(SUBJECT_RESULT)
        service
    }
}

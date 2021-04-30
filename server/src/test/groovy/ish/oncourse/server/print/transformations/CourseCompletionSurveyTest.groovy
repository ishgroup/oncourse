package ish.oncourse.server.print.transformations

import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.print.PrintWorker
import ish.oncourse.server.print.ReportDataSource
import ish.print.PrintRequest
import net.sf.jasperreports.engine.*
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.ExporterInput
import net.sf.jasperreports.export.OutputStreamExporterOutput
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import org.apache.cayenne.access.DataContext
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.Matchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class CourseCompletionSurveyTest {

    private Report report
    Report subreport
    private PrintWorker printWorker
    ICayenneService cayenneService

    @BeforeEach
    void before () throws IOException {
        InputStream is = CourseCompletionSurveyTest.class.getClassLoader().getResourceAsStream("reports/CourseClass/CourseCompletionSurvey.jrxml")
        report = mock(Report.class)
        when(report.getData()).thenReturn(IOUtils.toByteArray(is))

        is = CourseCompletionSurveyTest.class.getClassLoader().getResourceAsStream("reports/CourseClass/subreports/CourseCompletionSurveySubreport.jrxml")
        subreport = mock(Report.class)
        when(subreport.getData()).thenReturn(IOUtils.toByteArray(is))

        PrintRequest printRequest = mock(PrintRequest.class)
        when(printRequest.getValueForKey("reportTitle")).thenReturn("Course Completion Survey")
        printWorker = mock(PrintWorker.class)
        when(printWorker.getPrintRequest()).thenReturn(printRequest)
        cayenneService = mock(ICayenneService.class)
        when(printWorker.getCayenneService()).thenReturn(cayenneService)
        DataContext context = mock(DataContext.class)
        when(cayenneService.getSharedContext()).thenReturn(context)
        when(context.select(any())).thenReturn(Arrays.asList(subreport))
    }

    @Test
    void test() throws JRException, FileNotFoundException {
        ReportDataSource reportDataSource = new ReportDataSource(printWorker, report, getItems())
        JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(report.getData()))
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), reportDataSource)

        ExporterInput input = SimpleExporterInput.getInstance(Collections.singletonList(jasperPrint))
        OutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(new FileOutputStream("TestCourseCompletionSurvey.pdf"))

        JRPdfExporter exporter = new JRPdfExporter()
        exporter.setExporterInput(input)
        exporter.setExporterOutput(output)
        exporter.exportReport()
    }

    private List<CourseClass> getItems(){
        List<CourseClass> classes = new ArrayList<>()

        for (int i = 0; i < 4; i++) {
            classes.add(getItem(i))
        }

        return classes
    }

    CourseClass getItem(int i) {
        CourseClass courseClass = mock(CourseClass.class)
        Room room = mock(Room.class)
        Site site = mock(Site.class)
        Course course = mock(Course.class)
        CourseClassTutor courseClassTutor1 = mock(CourseClassTutor.class)
        CourseClassTutor courseClassTutor2 = mock(CourseClassTutor.class)
        Tutor tutor1 = mock(Tutor.class)
        Tutor tutor2 = mock(Tutor.class)
        Contact contact1 = mock(Contact.class)
        Contact contact2 = mock(Contact.class)
        Enrolment enrolment1 = mock(Enrolment.class)
        Enrolment enrolment2 = mock(Enrolment.class)
        Enrolment enrolment3 = mock(Enrolment.class)
        Survey survey1 = mock(Survey.class)
        Survey survey2 = mock(Survey.class)
        Survey survey3 = mock(Survey.class)
        Survey survey4 = mock(Survey.class)

        //getting courseName
        when(courseClass.getCourse()).thenReturn(course)
        when(course.getName()).thenReturn("Diploma in Disability " + (i + 1))

        //getting courseClass start date
        when(courseClass.getStartDateTime()).thenReturn(new Date())

        //getting courseClass site name
        when(courseClass.getRoom()).thenReturn(room)
        when(room.getSite()).thenReturn(site)
        when(site.getName()).thenReturn(RandomStringUtils.random(6, true, false) + " Site")

        //getting course and class code
        when(courseClass.getCode()).thenReturn("ABC")
        when(course.getCode()).thenReturn(123 + i + "")

        //getting courseClass successful enrolments
        when(courseClass.getSuccessAndQueuedEnrolments()).thenReturn(Arrays.asList(enrolment1, enrolment2, enrolment3))

        //getting enrolments with surveys count
        when(survey1.getEnrolment()).thenReturn(enrolment1)
        when(survey2.getEnrolment()).thenReturn(enrolment1)
        when(survey3.getEnrolment()).thenReturn(enrolment2)
        when(survey4.getEnrolment()).thenReturn(enrolment2)

        //getting courseClass tutor full name
        when(courseClass.getTutorRoles()).thenReturn(Arrays.asList(courseClassTutor1, courseClassTutor2, courseClassTutor1))
        when(courseClassTutor1.getTutor()).thenReturn(tutor1)
        when(courseClassTutor2.getTutor()).thenReturn(tutor2)
        when(tutor1.getContact()).thenReturn(contact1)
        when(tutor2.getContact()).thenReturn(contact2)
        when(contact1.getFullName()).thenReturn("John Smith")
        when(contact2.getFullName()).thenReturn("Jane Smith")

        if (i != 3) {
            //getting surveys average venue score
            when(enrolment1.getSurveys()).thenReturn(Arrays.asList(survey1, survey2))
            when(enrolment2.getSurveys()).thenReturn(Arrays.asList(survey3, survey4))
            when(survey1.getVenueScore()).thenReturn(7)
            when(survey2.getVenueScore()).thenReturn(7)
            when(survey3.getVenueScore()).thenReturn(7)
            when(survey4.getVenueScore()).thenReturn(7 + i)

            //getting surveys average course score
            when(survey1.getCourseScore()).thenReturn(7)
            when(survey2.getCourseScore()).thenReturn(7)
            when(survey3.getCourseScore()).thenReturn(7)
            when(survey4.getCourseScore()).thenReturn(7 - i)

            //getting surveys average tutor score
            when(survey1.getTutorScore()).thenReturn(7)
            when(survey2.getTutorScore()).thenReturn(7)
            when(survey3.getTutorScore()).thenReturn(7)
            when(survey4.getTutorScore()).thenReturn(7 + i)
        }

        return courseClass
    }
}

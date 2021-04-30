package ish.oncourse.server.print.transformations

import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.print.PrintWorker
import ish.oncourse.server.print.ReportDataSource
import ish.print.PrintRequest
import net.sf.jasperreports.engine.*
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.ExporterInput
import net.sf.jasperreports.export.OutputStreamExporterOutput
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class CertificateAccendanceTest {

    private Report report
    private PrintWorker printWorker

    @BeforeEach
    void before () throws IOException {
        InputStream is = CertificateAccendanceTest.class.getClassLoader().getResourceAsStream("reports/Certificate/CertificateAttendanceReport.jrxml")
        report = mock(Report.class)
        when(report.getData()).thenReturn(IOUtils.toByteArray(is))

        PrintRequest printRequest = mock(PrintRequest.class)
        when(printRequest.getValueForKey("reportTitle")).thenReturn("Certificate of Attendance")
        printWorker = mock(PrintWorker.class)
        when(printWorker.getPrintRequest()).thenReturn(printRequest)
    }

    @Test
    void test() throws JRException, FileNotFoundException {
        ReportDataSource reportDataSource = new ReportDataSource(printWorker, report, getItem())
        JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(report.getData()))
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), reportDataSource)

        ExporterInput input = SimpleExporterInput.getInstance(Collections.singletonList(jasperPrint))
        OutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(new FileOutputStream("TestCertificateAttendanceReport.pdf"))

        JRPdfExporter exporter = new JRPdfExporter()
        exporter.setExporterInput(input)
        exporter.setExporterOutput(output)
        exporter.exportReport()
    }

    private List<Enrolment> getItem() {
        Enrolment enrolment = mock(Enrolment.class)
        CourseClass courseClass = mock(CourseClass.class)
        Course course = mock(Course.class)

        when(enrolment.getCourseClass()).thenReturn(courseClass)
        when(courseClass.getCourse()).thenReturn(course)
        when(course.getName()).thenReturn("Very long nameVery long nameVery long nameVery long nameVery long nameVery long nameVery long nameVery long nameVery long nameVery long nameVery long name")

        List<Enrolment> enrolments = new ArrayList<>()
        enrolments.add(enrolment)

        return enrolments
    }

}

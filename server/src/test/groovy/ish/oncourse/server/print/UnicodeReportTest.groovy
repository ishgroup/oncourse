package ish.oncourse.server.print

import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.print.transformations.ContactDataRowDelegator
import ish.print.PrintRequest
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.ExporterInput
import net.sf.jasperreports.export.OutputStreamExporterOutput
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import org.apache.cayenne.PersistentObject
import org.junit.Before
import org.junit.Test
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Created by anarut on 2/20/17.
 */
class UnicodeReportTest {

    private Report report
    private PrintWorker printWorker
    private PrintRequest printRequest

    @Before
    void before() {

        InputStream is = UnicodeReportTest.classLoader.getResourceAsStream("resources/oncourse-scripts/reports/unicodeReport.jrxml")
        report = mock(Report)
        when(report.data).thenReturn(is.text.bytes)

        printRequest = mock(PrintRequest)
        when(printRequest.getValueForKey("reportTitle")).thenReturn("Unicode test")
        printWorker = mock(PrintWorker)
        when(printWorker.printRequest).thenReturn(printRequest)
    }

    @Test
    void test() {

        ReportDataSource reportDataSource = new ReportDataSource(printWorker, report, getData())
        JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(report.getData()))
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<String, Object>(), reportDataSource)

        ExporterInput input = SimpleExporterInput.getInstance(Collections.singletonList(jasperPrint))
        OutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(new FileOutputStream("unicodeReport.pdf"))

        JRPdfExporter exporter = new JRPdfExporter()
        exporter.setExporterInput(input)
        exporter.setExporterOutput(output)
        exporter.exportReport()
    }

    private List<ContactDataRowDelegator> getData() {
        ArrayList<ContactDataRowDelegator> result = new ArrayList<>()
        for (int i = 0; i < 1; i++) {
            result.add(mock(PersistentObject))
        }
        return result
    }
}

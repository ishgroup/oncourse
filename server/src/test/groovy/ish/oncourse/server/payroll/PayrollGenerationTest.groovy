package ish.oncourse.server.payroll


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.PayslipPayType
import ish.oncourse.entity.services.SessionService
import ish.oncourse.server.CayenneService
import ish.oncourse.server.cayenne.Payslip
import ish.oncourse.server.cayenne.SessionTest
import ish.payroll.PayrollGenerationRequest
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class PayrollGenerationTest extends CayenneIshTestCase {

    private PayrollService payrollService
    private CayenneService cayenneService
    private SessionService sessionService

    private ObjectContext context

    
    @BeforeEach
    void setup() {
        wipeTables()
        InputStream st = SessionTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/payroll/payslipGenerationTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        executeDatabaseOperation(rDataSet)

        cayenneService = injector.getInstance(CayenneService.class)
        sessionService = injector.getInstance(SessionService.class)
        payrollService = new PayrollService(cayenneService, sessionService)
        context = cayenneService.newContext
    }

    
    @Test
    void generatePayslipWithPayType() {
        List<Long> contactIds = [1l]
        PayrollGenerationRequest request = new PayrollGenerationRequest().with { request ->
            request.ids = contactIds
            request.entityName = "Contact"
            request.until = (new Date() + 1).clearTime()
            request.confirm = Boolean.FALSE
            request
        }

        payrollService.generatePayslips(request)

        List<Payslip> payslips = ObjectSelect.query(Payslip).select(context)
        Assertions.assertEquals(payslips.size(), 1)

        Payslip payslip = payslips[0]
        Assertions.assertEquals(payslip.payType, PayslipPayType.CONTRACTOR, "Pay type should be equal to tutor pay type")
    }

    
    @Test
    void generatePayslipWithoutPayType() {
        List<Long> contactIds = [2l]
        PayrollGenerationRequest request = new PayrollGenerationRequest().with { request ->
            request.ids = contactIds
            request.entityName = "Contact"
            request.until = (new Date() + 1).clearTime()
            request.confirm = Boolean.FALSE
            request
        }

        payrollService.generatePayslips(request)

        List<Payslip> payslips = ObjectSelect.query(Payslip).select(context)
        Assertions.assertEquals(payslips.size(), 0, "There are no payslips should be created for tutor without pay type")
    }
}

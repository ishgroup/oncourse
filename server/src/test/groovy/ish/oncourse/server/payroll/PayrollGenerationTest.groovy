package ish.oncourse.server.payroll

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.PayslipPayType
import ish.oncourse.entity.services.SessionService
import ish.oncourse.server.cayenne.Payslip
import ish.payroll.PayrollGenerationRequest
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/payroll/payslipGenerationTest.xml")
class PayrollGenerationTest extends TestWithDatabase {

    private PayrollService payrollService
    private SessionService sessionService

    @BeforeEach
    void setup() {
        sessionService = injector.getInstance(SessionService.class)
        payrollService = new PayrollService(cayenneService, sessionService)
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

        List<Payslip> payslips = ObjectSelect.query(Payslip).select(cayenneContext)
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

        List<Payslip> payslips = ObjectSelect.query(Payslip).select(cayenneContext)
        Assertions.assertEquals(payslips.size(), 0, "There are no payslips should be created for tutor without pay type")
    }
}

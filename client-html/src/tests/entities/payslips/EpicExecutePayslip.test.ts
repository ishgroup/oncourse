import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { executePayslip, POST_PAYSLIP_EXECUTE_FULFILLED } from "../../../js/containers/entities/payslips/actions";
import { EpicExecutePayslip } from "../../../js/containers/entities/payslips/epics/EpicExecutePayslip";

describe("Execute payslip epic tests", () => {
  it("EpicExecutePayslip should returns correct values", () => DefaultEpic({
    action: executePayslip([2], "Completed"),
    epic: EpicExecutePayslip,
    processData: () => [
      {
        type: POST_PAYSLIP_EXECUTE_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Payslip status updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Payslip", listUpdate: true, savedID: 2 }
      }
    ]
  }));
});

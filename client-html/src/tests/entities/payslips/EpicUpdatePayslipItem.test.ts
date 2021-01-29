import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdatePayslipItem } from "../../../js/containers/entities/payslips/epics/EpicUpdatePayslipItem";
import {
  GET_PAYSLIP_ITEM,
  UPDATE_PAYSLIP_ITEM_FULFILLED,
  updatePayslip
} from "../../../js/containers/entities/payslips/actions";

describe("Update payslip epic tests", () => {
  it("EpicUpdatePayslipItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updatePayslip("1", mockedApi.db.getPayslip(1)),
    epic: EpicUpdatePayslipItem,
    processData: () => [
      {
        type: UPDATE_PAYSLIP_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Payslip Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Payslip", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_PAYSLIP_ITEM,
        payload: "1"
      }
    ]
  }));
});

import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeletePayslip } from "../../../js/containers/entities/payslips/epics/EpicDeletePayslip";
import { DELETE_PAYSLIP_ITEM_FULFILLED, removePayslip } from "../../../js/containers/entities/payslips/actions";

describe("Delete payslip epic tests", () => {
  it("EpicDeletePayslip should returns correct values", () => DefaultEpic({
    action: removePayslip("1"),
    epic: EpicDeletePayslip,
    processData: () => [
      {
        type: DELETE_PAYSLIP_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Payslip record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Payslip", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});

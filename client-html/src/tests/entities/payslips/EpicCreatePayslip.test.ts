import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreatePayslip } from "../../../js/containers/entities/payslips/epics/EpicCreatePayslip";
import { CREATE_PAYSLIP_ITEM_FULFILLED, createPayslip } from "../../../js/containers/entities/payslips/actions";

describe("Create payslip epic tests", () => {
  it("EpicCreatePayslip should returns correct values", () => DefaultEpic({
    action: mockedApi => createPayslip(mockedApi.db.getPayslip(1)),
    epic: EpicCreatePayslip,
    processData: () => [
      {
        type: CREATE_PAYSLIP_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Payslip Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Payslip" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});

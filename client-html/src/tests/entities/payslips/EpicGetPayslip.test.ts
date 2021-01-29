import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetPayslip } from "../../../js/containers/entities/payslips/epics/EpicGetPayslip";
import { GET_PAYSLIP_ITEM_FULFILLED, getPayslip } from "../../../js/containers/entities/payslips/actions";

describe("Get payslip epic tests", () => {
  it("EpicGetPayslip should returns correct values", () => DefaultEpic({
    action: getPayslip("1"),
    epic: EpicGetPayslip,
    processData: mockedApi => {
      const payslip = mockedApi.db.getPayslip(1);
      return [
        {
          type: GET_PAYSLIP_ITEM_FULFILLED,
          payload: { payslip }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: payslip, name: payslip.tutorFullName }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, payslip)
      ];
    }
  }));
});

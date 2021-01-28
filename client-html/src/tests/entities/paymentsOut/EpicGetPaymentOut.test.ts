import { initialize, reset } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetPaymentOut } from "../../../js/containers/entities/paymentsOut/epics/EpicGetPaymentOut";
import { GET_PAYMENT_OUT_ITEM_FULFILLED, getPaymentOut } from "../../../js/containers/entities/paymentsOut/actions";

describe("Get paymentOut epic tests", () => {
  it("EpicGetPaymentOut should returns correct values", () => DefaultEpic({
    action: getPaymentOut("1"),
    epic: EpicGetPaymentOut,
    processData: mockedApi => {
      const paymentOut = mockedApi.db.getPaymentOut(1);
      return [
        {
          type: GET_PAYMENT_OUT_ITEM_FULFILLED,
          payload: { paymentOut }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: paymentOut, name: paymentOut.payeeName }
        },
        reset(LIST_EDIT_VIEW_FORM_NAME),
        initialize(LIST_EDIT_VIEW_FORM_NAME, paymentOut)
      ];
    }
  }));
});

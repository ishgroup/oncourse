import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetPaymentIn } from "../../../js/containers/entities/paymentsIn/epics/EpicGetPaymentIn";
import { GET_PAYMENT_IN_ITEM_FULFILLED, getPaymentIn } from "../../../js/containers/entities/paymentsIn/actions";

describe("Get paymentIn epic tests", () => {
  it("EpicGetPaymentIn should returns correct values", () => DefaultEpic({
    action: getPaymentIn("1"),
    epic: EpicGetPaymentIn,
    processData: mockedApi => {
      const paymentIn = mockedApi.db.getPaymentIn(1);
      return [
        {
          type: GET_PAYMENT_IN_ITEM_FULFILLED,
          payload: { paymentIn }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: paymentIn, name: paymentIn.payerName }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, paymentIn)
      ];
    }
  }));
});

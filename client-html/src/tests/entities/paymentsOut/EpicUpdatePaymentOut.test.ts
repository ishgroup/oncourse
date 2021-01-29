import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdatePaymentOut } from "../../../js/containers/entities/paymentsOut/epics/EpicUpdatePaymenOut";
import {
  GET_PAYMENT_OUT_ITEM,
  UPDATE_PAYMENT_OUT_ITEM_FULFILLED,
  updatePaymentOut
} from "../../../js/containers/entities/paymentsOut/actions";

describe("Update payment out epic tests", () => {
  it("EpicUpdatePaymentOut should returns correct values", () => DefaultEpic({
    action: mockedApi => updatePaymentOut("1", mockedApi.db.getPaymentOut(1)),
    epic: EpicUpdatePaymentOut,
    processData: () => [
      {
        type: UPDATE_PAYMENT_OUT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "PaymentOut Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "PaymentOut", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_PAYMENT_OUT_ITEM,
        payload: "1"
      }
    ]
  }));
});

import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdatePaymentIn } from "../../../js/containers/entities/paymentsIn/epics/EpicUpdatePaymentIn";
import {
  UPDATE_PAYMENT_IN_ITEM_FULFILLED,
  updatePaymentIn
} from "../../../js/containers/entities/paymentsIn/actions";

describe("Update paymentIn epic tests", () => {
  it("EpicUpdatePaymentIn should returns correct values", () => DefaultEpic({
    action: mockedApi => updatePaymentIn("1", mockedApi.db.getPaymentIn(1)),
    epic: EpicUpdatePaymentIn,
    processData: () => [
      {
        type: UPDATE_PAYMENT_IN_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "PaymentIn Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "PaymentIn", listUpdate: true, savedID: "1" }
      }
    ]
  }));
});

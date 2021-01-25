import { DefaultEpic } from "../../common/Default.Epic";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import {
  GET_PAYMENT_IN_ITEM,
  reverse,
  REVERSE_PAYMENT_IN_ITEM_FULFILLED
} from "../../../js/containers/entities/paymentsIn/actions";
import { EpicReversePaymentIn } from "../../../js/containers/entities/paymentsIn/epics/EpicReversePaymentIn";
import { FETCH_SUCCESS } from "../../../js/common/actions";

describe("Create reverse paymentIn epic tests", () => {
  it("EpicReversePaymentIn should returns correct values", () => DefaultEpic({
    action: reverse(1),
    epic: EpicReversePaymentIn,
    processData: () => {
      const id = 1;
      return [
        {
          type: REVERSE_PAYMENT_IN_ITEM_FULFILLED
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "PaymentIn Record reversed" }
        },
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: "PaymentIn", listUpdate: true, savedID: id }
        },
        {
          type: GET_PAYMENT_IN_ITEM,
          payload: id
        }
      ];
    }
  }));
});

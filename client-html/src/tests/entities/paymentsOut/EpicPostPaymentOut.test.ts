import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicPostPaymentOut } from "../../../js/containers/entities/paymentsOut/epics/EpicPostPaymentOut";
import { POST_PAYMENT_OUT_ITEM_FULFILLED, postPaymentOut } from "../../../js/containers/entities/paymentsOut/actions";

describe("Create payment out epic tests", () => {
  it("EpicPostPaymentOut should returns correct values", () => DefaultEpic({
    action: mockedApi => postPaymentOut(mockedApi.db.getPaymentOut(1)),
    epic: EpicPostPaymentOut,
    processData: () => [
      {
        type: POST_PAYMENT_OUT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "PaymentOut Record created" }
      }
    ]
  }));
});

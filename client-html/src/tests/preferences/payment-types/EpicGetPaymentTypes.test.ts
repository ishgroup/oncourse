import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_PAYMENT_TYPES_FULFILLED,
  getPaymentTypes
} from "../../../js/containers/preferences/actions";
import { EpicGetPaymentTypes } from "../../../js/containers/preferences/containers/payment-types/epics/EpicGetPaymentTypes";

describe("Get payment types epic tests", () => {
  it("EpicGetPaymentTypes should returns correct values", () => DefaultEpic({
    action: getPaymentTypes(),
    epic: EpicGetPaymentTypes,
    processData: mockedApi => {
      const paymentTypes = mockedApi.db.paymentTypes;

      return [
        {
          type: GET_PAYMENT_TYPES_FULFILLED,
          payload: { paymentTypes }
        }
      ];
    }
  }));
});

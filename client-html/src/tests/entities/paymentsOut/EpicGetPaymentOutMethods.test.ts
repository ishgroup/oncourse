import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetPaymentOutMethods } from "../../../js/containers/entities/paymentsOut/epics/EpicGetPaymentOutMethods";
import {
  GET_ACTIVE_PAYMENT_OUT_METHODS_FULFILLED,
  getActivePaymentOutMethods
} from "../../../js/containers/entities/paymentsOut/actions";

describe("Get payment out methods / types epic tests", () => {
  it("EpicGetPaymentOutMethods should returns correct values", () => DefaultEpic({
    action: getActivePaymentOutMethods(),
    epic: EpicGetPaymentOutMethods,
    processData: mockedApi => {
      const paymentMethods = mockedApi.db.paymentTypes;
      const paymentOutMethods = paymentMethods.filter(({ systemType }) => systemType !== true);

      return [
        {
          type: GET_ACTIVE_PAYMENT_OUT_METHODS_FULFILLED,
          payload: paymentOutMethods
        }
      ];
    }
  }));
});

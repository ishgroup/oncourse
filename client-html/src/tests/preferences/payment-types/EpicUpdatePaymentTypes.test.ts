import { DefaultEpic } from "../../common/Default.Epic";
import {
  UPDATE_PAYMENT_TYPES_FULFILLED,
  updatePaymentTypes
} from "../../../js/containers/preferences/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdatePaymentTypes } from "../../../js/containers/preferences/containers/payment-types/epics/EpicUpdatePaymentTypes";

describe("Update payment types epic tests", () => {
  it("EpicUpdatePaymentTypes should returns correct values", () => DefaultEpic({
    action: mockedApi => updatePaymentTypes(mockedApi.db.paymentTypes),
    epic: EpicUpdatePaymentTypes,
    processData: mockedApi => {
      const paymentTypes = mockedApi.db.paymentTypes;
      return [
        {
          type: UPDATE_PAYMENT_TYPES_FULFILLED,
          payload: { paymentTypes }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Payment Types were successfully updated" }
        }
      ];
    }
  }));
});

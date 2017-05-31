import * as L from "lodash";
import moment from "moment";
import uuid from "uuid";

import {createStringEnum} from "../../../../common/utils/EnumUtils";
import {Item} from "../../../../model/common/Item";
import {IshState} from "../../../../services/IshState";
import {PaymentRequest} from "../../../../model/checkout/payment/PaymentRequest";
import {BuildCheckoutModelRequest} from "../../../services/CheckoutService";
import {AxiosResponse} from "axios";
import {isCommonError} from "../../../../common/utils/ErrorUtils";

export const FieldName = createStringEnum([
  "creditCardName",
  "creditCardNumber",
  "expiryMonth",
  "expiryYear",
  "creditCardCvv",
  "agreementFlag"
]);
export type FieldName = keyof typeof FieldName;

export interface Values {
  creditCardName: string
  creditCardNumber: string
  expiryMonth: string
  expiryYear: string
  creditCardCvv: string
  agreementFlag: boolean
}

export class PaymentService {
  static months = (): Promise<Item> => {
    const result = L.range(1, 13).map((i) => {
      const v = i < 10 ? `0${i}` : `${i}`;
      return {key: v, value: v}
    });
    return Promise.resolve(result);
  };

  static years = (): Promise<Item> => {
    const start: number = moment().get('year');
    const result = L.range(start, start + 11).map((y) => {
      const v = `${y}`;
      return {key: v, value: v}
    });
    return Promise.resolve(result);
  };

  static valuesToRequest = (values: Values, state: IshState): PaymentRequest => {
    const result: PaymentRequest = new PaymentRequest();
    result.creditCardName = values.creditCardName;
    result.creditCardNumber = values.creditCardNumber;
    result.creditCardCvv = values.creditCardCvv;
    result.expiryMonth = values.expiryMonth;
    result.expiryYear = values.expiryYear;
    result.agreementFlag = values.agreementFlag;
    result.checkoutModelRequest = BuildCheckoutModelRequest.fromState(state);
    result.payNow = state.checkout.amount.payNow;
    result.sessionId = uuid();
    return result;
  };
}



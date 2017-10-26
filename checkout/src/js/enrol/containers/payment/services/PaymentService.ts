import * as L from "lodash";
import moment from "moment";
import uuid from "uuid";

import {createStringEnum} from "../../../../common/utils/EnumUtils";
import {Item, PaymentRequest, MakeCorporatePassRequest} from "../../../../model";
import {IshState} from "../../../../services/IshState";
import {BuildCheckoutModelRequest} from "../../../services/CheckoutService";

export const FieldName = createStringEnum([
  "creditCardName",
  "creditCardNumber",
  "expiryMonth",
  "expiryYear",
  "creditCardCvv",
  "agreementFlag",
]);
export type FieldName = keyof typeof FieldName;

export interface CreditCardFormValues {
  creditCardName?: string;
  creditCardNumber?: string;
  expiryMonth?: string;
  expiryYear?: string;
  creditCardCvv?: string;
  agreementFlag: boolean;
}

export interface CorporatePassFormValues {
  reference: string;
  agreementFlag: boolean;
}

export class PaymentService {
  static months = (): Promise<Item[]> => {
    const result = L.range(1, 13).map(i => {
      const v = i < 10 ? `0${i}` : `${i}`;
      return {key: v, value: v};
    });
    return Promise.resolve(result);
  }

  static years = (): Promise<Item[]> => {
    const start: number = moment().get('year');
    const result = L.range(start, start + 11).map(y => {
      const v = `${y}`;
      return {key: v, value: v};
    });
    return Promise.resolve(result);
  }

  static creditFormValuesToRequest = (values: CreditCardFormValues, state: IshState): PaymentRequest => {
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
  }

  static corporatePassValuesToRequest = (values, state: IshState): MakeCorporatePassRequest => {
    const result: MakeCorporatePassRequest = new MakeCorporatePassRequest();
    result.agreementFlag = values.agreementFlag;
    result.reference = values.reference || null;
    result.checkoutModelRequest = BuildCheckoutModelRequest.fromState(state);
    result.corporatePassId = state.checkout.payment.corporatePass.id;
    return result;
  }
}


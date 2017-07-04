import {Epic} from "redux-observable";
import "rxjs";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {GET_AMOUNT, updateAmount} from "../actions/Actions";
import {Amount} from "../../model/checkout/Amount";

const request: EpicUtils.Request<Amount, IshState> = {
  type: GET_AMOUNT,
  getData: (payload, state: IshState) => CheckoutService.getAmount(state),
  processData: (value: Amount, state: IshState) => {
    return [
      updateAmount(value),
    ];
  },
};

export const EpicUpdateAmount: Epic<any, any> = EpicUtils.Create(request);

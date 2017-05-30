import {Epic} from "redux-observable";
import "rxjs";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {UPDATE_AMOUNT_REQUEST, updateAmount} from "../actions/Actions";
import {Amount} from "../../model/checkout/Amount";

const request: EpicUtils.Request<Amount, IshState> = {
  type: UPDATE_AMOUNT_REQUEST,
  getData: (payload, state: IshState) => CheckoutService.getAmount(state),
  processData: (value: Amount, state: IshState) => {
    return [updateAmount(value)];
  }
};

const Epic: Epic<any, any> = EpicUtils.Create(request);

export default Epic;
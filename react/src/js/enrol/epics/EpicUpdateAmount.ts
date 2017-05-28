import {Epic} from "redux-observable";
import "rxjs";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {AmountUpdate, AmountUpdateRequest} from "../actions/Actions";
import {Amount} from "../../model/checkout/Amount";

const request: EpicUtils.Request<Amount, IshState> = {
  type: AmountUpdateRequest,
  getData: (payload, state: IshState) => CheckoutService.getAmount(state),
  processData: (value: Amount, state: IshState) => {
    return [{type: AmountUpdate, payload: value}];
  }
};

const Epic: Epic<any, any> = EpicUtils.Create(request);

export default Epic;
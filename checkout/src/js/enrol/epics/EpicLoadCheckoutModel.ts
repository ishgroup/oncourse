import {IshState} from "../../services/IshState";
import CheckoutService from "../services/CheckoutService";
import * as EpicUtils from "../../common/epics/EpicUtils";
import {CheckoutModel, ContactNode} from "../../model";
import {GET_CHECKOUT_MODEL_FROM_BACKEND, updateAmount} from "../actions/Actions";
import {rewriteContactNodeToState} from "../containers/summary/actions/Actions";
import {Epic} from "redux-observable";


const request: EpicUtils.Request<CheckoutModel, IshState> = {
  type: GET_CHECKOUT_MODEL_FROM_BACKEND,
  getData: (payload: any, state: IshState): Promise<CheckoutModel> => CheckoutService.getCheckoutModel(state),
  processData: (value: CheckoutModel, state: IshState) => {
    const result = [];
    value.contactNodes.forEach((node: ContactNode) => {
      result.push(rewriteContactNodeToState(node));
    });
    result.push(updateAmount(value.amount));
    return result;
  },
};

export const EpicUpdateSummary: Epic<any, any> = EpicUtils.Create(request);

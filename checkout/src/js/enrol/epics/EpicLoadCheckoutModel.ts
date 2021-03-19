import {IshState} from "../../services/IshState";
import CheckoutService from "../services/CheckoutService";
import * as EpicUtils from "../../common/epics/EpicUtils";
import {CheckoutModel} from "../../model";
import {GET_CHECKOUT_MODEL_FROM_BACKEND, updateAmount} from "../actions/Actions";
import {rewriteContactNodesToState} from "../containers/summary/actions/Actions";
import {Epic} from "redux-observable";


const request: EpicUtils.Request<CheckoutModel, IshState> = {
  type: GET_CHECKOUT_MODEL_FROM_BACKEND,
  getData: (payload: any, state: IshState): Promise<CheckoutModel> => CheckoutService.getCheckoutModel(state),
  processData: (value: CheckoutModel) => [rewriteContactNodesToState(value.contactNodes), updateAmount(value.amount)]
};

export const EpicUpdateSummary: Epic<any, any> = EpicUtils.Create(request);

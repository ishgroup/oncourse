import * as L from "lodash";
import {IshState} from "../../services/IshState";
import CheckoutService from "../services/CheckoutService";
import * as EpicUtils from "./EpicUtils";
import {CheckoutModel} from "../../model/checkout/CheckoutModel";
import {ContactNode} from "../../model/checkout/ContactNode";
import {UPDATE_SUMMARY_REQUEST, updateAmount} from "../actions/Actions";
import {updateContactNode} from "../containers/summary/actions/Actions";
import {Epic} from "redux-observable";


const request: EpicUtils.Request<CheckoutModel, IshState> = {
  type: UPDATE_SUMMARY_REQUEST,
  getData: (payload: any, state: IshState): Promise<CheckoutModel> => CheckoutService.getCheckoutModel(state),
  processData: (value: CheckoutModel, state: IshState) => {
    const result = [];
    value.contactNodes.forEach((node: ContactNode) => {
      result.push(updateContactNode(node));
    });
    result.push(updateAmount(value.amount));
    return result;
  },
};

export const EpicUpdateSummary: Epic<any, any> = EpicUtils.Create(request);



import * as L from "lodash";
import {IshState} from "../../services/IshState";
import CheckoutService from "../services/CheckoutService";
import {PROCEED_TO_PAYMENT, updateContactNode} from "../containers/summary/actions/Actions";
import {CHANGE_PHASE, changePhase, updateAmount} from "../actions/Actions";
import * as EpicUtils from "./EpicUtils";
import {CheckoutModel} from "../../model/checkout/CheckoutModel";
import {ContactNode} from "../../model/checkout/ContactNode";
import {Phase} from "../reducers/State";
import {CommonError} from "../../model/common/CommonError";
import {Epic} from "redux-observable";


const request: EpicUtils.Request<CheckoutModel, IshState> = {
  type: PROCEED_TO_PAYMENT,
  getData: (payload: any, state: IshState): Promise<CheckoutModel> => CheckoutService.getCheckoutModel(state),
  processData: (value: CheckoutModel, state: IshState) => {
    return ProcessCheckoutModel.process(value);
  },
};

export const EpicProceedToPayment: Epic<any, any> = EpicUtils.Create(request);


/**
 * Convert CheckoutModel to set of redux-Action
 */
export class ProcessCheckoutModel {
  static process = (model: CheckoutModel): any[] => {
    let result = ProcessCheckoutModel.processError(model.error);
    result = [...result, ... ProcessCheckoutModel.processNodes(model.contactNodes)];
    result.push(updateAmount(model.amount));
    if (!result.find(a => a.type === CHANGE_PHASE && a.payload === Phase.Summary)) {
      result.push(changePhase(Phase.Payment));
    }
    return result;
  };

  static processNodes = (nodes: ContactNode[]): any[] => {
    const result = [];
    if (!L.isEmpty(nodes)) {
      nodes.forEach((node: ContactNode) => {
        if (node.enrolments.find(e => !L.isEmpty(e.errors) && e.selected)
          || node.applications.find(a => !L.isEmpty(a.errors) && a.selected)
          || node.memberships.find(m => !L.isEmpty(m.errors) && m.selected)
          || node.articles.find(ar => !L.isEmpty(ar.errors) && ar.selected)
          || node.vouchers.find(v => !L.isEmpty(v.errors) && v.selected)) {
          result.push(changePhase(Phase.Summary));
        }
        result.push(updateContactNode(node));
      });
    }
    return result;
  };

  static processError = (error: CommonError): any[] => {
    const result = [];
    if (!L.isNil(error)) {
      result.push(changePhase(Phase.Summary));
      result.push(EpicUtils.showCommonError(error));
    }
    return result;
  }
}

import * as L from "lodash";
import {IshState} from "../../services/IshState";
import CheckoutService from "../services/CheckoutService";
import {addContactNodeToState, PROCEED_TO_PAYMENT} from "../containers/summary/actions/Actions";
import {CHANGE_PHASE, changePhase} from "../actions/Actions";
import * as EpicUtils from "../../common/epics/EpicUtils";
import {CheckoutModel, ContactNode, CommonError} from "../../model";
import {Phase} from "../reducers/State";
import {Epic} from "redux-observable";
import {processingMandatoryFields} from "../containers/payment/actions/Actions";

const request: EpicUtils.Request<CheckoutModel, IshState> = {
  type: PROCEED_TO_PAYMENT,
  getData: (payload: any, state: IshState): Promise<CheckoutModel> => CheckoutService.getCheckoutModel(state),
  processData: (value: CheckoutModel) => {
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

    if (!result.find(a => a.type === CHANGE_PHASE && a.payload === Phase.Summary)) {
      result.push(processingMandatoryFields());
    }
    return result;
  }

  static processNodes = (nodes: ContactNode[]): any[] => {
    const result = [];
    if (!L.isEmpty(nodes)) {
      nodes.forEach((node: ContactNode) => {
        if (node.enrolments.find(e => !L.isEmpty(e.errors) && e.selected)
          || node.applications.find(a => !L.isEmpty(a.errors) && a.selected)
          || node.memberships.find(m => !L.isEmpty(m.errors) && m.selected)
          || node.articles.find(ar => !L.isEmpty(ar.errors) && ar.selected)
          || node.vouchers.find(v => !L.isEmpty(v.errors) && v.selected)
          || node.waitingLists.find(w => !L.isEmpty(w.errors) && w.selected)) {
          result.push(changePhase(Phase.Summary));
        }
        result.push(addContactNodeToState(node));
      });
    }
    return result;
  }

  static processError = (error: CommonError): any[] => {
    const result = [];
    if (!L.isNil(error)) {
      result.push(changePhase(Phase.Summary));
    }
    return result;
  }
}

import * as L from "lodash";
import {IshState} from "../../services/IshState";
import CheckoutService from "../services/CheckoutService";
import {PROCEED_TO_PAYMENT, updateContactNode} from "../containers/summary/actions/Actions";
import {changePhase, updateAmount} from "../actions/Actions";
import * as EpicUtils from "./EpicUtils";
import {showCommonError} from "./EpicUtils";
import {CheckoutModel} from "../../model/checkout/CheckoutModel";
import {ContactNode} from "../../model/checkout/ContactNode";
import {Phase} from "../reducers/State";
import {CommonError} from "../../model/common/CommonError";
import {commonErrorToValidationError} from "../../common/utils/ErrorUtils";


const request: EpicUtils.Request<CheckoutModel, IshState> = {
  type: PROCEED_TO_PAYMENT,
  getData: (payload: any, state: IshState): Promise<CheckoutModel> => CheckoutService.getCheckoutModel(state),
  processData: (value: CheckoutModel, state: IshState) => {
    return ProcessCheckoutModel.process(value);
  }
};


/**
 * Convert CheckoutModel to set of redux-Action
 */
export class ProcessCheckoutModel {
  static process = (model: CheckoutModel): any[] => {
    let result = ProcessCheckoutModel.processError(model.error);
    result = [...result, ... ProcessCheckoutModel.processNodes(model.contactNodes)];
    result.push(updateAmount(model.amount));
    return result;
  };

  static processNodes = (nodes: ContactNode[]): any[] => {
    const result = [];
    if (!L.isEmpty(nodes)) {
      nodes.forEach((node: ContactNode) => {
        if (node.enrolments.find((e) => !L.isEmpty(e.errors))) {
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
      result.push(showCommonError(commonErrorToValidationError(error)))
    }
    return result;
  }
}

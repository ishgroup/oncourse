import {Epic} from "redux-observable";
import "rxjs";
import {ItemsLoad, OpenSummaryRequest} from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {ContactNode} from "../../model/checkout/ContactNode";
import {IshState} from "../../services/IshState";
import {changePhase, SHOW_MESSAGES, updateAmountRequest, updateSummaryRequest} from "../actions/Actions";
import {Phase} from "../reducers/State";
import {toValidationError} from "../../common/utils/ErrorUtils";
import * as EpicUtils from "./EpicUtils";
import {ContactNodeToState} from "../containers/summary/reducers/State";

const request: EpicUtils.Request<ContactNode, IshState> = {
  type: OpenSummaryRequest,
  getData: (payload, state) => CheckoutService.getContactNode(state),
  processData: (contactNode, state) => {
    return [{
      type: ItemsLoad,
      payload: ContactNodeToState([contactNode])
    }, updateSummaryRequest(), changePhase(Phase.Summary)];
  },
  processError: (data) => {
    return [{type: SHOW_MESSAGES, payload: toValidationError(data)}]
  }
};

/**
 * This epic loads ContactNode for payer and change phase to Summary
 */
export const EpicOpenSummary: Epic<any, any> = EpicUtils.Create(request);


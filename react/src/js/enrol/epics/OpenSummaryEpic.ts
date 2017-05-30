import {Epic} from "redux-observable";
import "rxjs";
import {ItemsLoad, OpenSummaryRequest} from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {ContactNode} from "../../model/checkout/ContactNode";
import {IshState} from "../../services/IshState";
import {changePhase, SHOW_MESSAGES, updateAmountRequest} from "../actions/Actions";
import {Phase} from "../reducers/State";
import {toValidationError} from "../../common/utils/ErrorUtils";
import * as EpicUtils from "./EpicUtils";
import {convert} from "../containers/summary/reducers/State";

const request: EpicUtils.Request<ContactNode, IshState> = {
  type: OpenSummaryRequest,
  getData: (payload, state) => CheckoutService.getContactNode(state),
  processData: (contactNode, state) => {
    return [{type: ItemsLoad, payload: convert([contactNode])}, changePhase(Phase.Summary), updateAmountRequest()];
  },
  processError: (data) => {
    return {type: SHOW_MESSAGES, payload: toValidationError(data)}
  }
};

/**
 * This epic loads ContactNode for payer and change phase to Summary
 */
const Epic: Epic<any, any> = EpicUtils.Create(request);

export default Epic;
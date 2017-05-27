import {Epic} from "redux-observable";
import "rxjs";
import {ItemsLoad, OpenSummaryRequest} from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {PurchaseItems} from "../../model/checkout/PurchaseItems";
import {IshState} from "../../services/IshState";
import {changePhase, MessagesShow, updateAmount} from "../actions/Actions";
import {Phase} from "../reducers/State";
import {toValidationError} from "../../common/utils/ErrorUtils";
import * as EpicUtils from "./EpicUtils";
import {convert} from "../containers/summary/reducers/State";

const request: EpicUtils.Request<PurchaseItems, IshState> = {
  type: OpenSummaryRequest,
  getData: (payload, state) => CheckoutService.getPurchaseItems(state),
  processData: (items, state) => {
    return [{type: ItemsLoad, payload: convert([items])}, changePhase(Phase.Summary), updateAmount()];
  },
  processError: (data) => {
    return {type: MessagesShow, payload: toValidationError(data)}
  }
};

/**
 * This epic loads PurchaseItems for payer and change phase to Summary
 */
const Epic: Epic<any, any> = EpicUtils.Create(request);

export default Epic;
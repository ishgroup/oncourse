import * as L from "lodash";
import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";

import * as Actions from "../actions/Actions";
import {Actions as WebActions} from "../../web/actions/Actions";
import {ValidationError, PaymentStatus} from "../../model";
import {ShoppingCardIsEmpty} from "../containers/checkout/Errors";
import {Phase} from "../reducers/State";
import CheckoutService from "../services/CheckoutService";
import {IAction} from "../../actions/IshAction";
import {getPaymentStatus, resetPaymentState} from "../containers/payment/actions/Actions";
import {IshState} from "../../services/IshState";
import {getAllContactNodesFromBackend, removeItemFromSummary} from "../containers/summary/actions/Actions";


const showCartIsEmptyMessage = (): IAction<any>[] => {
  const error: ValidationError = {formErrors: [ShoppingCardIsEmpty], fieldsErrors: []};
  return [Actions.changePhase(Phase.Init), {type: Actions.SHOW_MESSAGES, payload: error}];
};



const getItemType = actionType => {
  switch (actionType) {
    case WebActions.REMOVE_CLASS_FROM_CART:
      return 'enrolments';

    case WebActions.REMOVE_WAITING_COURSE_FROM_CART:
      return 'waitingLists';

    default:
      return null;
  }
}


/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const EpicInit: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(
    Actions.INIT_REQUEST,
    WebActions.REMOVE_CLASS_FROM_CART,
    WebActions.REMOVE_PRODUCT_FROM_CART,
    WebActions.REMOVE_WAITING_COURSE_FROM_CART,
  ).flatMap(action => {
    const state = store.getState();

    const result = [];

    result.push(Actions.updateContactAddProcess({}, null, null));

    if (!L.isNil(state.checkout.payment.value)) {
      const payment = state.checkout.payment.value;

      if (CheckoutService.isFinalStatus(payment)) {
        result.push(Actions.changePhase(Phase.Init));
        result.push(resetPaymentState());
        result.push(Actions.sendInitRequest());
        return result;
      } else if (state.checkout.phase === Phase.Result) {
        return result.concat(payment.status === PaymentStatus.IN_PROGRESS ? getPaymentStatus() : CheckoutService.processPaymentResponse(payment));
      }
    }

    if (CheckoutService.cartIsEmpty(state.cart)) {
      return result.concat(showCartIsEmptyMessage());
    }
    if (state.checkout.summary.result.length) {
      return result.concat([
        Actions.changePhase(state.checkout.isCartModified ? Phase.Summary : state.checkout.page),
        removeItemFromSummary(getItemType(action.type), action.payload && action.payload.id),
        getAllContactNodesFromBackend()
      ]);
    }

    return result.concat([Actions.changePhase(Phase.AddPayer)]);
  });
};


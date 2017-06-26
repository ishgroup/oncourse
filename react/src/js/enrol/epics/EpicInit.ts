import * as L from "lodash";
import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";

import * as Actions from "../actions/Actions";
import {changePhase, sendInitRequest} from "../actions/Actions";
import {ValidationError} from "../../model/common/ValidationError";
import {ShoppingCardIsEmpty} from "../containers/checkout/Errors";
import {CheckoutState, Phase} from "../reducers/State";
import CheckoutService from "../services/CheckoutService";
import {submitAddContact, Values} from "../containers/contact-add/actions/Actions";
import {IAction} from "../../actions/IshAction";
import {Contact} from "../../model/web/Contact";
import {ContactId} from "../../model/web/ContactId";
import {ProcessError} from "./EpicUtils";
import {AxiosResponse} from "axios";
import {resetPaymentState} from "../containers/payment/actions/Actions";
import {IshState} from "../../services/IshState";
import {openEditContact} from "../containers/contact-edit/actions/Actions";

const showCartIsEmptyMessage = (): IAction<any>[] => {
  const error: ValidationError = {formErrors: [ShoppingCardIsEmpty], fieldsErrors: []};
  return [changePhase(Phase.Init), {type: Actions.SHOW_MESSAGES, payload: error}];
};

const openPayerDetails = (state: CheckoutState): IAction<any>[] => {
  const contact: Contact = state.contacts.entities.contact[state.payerId];
  return [openEditContact(contact)];
};


const setPayerFromCart = (contact: Contact): Observable<any> => {
  return Observable.fromPromise(CheckoutService.createOrGetContact(contact as Values))
    .flatMap((data: ContactId) => {
      return [submitAddContact(data, contact as Values)];
    })
    .catch((data: AxiosResponse) => {
      return ProcessError(data);
    });
};


/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const EpicInit: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(Actions.INIT_REQUEST).flatMap(action => {

    if (!L.isNil(store.getState().checkout.payment.value)) {
      if (CheckoutService.isFinalStatus(store.getState().checkout.payment.value)) {
        return [changePhase(Phase.Init), resetPaymentState(), sendInitRequest()];
      } else {
        return CheckoutService.processPaymentResponse(store.getState().checkout.payment.value);
      }
    }

    if (CheckoutService.cartIsEmpty(store.getState().cart)) {
      return showCartIsEmptyMessage();
    }

    if (CheckoutService.hasPayer(store.getState().checkout)) {
      return openPayerDetails(store.getState().checkout);
    }

    if (CheckoutService.hasCartContact(store.getState().cart)) {
      return setPayerFromCart(store.getState().cart.contact);
    }
    return [changePhase(Phase.AddPayer)];
  });
};


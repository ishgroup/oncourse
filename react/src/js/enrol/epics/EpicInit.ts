import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";

import * as Actions from "../actions/Actions";
import {changePhase, changePhaseRequest} from "../actions/Actions";
import {ValidationError} from "../../model/common/ValidationError";
import {ShoppingCardIsEmpty} from "../containers/checkout/Errors";
import {Phase} from "../reducers/State";
import CheckoutService from "../services/CheckoutService";
import {addContactRequest, Values} from "../containers/contact-add/actions/Actions";
import {IAction} from "../../actions/IshAction";
import {Contact} from "../../model/web/Contact";
import {ContactId} from "../../model/web/ContactId";
import {ProcessError} from "./EpicUtils";
import {AxiosResponse} from "axios";
import {getPaymentStatus} from "../containers/payment/actions/Actions";

const showCartIsEmptyMessage = (): IAction<any>[] => {
  const error: ValidationError = {formErrors: [ShoppingCardIsEmpty], fieldsErrors: []};
  return [changePhase(Phase.Init), {type: Actions.SHOW_MESSAGES, payload: error}];
};

const showContactDetails = (): IAction<any>[] => {
  return [changePhaseRequest(Phase.EditContact)];
};

const showAddContact = (): IAction<any>[] => {
  return [changePhase(Phase.AddContact)]
};

const showPaymentInProgress = (): IAction<any>[] => {
  return [changePhase(Phase.PaymentInProgress), getPaymentStatus()]
};

const setPayerFromCart = (contact: Contact): Observable<any> => {
  return Observable.fromPromise(CheckoutService.createOrGetContact(contact as Values))
    .flatMap((data: ContactId) => {
      return [addContactRequest(data, contact as Values)]
    })
    .catch((data: AxiosResponse) => {
      return ProcessError(data)
    })
};


/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const EpicInit: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(Actions.InitRequest).flatMap((action) => {
    if (CheckoutService.cartIsEmpty(store.getState().cart)) {
      return showCartIsEmptyMessage();
    }

    if (CheckoutService.hasPayer(store.getState().checkout)) {
      return showContactDetails();
    }

    if (CheckoutService.hasCartContact(store.getState().cart)) {
      return setPayerFromCart(store.getState().cart.contact)
    }

    if (CheckoutService.isPaymentInProgress(store.getState().checkout.payment)) {
      return showPaymentInProgress();
    }

    return showAddContact();
  });
};


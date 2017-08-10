import * as L from "lodash";
import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";

import * as Actions from "../actions/Actions";
import {ValidationError, Contact, ContactId} from "../../model";
import {ShoppingCardIsEmpty} from "../containers/checkout/Errors";
import {Phase} from "../reducers/State";
import CheckoutService from "../services/CheckoutService";
import {submitAddContact, Values} from "../containers/contact-add/actions/Actions";
import {IAction} from "../../actions/IshAction";
import {ProcessError} from "./EpicUtils";
import {AxiosResponse} from "axios";
import {resetPaymentState} from "../containers/payment/actions/Actions";
import {IshState} from "../../services/IshState";
import {openEditContact} from "../containers/contact-edit/actions/Actions";
import {getAllContactNodesFromBackend, getContactNodeFromBackend} from "../containers/summary/actions/Actions";

const updateContactNodes = contacts => {
  const result = [];
  contacts.result.map(id => result.push(getContactNodeFromBackend(contacts.entities.contact[id])));

  return result;
};

const showCartIsEmptyMessage = (): IAction<any>[] => {
  const error: ValidationError = {formErrors: [ShoppingCardIsEmpty], fieldsErrors: []};
  return [Actions.changePhase(Phase.Init), {type: Actions.SHOW_MESSAGES, payload: error}];
};

const openPayerDetails = (state: IshState): IAction<any>[] => {
  const contact: Contact = state.checkout.contacts.entities.contact[state.checkout.payerId];
  return [
    openEditContact(contact),
    ...updateContactNodes(state.checkout.contacts),
  ];
};


const setPayerFromCart = (state: IshState): Observable<any> => {
  return Observable.fromPromise(CheckoutService.createOrGetContact(state.cart.contact as Values))
    .flatMap((data: ContactId) => {
      return [
        submitAddContact(data, state.cart.contact as Values),
        ...updateContactNodes(state.checkout.contacts),
      ];
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
    const state = store.getState();

    const result = [];

    result.push(Actions.getPreferences());
    result.push(Actions.updateContactAddProcess({}, null, null, null));

    if (state.checkout.payment.resetOnInit) {
      result.push(resetPaymentState());
    }

    if (!L.isNil(state.checkout.payment.value)) {
      if (CheckoutService.isFinalStatus(state.checkout.payment.value)) {
        result.push(Actions.changePhase(Phase.Init));
        result.push(resetPaymentState());
        result.push(Actions.sendInitRequest());
        return result;
      } else {
        return result.concat(CheckoutService.processPaymentResponse(state.checkout.payment.value));
      }
    }

    if (CheckoutService.cartIsEmpty(state.cart)) {
      return result.concat(showCartIsEmptyMessage());
    }

    if (state.checkout.summary.result.length) {
      return result.concat([
        Actions.changePhase(state.checkout.page),
        getAllContactNodesFromBackend(),
      ]);
    }

    return result.concat([Actions.changePhase(Phase.AddPayer)]);
  });
};


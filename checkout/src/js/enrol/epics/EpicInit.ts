import * as L from "lodash";
import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";

import * as Actions from "../actions/Actions";
import {Actions as WebActions} from "../../web/actions/Actions";
import {ValidationError, Contact, ContactId, PaymentStatus} from "../../model";
import {ShoppingCardIsEmpty} from "../containers/checkout/Errors";
import {Phase} from "../reducers/State";
import CheckoutService from "../services/CheckoutService";
import {submitAddContact, Values} from "../containers/contact-add/actions/Actions";
import {IAction} from "../../actions/IshAction";
import {ProcessError} from "../../common/epics/EpicUtils";
import {AxiosResponse} from "axios";
import {getPaymentStatus, resetPaymentState} from "../containers/payment/actions/Actions";
import {IshState} from "../../services/IshState";
import {openEditContact, setFieldsToState} from "../containers/contact-edit/actions/Actions";
import {getAllContactNodesFromBackend, getContactNodeFromBackend, removeItemFromSummary} from "../containers/summary/actions/Actions";
import {getCheckoutModelFromBackend} from "../actions/Actions";

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
        getCheckoutModelFromBackend(),
        removeItemFromSummary(getItemType(action.type), action.payload && action.payload.id),
      ]);
    }

    return result.concat([Actions.changePhase(Phase.AddPayer)]);
  });
};


import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import * as Lodash from "lodash";

import * as Actions from "../actions/Actions";
import {changePhase, changePhaseRequest} from "../actions/Actions";
import {CartState, IshState} from "../../services/IshState";
import {ValidationError} from "../../model/common/ValidationError";
import {ShoppingCardIsEmpty} from "../containers/checkout/Errors";
import {Phase} from "../reducers/State";

const hasContact = (state: IshState) => {
  return !Lodash.isNil(state.cart.contact.id) || !Lodash.isNil(state.checkout.payer.entity.id);
};

const cartIsEmpty = (cart: CartState) => {
  return Lodash.isEmpty(cart.courses.result) && Lodash.isEmpty(cart.products.result)
};

const showCartIsEmptyMessage = (): any => {
  const error: ValidationError = {formErrors: [ShoppingCardIsEmpty], fieldsErrors: []};
  return [changePhase(Phase.Init),{type: Actions.MessagesShow, payload: error}];
};

const showContactDetails = (): any => {
  return [changePhaseRequest(Phase.EditContactDetails)];
};

const showAddContact = (): any => {
  return [changePhase(Phase.AddContact)]
};

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
const InitEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(Actions.InitRequest).flatMap((action) => {
    if (cartIsEmpty(store.getState().cart)) {
      return showCartIsEmptyMessage();
    } else if (hasContact(store.getState())) {
      return showContactDetails();
    } else {
      return showAddContact();
    }
  });
};


export default InitEpic

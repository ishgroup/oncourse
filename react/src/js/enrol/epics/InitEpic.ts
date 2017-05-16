import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import * as Lodash from "lodash";

import * as Actions from "../actions/Actions";
import {CartState, IshState} from "../../services/IshState";
import {ValidationError} from "../../model/common/ValidationError";
import {ShoppingCardIsEmpty} from "../containers/checkout/Errors";
import {Phase} from "../reducers/State";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
const InitEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(Actions.InitRequest).flatMap((action) => {
    let result;
    if (cartIsEmpty(store.getState().cart)) {
      const error: ValidationError = {formErrors: [ShoppingCardIsEmpty], fieldsErrors: []};
      result = [{type: Actions.InitReject, payload: error}]
    } else if (hasContact(store.getState())) {
      result = [{type: Actions.PhaseChange, payload: Phase.EditContactDetails}]
    } else {
      result = [{type: Actions.PhaseChange, payload: Phase.AddContact}]
    }
    return result;
  });
};

const hasContact = (state: IshState) => {
    return !Lodash.isNil(state.cart.contact.id) || !Lodash.isNil(state.checkout.payer.entity.id);
};

const cartIsEmpty = (cart: CartState) => {
  return Lodash.isEmpty(cart.courses.result) && Lodash.isEmpty(cart.products.result)
};

export default InitEpic

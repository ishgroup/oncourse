import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {
  ADD_CONTACT_REQUEST, addAdditionalContact, addContact,
  AddContactPayload
} from "../containers/contact-add/actions/Actions";
import {addContact as addContactToCart} from "../../web/actions/Actions";
import {Phase} from "../reducers/State";
import {changePhaseRequest, setNewContactFlag} from "../actions/Actions";
import {IshState} from "../../services/IshState";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const EpicAddContact: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(ADD_CONTACT_REQUEST).flatMap((action) => {
    const payload: AddContactPayload = action.payload;
    // isMain = store.getState().checkout.payer
    const payer = store.getState().checkout.payer;
    const isAdditional = store.getState().checkout.payer && store.getState().checkout.payer.entity;
    const result = [
      isAdditional ? addAdditionalContact(payload.contact) : addContact(payload.contact),
      addContactToCart(payload.contact),
      setNewContactFlag(payload.newContact),
      changePhaseRequest(Phase.EditContact),
    ];
    return result;
  });
};

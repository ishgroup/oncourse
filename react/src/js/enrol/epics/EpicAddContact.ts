import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {ADD_CONTACT_REQUEST, addContact, AddContactPayload} from "../containers/contact-add/actions/Actions";
import {addContact as addContactToCart} from "../../web/actions/Actions";
import {Phase} from "../reducers/State";
import {changePhaseRequest, setNewContactFlag} from "../actions/Actions";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const EpicAddContact: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(ADD_CONTACT_REQUEST).flatMap((action) => {
    const payload: AddContactPayload = action.payload;
    const result = [
      addContact(payload.contact),
      addContactToCart(payload.contact),
      setNewContactFlag(payload.newContact),
      changePhaseRequest(Phase.EditContact),
    ];
    return result;
  });
};

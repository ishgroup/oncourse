import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {ADD_CONTACT_REQUEST, addContact} from "../containers/contact-add/actions/Actions";
import {addContact as addContactToCart} from "../../web/actions/Actions";
import {Phase} from "../reducers/State";
import {changePhaseRequest} from "../actions/Actions";
import {Contact} from "../../model/web/Contact";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const EpicAddContact: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(ADD_CONTACT_REQUEST).flatMap((action) => {
    const contact: Contact = action.payload;
    const result = [
      addContact(contact),
      addContactToCart(contact),
      changePhaseRequest(Phase.EditContact)
    ];
    return result;
  });
};

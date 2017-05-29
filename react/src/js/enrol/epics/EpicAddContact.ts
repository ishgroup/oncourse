import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {ADD_CONTACT, ADD_CONTACT_REQUEST} from "../containers/contact-add/actions/Actions";
import {Phase} from "../reducers/State";
import {changePhaseRequest} from "../actions/Actions";
import {Actions as WebActions} from "../../web/actions/Actions";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {normalize} from "normalizr";
import {ContactsSchema} from "../../NormalizeSchema";
import {Contact} from "../../model/web/Contact";

let addContact = function (contact: Contact) {
  return {type: ADD_CONTACT, payload: normalize(contact, ContactsSchema)};
};
let addContactToCart = function (contact: Contact) {
  return {type: FULFILLED(WebActions.REQUEST_CONTACT), payload: contact};
};
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

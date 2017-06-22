import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {addContact, SUBMIT_ADD_CONTACT, SUBMIT_ADD_CONTACT_AS_PAYER, SubmitContact} from "../actions/Actions";
import {setPayer, setNewContactFlag} from "../../../actions/Actions";
import {addContact as addContactToCart} from "../../../../web/actions/Actions";
import {IshState} from "../../../../services/IshState";
import {openEditContact} from "../../contact-edit/actions/Actions";
import CheckoutService from "../../../services/CheckoutService";


/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const SubmitAddContact: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(SUBMIT_ADD_CONTACT).flatMap((action) => {
    const payload: SubmitContact = action.payload;
    const state: IshState = store.getState();

    if (CheckoutService.hasPayer(state.checkout)) {
      return [
        addContact(payload.contact),
        setNewContactFlag(payload.newContact),
        openEditContact(payload.contact),
      ];
    } else {
      return [
        addContact(payload.contact),
        setNewContactFlag(payload.newContact),
        setPayer(payload.contact.id),
        addContactToCart(payload.contact),
        openEditContact(payload.contact),
      ];
    }
  });
};

export const SubmitAddContactAsPayer: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(SUBMIT_ADD_CONTACT_AS_PAYER).flatMap((action) => {
    const payload: SubmitContact = action.payload;

    return [
      addContact(payload.contact),
      setNewContactFlag(payload.newContact),
      setPayer(payload.contact.id),
      openEditContact(payload.contact),
    ];
  });
};


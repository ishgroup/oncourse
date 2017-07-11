import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {
  ADD_PAYER_FROM_VOUCHER, addContact, SUBMIT_ADD_CONTACT, SubmitContact,
} from "../actions/Actions";
import {setPayer, setNewContactFlag, updateContactAddProcess} from "../../../actions/Actions";
import {IshState} from "../../../../services/IshState";
import {openEditContact} from "../../contact-edit/actions/Actions";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const SubmitAddContact: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(SUBMIT_ADD_CONTACT).flatMap(action => {
    const payload: SubmitContact = action.payload;
    const state: IshState = store.getState();

    return [
      updateContactAddProcess(payload.contact, state.checkout.phase),
      setNewContactFlag(payload.newContact),
      openEditContact(payload.contact),
    ];
  });
};

export const AddPayerFromVoucher: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(ADD_PAYER_FROM_VOUCHER).flatMap(action => {
    const payload: SubmitContact = action.payload;

    return [
      addContact(payload.contact),
      setNewContactFlag(payload.newContact),
      setPayer(payload.contact.id),
    ];
  });
};
import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {
  ADD_CONTACT_REQUEST, addAdditionalContact, addContact, AddContactPayload,
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
    console.log(action);
    const payload: AddContactPayload = action.payload;
    const payer = store.getState().checkout.payer;
    const isAdditional = payer && payer.entity;

    if (isAdditional) {
      return [
        addAdditionalContact(payload.contact),
        setNewContactFlag(payload.newContact),
        changePhaseRequest(Phase.EditContact),
      ];
    } else {
      return [
        addContact(payload.contact),
        addContactToCart(payload.contact),
        setNewContactFlag(payload.newContact),
        changePhaseRequest(Phase.EditContact),
      ];
    }
  });
};


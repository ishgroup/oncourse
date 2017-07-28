import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";

import {ADD_CONTACT_TO_SUMMARY, getContactNodeFromBackend} from "../actions/Actions";
import {
  changePhase, getCheckoutModelFromBackend, setPayer, updateContactAddProcess,
  updateParentChilds,
} from "../../../actions/Actions";
import {addContact as addContactToCart} from "../../../../web/actions/Actions";

import {IAction} from "../../../../actions/IshAction";
import {Contact} from "../../../../model";
import CheckoutService from "../../../services/CheckoutService";
import {addContact} from "../../contact-add/actions/Actions";
import {Phase} from "../../../reducers/State";


export const AddContactToSummary: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(ADD_CONTACT_TO_SUMMARY).flatMap((action: IAction<Contact>) => {
    const state: IshState = store.getState();

    const {type, parent} = state.checkout.contactAddProcess;
    const contact = action.payload;
    const payerId = (!contact.parentRequired && contact.id) || (parent && parent.id) || null;
    const ifParentRequired = contact.parentRequired && !parent;

    const allChilds = CheckoutService
      .getAllSingleChildIds(state.checkout)
      .concat(ifParentRequired ? contact.id : []);

    const result = [];

    if (!contact.id) return [changePhase(state.checkout.page)];

    if (!CheckoutService.hasPayer(state.checkout)) {
      result.push(setPayer(payerId));
      result.push(addContactToCart(contact));
    }

    if ((type === Phase.AddContactAsPayer || type === Phase.AddContactAsCompany) && payerId) {
      result.push(setPayer(payerId));
    }

    if (allChilds.length && !contact.parentRequired) {
      result.push(updateParentChilds(contact.id, allChilds));
    }

    if (parent) {
      result.push(addContact(parent));
      result.push(getContactNodeFromBackend(parent));
    }

    result.push(addContact({...contact, parentRequired: (contact.parentRequired && !parent)}));
    result.push(getContactNodeFromBackend(action.payload));
    result.push(getCheckoutModelFromBackend());
    result.push(updateContactAddProcess({}, null, null));
    result.push(changePhase(state.checkout.page));

    return result;
  });
};

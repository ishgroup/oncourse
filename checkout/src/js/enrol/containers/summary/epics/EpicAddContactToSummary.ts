import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import {ADD_CONTACT_TO_SUMMARY, getContactNodeFromBackend} from "../actions/Actions";
import {
  changePhase, setPayer, updateContactAddProcess, updateParentChilds,
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

    const {type, parent, forChild} = state.checkout.contactAddProcess;
    const contact = action.payload;
    const payerId = ((!contact.parentRequired || parent) && contact.id) || null;
    const ifParentRequired = contact.parentRequired && !parent;

    const allChilds = CheckoutService
      .getAllSingleChildIds(state.checkout)
      .concat(ifParentRequired ? contact.id : []);

    // Uncheck all enrolments for contact. Cases:
    // - if new contact is a parent/guardian for existing children
    // - if new contact is a payer
    const uncheckContactItems: boolean =
      (allChilds.length && !contact.parentRequired) ||
      (type === Phase.AddContactAsPayer || type === Phase.AddContactAsCompany);

    const result = [];

    if (!contact.id) return [changePhase(state.checkout.page)];

    // case: Set payer if it didn't exist
    if (!CheckoutService.hasPayer(state.checkout)) {
      result.push(setPayer(payerId));
      result.push(addContactToCart(contact));
    }

    // case: Force set payer from payment screen if payer age is valid or have parent
    if ((type === Phase.AddContactAsPayer || type === Phase.AddContactAsCompany) && payerId) {
      result.push(setPayer(payerId));
    }

    // case: Update parent for existing children
    if (allChilds.length || forChild && !contact.parentRequired) {
      result.push(updateParentChilds(contact.id, forChild ? [forChild] : allChilds));
    }

    // case: add new contact to summary and update parentRequired flag
    result.push(addContact({...contact, parent: parent || null, parentRequired: (contact.parentRequired && !parent)}));

    result.push(getContactNodeFromBackend(contact, uncheckContactItems));
    result.push(updateContactAddProcess({}, null, null));
    result.push(changePhase(state.checkout.page));

    return result;
  });
};

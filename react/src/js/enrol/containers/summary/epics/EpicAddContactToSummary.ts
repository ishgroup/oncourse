import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";

import {ADD_CONTACT_TO_SUMMARY, getContactNodeFromBackend} from "../actions/Actions";
import {
  changePhase, getCheckoutModelFromBackend, setParent, setPayer, updateContactAddProcess,
  updateParentChilds,
} from "../../../actions/Actions";
import {addContact as addContactToCart} from "../../../../web/actions/Actions";

import {IAction} from "../../../../actions/IshAction";
import {Contact} from "../../../../model/web/Contact";
import CheckoutService from "../../../services/CheckoutService";
import {addContact} from "../../contact-add/actions/Actions";
import {Phase} from "../../../reducers/State";


export const AddContactToSummary: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(ADD_CONTACT_TO_SUMMARY).flatMap((action: IAction<Contact>) => {
    const state: IshState = store.getState();
    const type = store.getState().checkout.contactAddProcess.type;
    const contact = action.payload;
    const newParentId = !contact.parentRequired ? state.checkout.parentId || contact.id : state.checkout.parentId;

    const allChilds = CheckoutService.getAllChildIds(state.checkout).concat(contact.parentRequired ? contact.id : []);

    const result = [];

    if (!contact.id) return [changePhase(store.getState().checkout.page)];

    if (!CheckoutService.hasPayer(state.checkout)) {
      result.push(setPayer(contact.parentRequired ? null : contact.id));
      result.push(addContactToCart(contact));
    }

    if ((type === Phase.AddContactAsPayer || type === Phase.AddContactAsCompany) && !contact.parentRequired) {
      result.push(setPayer(contact.id));
    }

    if (allChilds.length && newParentId) {
      result.push(updateParentChilds(newParentId, allChilds));
    }

    result.push(setParent(newParentId));
    result.push(addContact(contact));
    result.push(getContactNodeFromBackend(action.payload));
    result.push(getCheckoutModelFromBackend());
    result.push(updateContactAddProcess({}, null));
    result.push(changePhase(store.getState().checkout.page));

    return result;
  });
};



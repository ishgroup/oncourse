import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";

import {IAction} from "../../actions/IshAction";
import {Contact} from "../../model";
import {
  removeContact,
  EPIC_REMOVE_CONTACT,
  setPayer,
  removeRedeemVoucher,
  changePhase,
  getCheckoutModelFromBackend
} from "../actions/Actions";
import {getAllContactNodesFromBackend, removeContactFromSummary} from "../containers/summary/actions/Actions";
import {Phase} from "../reducers/State";


export const EpicRemoveContact: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(EPIC_REMOVE_CONTACT).flatMap((action: IAction<Contact>) => {
    const state: IshState = store.getState();
    const result = [];
    const {contactId} = action.payload as { contactId: string };

    // If contact is a payer and not last. Set payer to next available contact
    if (state.checkout.payerId === contactId && state.checkout.summary.result.length > 1) {
      const nextPayerId = state.checkout.summary.result.find(id => id !== contactId);
      result.push(setPayer(nextPayerId));
    }

    // If contact is last. Set payer to null and redirect
    if (state.checkout.summary.result.length <= 1) {
      result.push(setPayer(null));
      result.push(changePhase(Phase.AddContact));
    }

    // If contact have redeem vouchers. Remove redeem vouchers
    const voucher = state.checkout.redeemVouchers &&
      state.checkout.redeemVouchers.length &&
      state.checkout.redeemVouchers.find(v => v.payer && v.payer.id === contactId);

    if (voucher) {
      result.push(removeRedeemVoucher(voucher));
    }

    result.push(removeContact(contactId));
    result.push(removeContactFromSummary(contactId));
    result.push(getCheckoutModelFromBackend());

    return result;
  });
};

import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";

import {IAction} from "../../actions/IshAction";
import {Contact} from "../../model";
import CheckoutService from "../services/CheckoutService";
import {Phase} from "../reducers/State";
import {removeContact, EPIC_REMOVE_CONTACT} from "../actions/Actions";
import {getAllContactNodesFromBackend, removeContactFromSummary} from "../containers/summary/actions/Actions";


export const EpicRemoveContact: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(EPIC_REMOVE_CONTACT).flatMap((action: IAction<Contact>) => {
    const state: IshState = store.getState();
    const result = [];
    const {contactId} = action.payload as {contactId: string};

    result.push(removeContact(contactId));
    result.push(removeContactFromSummary(contactId));
    result.push(getAllContactNodesFromBackend());

    return result;
  });
};

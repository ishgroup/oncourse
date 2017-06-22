import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";

import {ADD_CONTACT_TO_SUMMARY, getContactNodeFromBackend} from "../actions/Actions";
import {changePhase, getCheckoutModelFromBackend} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";
import {IAction} from "../../../../actions/IshAction";
import {Contact} from "../../../../model/web/Contact";


export const AddContactToSummary: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(ADD_CONTACT_TO_SUMMARY).flatMap((action:IAction<Contact>) => {
    return [
      getContactNodeFromBackend(action.payload),
      getCheckoutModelFromBackend(),
      changePhase(store.getState().checkout.page),
    ];
  });
};



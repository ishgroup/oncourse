import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import * as Actions from "../containers/contact-edit/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {changePhase, changePhaseRequest, MessagesSet} from "../actions/Actions";
import {ContactFields} from "../../model/field/ContactFields";
import {Phase} from "../reducers/State";

const OpenContactDetailsEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$
    .ofType(Actions.OpenContactDetailsRequest)
    .mergeMap(action => Observable.fromPromise(
      CheckoutService.loadFields(store.getState().checkout.payer, store.getState().classes, store.getState().products)
    ))
    .flatMap((data: ContactFields) => {
      if (data.headings.length > 0) {
        return [changePhase(Phase.EditContactDetails), {type: Actions.FieldsLoad, payload: data}];
      } else {
        return [changePhaseRequest(Phase.Summary)]
      }
    }).catch((data) => {
      return Observable.of({type: MessagesSet, payload: data.data});
    });
};
export default OpenContactDetailsEpic;


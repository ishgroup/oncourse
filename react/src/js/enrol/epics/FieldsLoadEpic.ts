import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import * as Actions from "../containers/contact-edit/actions/Actions";
import {loadFields} from "../services/CheckoutSerivce";
import {MessagesSet} from "../actions/Actions";

const FieldsLoadEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$
    .ofType(Actions.FieldsLoadRequest)
    .mergeMap(action => Observable.fromPromise(
      loadFields(store.getState().checkout.payer, store.getState().classes, store.getState().products)
    ))
    .map((data) => {
      return {type: Actions.FieldsLoad, payload: data};
    }).catch((data) => {
      return Observable.of({type: MessagesSet, payload: data.data});
    });
};

export default FieldsLoadEpic;


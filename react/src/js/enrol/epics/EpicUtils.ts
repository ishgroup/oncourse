import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {MessagesShow} from "../actions/Actions";
import {toValidationError} from "../../common/utils/ErrorUtils";


export interface Request<V, S> {
  type: string,
  getData: (payload: any, state: S) => Promise<V>
  processData: (value: V, state: S) => { type: string; payload?: any }[]
  processError?: (data: any) => { type: string, payload: any }
}


const ProcessError = (data: any): { type: string, payload: any } => {
  return {type: MessagesShow, payload: toValidationError(data)}
};

export const Create = <V, S>(request: Request<V, S>): Epic<any, any> => {
  return (action$: ActionsObservable<any>, store: MiddlewareAPI<S>): Observable<any> => {
    return action$
      .ofType(request.type)
      .mergeMap(action => Observable.fromPromise(request.getData(action.payload, store.getState())))
      .flatMap(data => request.processData(data, store.getState()))
      .catch((data) => {
        return Observable.of(request.processError ? request.processError(data) : ProcessError(data));
      });
  };
};
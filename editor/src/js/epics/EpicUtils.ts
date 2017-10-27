import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from 'rxjs/Rx';
import Notifications, {success, error} from 'react-notification-system-redux';
import "rxjs";
import {commonErrorToValidationError, toValidationError} from "../common/utils/ErrorUtils";
import {AxiosResponse} from "axios";
import {IAction} from "../actions/IshAction";


export interface Request<V, S> {
  type: string;
  getData: (payload: any, state: S) => Promise<V>;
  processData: (value: V, state: S, payload?: any) => IAction<any>[] | Observable<any>;
  processError?: (data: any) => IAction<any>[] | Observable<any>;
}


export const showCommonError = (error: any): { type: string, payload: any } => {
  // return error({
  // uid: 'once-please', // you can specify your own uid if required
  //   title: 'Save failed',
  //   message: 'Something went wrong',
  //   position: 'tr',
  //   autoDismiss: 5,
  // });
  return {type: 'SHOW_MESSAGES', payload: commonErrorToValidationError(error)};
};

export const ProcessError = (data: AxiosResponse): { type: string, payload?: any }[] => {
  console.log(data);
  return [error({
    // uid: 'once-please', // you can specify your own uid if required
    title: 'Save failed',
    message: (data.data && data.data.message) || 'Something went wrong',
    position: 'tr',
    autoDismiss: 3,
  })];
  // return [{type: 'SHOW_MESSAGES', payload: toValidationError(data)}];
};


export const Create = <V, S>(request: Request<V, S>): Epic<any, any> => {
  return (action$: ActionsObservable<any>, store: MiddlewareAPI<S>): Observable<any> => {
    return action$
      .ofType(request.type).mergeMap(action => Observable
        .fromPromise(request.getData(action.payload, store.getState()))
        .flatMap(data => request.processData(data, store.getState(), action.payload))
        .catch(data => {
          return request.processError ? request.processError(data) : ProcessError(data);
        }),
      );
  };
};

export const Reply = <V, S>(request: Request<V, S>, retry: number): Epic<any, any> => {
  return (action$: ActionsObservable<any>, store: MiddlewareAPI<S>): Observable<any> => {
    return action$
      .ofType(request.type).mergeMap((action: IAction<any>) => Observable
        .defer(() => request.getData(action.payload, store.getState())).retry(retry)
        .flatMap((data: V) => request.processData(data, store.getState()))
        .catch(data => request.processError(data)),
      );
  };
};

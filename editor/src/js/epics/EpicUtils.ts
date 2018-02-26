import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from 'rxjs/Rx';
import Notifications, {success, error} from 'react-notification-system-redux';
import "rxjs";
import {AxiosResponse} from "axios";
import {IAction} from "../actions/IshAction";
import {SERVER_ERROR} from "../common/actions";
import {LOG_OUT_REQUEST} from "../containers/auth/actions/index";


export interface Request<V, S> {
  type: string;
  getData: (payload: any, state: S) => Promise<V>;
  processData: (value: V, state: S, payload?: any) => IAction<any>[] | Observable<any>;
  processError?: (data: any) => IAction<any>[] | Observable<any>;
}

export const errorMessage = data => (
  error({
    title: 'Request Failed',
    message: (data && data.data && data.data.message) || 'Something went wrong',
    position: 'tr',
    autoDismiss: 3,
  })
);

export const ProcessError = (data: AxiosResponse): { type: string, payload?: any }[] => {

  if (data && data.status && 401 === data.status) {
    return [{type: LOG_OUT_REQUEST}];
  }

  return [
    {type: SERVER_ERROR},
    errorMessage(data),
  ];
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

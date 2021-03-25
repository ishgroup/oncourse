import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic, StateObservable} from "redux-observable";
import {error} from 'react-notification-system-redux';
import "rxjs";
import {AxiosResponse} from "axios";
import {IAction} from "../actions/IshAction";
import {SERVER_ERROR} from "../common/actions";
import {LOG_OUT_REQUEST} from "../containers/auth/actions/index";
import {catchError, flatMap, mergeMap, retry} from "rxjs/operators";
import {defer, from, Observable} from "rxjs";


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
  return (action$: ActionsObservable<any>, state$: StateObservable<S>): Observable<any> => {
    return action$
      .ofType(request.type).pipe(
        mergeMap(action =>
          from(request.getData(action.payload, state$.value)).pipe(
            mergeMap(data => request.processData(data, state$.value, action.payload)),
            catchError(data => request.processError ? request.processError(data) : ProcessError(data)),
          ),
        ),
      );
  };
};

export const Reply = <V, S>(request: Request<V, S>, retryTimes: number): Epic<any, any> => {
  return (action$: ActionsObservable<any>, state$: StateObservable<S>): Observable<any> => {
    return action$
      .ofType(request.type).pipe(
        mergeMap((action: IAction<any>) =>
          from(() =>
            request.getData(action.payload, state$.value),
          ).pipe(
            retry(retryTimes),
            mergeMap((data: V) => request.processData(data, state$.value)),
            catchError(data => request.processError(data)),
          ),
        ),
      );
  };
};

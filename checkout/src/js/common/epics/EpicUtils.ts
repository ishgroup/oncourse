import {FULFILLED, REJECTED} from "../actions/ActionUtils";
import {Observable} from "rxjs";
import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import "rxjs";
import {SHOW_MESSAGES} from "../../enrol/actions/Actions";
import {commonErrorToValidationError, toValidationError} from "../utils/ErrorUtils";
import {AxiosResponse} from "axios";
import {CommonError} from "../../model/index";
import {IAction} from "../../actions/IshAction";

export function mapPayload(actionType: string) {
    return function (payload: any) {
        return {
            type: FULFILLED(actionType),
            payload,
        };
    };
}

export function mapError(actionType: string) {
    return function (payload: any) {
        return Observable.of({
            type: REJECTED(actionType),
            payload,
            error: true,
        });
    };
}

export interface Request<V, S, P = any> {
  type: string;
  getData: (payload: any, state: S) => Promise<V>;
  processData: (value: V, state: S, payload?: P) => IAction<any>[] | Observable<any>;
  processError?: (data: any) => IAction<any>[] | Observable<any>;
}


export const showCommonError = (error: CommonError): { type: string, payload: any } => {
  return {type: SHOW_MESSAGES, payload: commonErrorToValidationError(error)};
};

export const ProcessError = (data: AxiosResponse): { type: string, payload?: any }[] => {
  return [{type: SHOW_MESSAGES, payload: toValidationError(data)}];
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

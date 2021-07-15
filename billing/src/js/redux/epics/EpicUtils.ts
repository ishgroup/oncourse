/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ActionsObservable, Epic, ofType, StateObservable } from 'redux-observable';
import { concat, from, Observable } from 'rxjs';
import { catchError, delay, flatMap, mergeMap } from 'rxjs/operators';
import { IAction } from '../actions/IshAction';
import { FETCH_FINISH, FETCH_START } from '../actions';
import FetchErrorHandler from '../../api/fetch-errors-handlers/FetchErrorHandler';
import { REJECTED } from '../actions/ActionUtils';
import { EnvironmentConstants } from '../../constant/EnvironmentConstants';
import { State } from '../reducers';

export interface Request<V = any, P = any> {
  type: string;
  hideLoadIndicator?: boolean;
  getData: (payload: P, state: State) => Promise<V>;
  retrieveData?: (payload: any, state: State) => Promise<V>;
  processData: (value: V, state: State, payload?: P) => IAction<any>[] | Observable<any>;
  processError?: (data: any, payload?: P) => IAction<any>[] | Observable<any>;
}

export interface DelayedRequest<V, P> extends Request<V, P> {
  delay: number;
}

export const processError = (data: any, type: string, processError: any, payload: any): IAction<any>[] => [
  ...(process.env.NODE_ENV === EnvironmentConstants.development
    ? [
      {
        type: REJECTED(type),
        payload
      }
    ]
    : []),
  ...(processError ? processError(data, payload) : FetchErrorHandler(data))
];

export const CreateWithTimeout = <V, P>(request: DelayedRequest<V, P>): Epic<any, any> => (action$: ActionsObservable<any>, state$: StateObservable<State>): Observable<any> => action$.pipe(
  ofType(request.type),
  delay(request.delay),
  mergeMap((action) => concat(
    [
      {
        type: FETCH_START,
        payload: {
          hideIndicator: request.hideLoadIndicator
        }
      }
    ],
    from(request.getData(action.payload, state$.value)).pipe(
      flatMap((data) => (request.retrieveData ? request.retrieveData(action.payload, state$.value) : [data])),
      flatMap((data) => request.processData(data, state$.value, action.payload)),
      catchError((data) => processError(data, request.type, request.processError, action.payload))
    ),
    [
      {
        type: FETCH_FINISH
      }
    ]
  ))
);

export const Create = <V, P>(request: Request<V, P>): Epic<any, any, any, any> => (action$: ActionsObservable<any>, state$: StateObservable<State>): Observable<any> => action$.pipe(
  ofType(request.type),
  mergeMap((action) => concat(
    [
      {
        type: FETCH_START,
        payload: {
          hideIndicator: request.hideLoadIndicator
        }
      }
    ],
    from(request.getData(action.payload, state$.value)).pipe(
      flatMap((data) => (request.retrieveData ? request.retrieveData(action.payload, state$.value) : [data])),
      flatMap((data) => request.processData(data, state$.value, action.payload)),
      catchError((data) => processError(data, request.type, request.processError, action.payload))
    ),
    [
      {
        type: FETCH_FINISH
      }
    ]
  ))
);

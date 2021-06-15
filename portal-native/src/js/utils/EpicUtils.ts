/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  ActionsObservable, Epic, ofType, StateObservable,
} from 'redux-observable';
import { concat, from, Observable } from 'rxjs';
import {
  catchError, delay, mergeMap,
} from 'rxjs/operators';
import { IAction } from '../model/IshAction';
import { State } from '../model/State';
import { FETCH_FINISH, FETCH_START } from '../actions/FetchActions';
import { REJECTED } from './ActionUtils';
import { FetchErrorHandler } from './ApiUtils';

export interface Request<V = any, P = any> {
  type: string;
  delay?: number;
  hideLoadIndicator?: boolean;
  getData: (payload: P, state: State) => Promise<V>;
  retrieveData?: (payload: any, state: State) => Promise<V>;
  processData: (value: V, state: State, payload?: P) => IAction<any>[] | Observable<any>;
  processError?: (data: any, payload?: P) => IAction<any>[] | Observable<any>;
}

export const processError = (
  data: any,
  type: string,
  processError: any,
  payload: any,
): IAction<any>[] => [
  ...(__DEV__
    ? [
      {
        type: REJECTED(type),
        payload,
      },
    ]
    : []),
  ...(processError ? processError(data, payload) : FetchErrorHandler(data)),
];

export const createRequest = <V, P>(request: Request<V, P>): Epic<IAction<P>> => (
  action$: ActionsObservable<any>,
  state$: StateObservable<State>,
): Observable<any> => action$.pipe(
  ofType(request.type),
  delay(request.delay || 0),
  mergeMap((action) => concat(
    [
      {
        type: FETCH_START,
        payload: {
          hideIndicator: request.hideLoadIndicator,
        },
      },
    ],
    from(request.getData(action.payload, state$.value)).pipe(
      mergeMap((data) => (request.retrieveData
        ? request.retrieveData(action.payload, state$.value)
        : [data])),
      mergeMap((data) => request.processData(data, state$.value, action.payload)),
      catchError((data) => processError(data, request.type, request.processError, action.payload)),
    ),
    [
      {
        type: FETCH_FINISH,
      },
    ],
  )),
);

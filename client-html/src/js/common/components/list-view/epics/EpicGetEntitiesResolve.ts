/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic, ofType } from "redux-observable";
import { mergeMap } from "rxjs/operators";
import { Observable } from "rxjs";
import { GET_RECORDS_FULFILLED_RESOLVE } from "../actions";

export const EpicGetEntitiesResolve: Epic<any> = (
  action$: Observable<any>,
): Observable<any> => action$.pipe(
  ofType(GET_RECORDS_FULFILLED_RESOLVE),
  mergeMap(({ payload: { resolve } }) => {
    resolve();

    return [];
  })
);

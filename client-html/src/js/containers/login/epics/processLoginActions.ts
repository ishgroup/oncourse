/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */


import { LoginResponse } from "@api/model";
import { CLEAR_LAST_LOCATION, FETCH_SUCCESS } from "../../../common/actions";
import { IAction } from "../../../common/actions/IshAction";
import history from "../../../constants/History";
import { State } from "../../../reducers/state";
import { POST_AUTHENTICATION_FULFILLED } from "../actions";

export const processLoginActions = (data: LoginResponse, state: State ): IAction[] => {

  if (state.lastLocation) {
    history.push(state.lastLocation);
  }

  if (data.lastLoginOn) localStorage.setItem("lastLoginOn", data.lastLoginOn);

  return [
    ...(state.lastLocation
      ? [
        {
          type: CLEAR_LAST_LOCATION
        }
      ]
      : []),
    {
      type: POST_AUTHENTICATION_FULFILLED,
      payload: true
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "You have logged in" }
    },
  ];
}
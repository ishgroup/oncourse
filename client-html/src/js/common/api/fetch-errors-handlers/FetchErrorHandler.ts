/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { APPLICATION_THEME_STORAGE_NAME, DASHBOARD_ACTIVITY_STORAGE_NAME } from "../../../constants/Config";
import { FETCH_FAIL, SET_LAST_LOCATION } from "../../actions";
import { IAction } from "../../actions/IshAction";
import history from "../../../constants/History";
import { State } from "../../../reducers/state";
import store from "../../../constants/Store";
import { GET_IS_LOGGED_FULFILLED } from "../../../containers/preferences/actions";
import { ServerResponse } from "../../../model/common/apiHandlers";
import { LSRemoveItem } from "../../utils/storage";

const FetchErrorHandler = (response: ServerResponse, customMessage?: string): IAction<any>[] => {
  if (!response) {
    return [
      {
        type: FETCH_FAIL,
        payload: { message: customMessage || "Something went wrong" }
      }
    ];
  }

  const { data, status } = response;

  switch (status) {
    case 400:
      return [
        {
          type: FETCH_FAIL,
          payload: {
            formError: data,
            message: data.errorMessage || customMessage
          }
        }
      ];

    //  Redirect if not logged in
    case 401:
      const state: State = store.getState();
      const lastLocation = state.lastLocation || window.location.pathname;
      LSRemoveItem(APPLICATION_THEME_STORAGE_NAME);
      LSRemoveItem(DASHBOARD_ACTIVITY_STORAGE_NAME);
      history.push("/login");

      return [
        {
          type: SET_LAST_LOCATION,
          payload: lastLocation
        },
        {
          type: GET_IS_LOGGED_FULFILLED,
          payload: false
        },
        ...(data["url"] && data["url"] === "/a/"
          ? []
          : [
              {
                type: FETCH_FAIL,
                payload: { message: "Unauthorized" }
              }
            ])
      ];

    case 403:
      return [
        {
          type: FETCH_FAIL,
          payload: {
            message: data.errorMessage || customMessage
          }
        }
      ];

    default:
      console.error(response);

      return [
        {
          type: FETCH_FAIL,
          payload: { message: customMessage || "Something went wrong" }
        }
      ];
  }
};

export default FetchErrorHandler;

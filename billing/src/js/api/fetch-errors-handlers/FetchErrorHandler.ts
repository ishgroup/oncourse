/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FETCH_FAIL } from "../../redux/actions";
import { IAction } from "../../redux/actions/IshAction";

const FetchErrorHandler = (response: any, customMessage?: string): IAction<any>[] => {
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

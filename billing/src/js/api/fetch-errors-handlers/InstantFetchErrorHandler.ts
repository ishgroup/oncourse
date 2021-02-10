/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SHOW_MESSAGE } from "../../redux/actions/index";

const instantFetchErrorHandler = (
  response: any,
  customMessage: string = "Something went wrong"
) => {
  if (!response) {
    return ([{
      type: SHOW_MESSAGE,
      payload: { message: customMessage || "Something went wrong", error: true }
    }]);
  }

  const { data, status } = response;

  if (status) {
    return ([{
      type: SHOW_MESSAGE,
      payload: {
        message: (data && data.errorMessage) || customMessage,
        error: true
      }
    }]);
  } else {
    return ([{
      type: SHOW_MESSAGE,
      payload: {
        message: response,
        error: true
      }
    }]);
  }
};

export default instantFetchErrorHandler;

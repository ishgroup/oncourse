/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Dispatch } from "redux";
import { ServerResponse } from "../../../model/common/apiHandlers";
import { SHOW_MESSAGE } from "../../actions";

const instantFetchErrorHandler = (
  dispatch: Dispatch,
  response: ServerResponse | Error,
  customMessage: string = "Something went wrong"
) => {
  if (!response) {
    dispatch({
      type: SHOW_MESSAGE,
      payload: {message: customMessage}
    });
    return;
  }

  if (response instanceof Error) {
    throw response;
  }

  const {data, status} = response;

  if (status) {
    dispatch({
      type: SHOW_MESSAGE,
      payload: {
        message: (data && data.errorMessage) || customMessage
      }
    });
  } else {
    dispatch({
      type: SHOW_MESSAGE,
      payload: {
        message: typeof response === "string" ? response : customMessage
      }
    });
  }
};

export default instantFetchErrorHandler;

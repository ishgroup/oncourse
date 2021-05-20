/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DELETE_APPLICATION_ITEM, DELETE_APPLICATION_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import ApplicationService from "../service/ApplicationService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: DELETE_APPLICATION_ITEM,
  getData: (id: number) => ApplicationService.removeApplication(id),
  processData: () => {
    return [
      {
        type: DELETE_APPLICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Application record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Application", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, "Application record was not deleted")
};

export const EpicDeleteApplication: Epic<any, any> = EpicUtils.Create(request);

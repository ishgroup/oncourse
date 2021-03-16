/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Application } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { processCustomFields } from "../../customFieldTypes/utils";
import { CREATE_APPLICATION_ITEM, CREATE_APPLICATION_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import ApplicationService from "../service/ApplicationService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: Application;

const request: EpicUtils.Request = {
  type: CREATE_APPLICATION_ITEM,
  getData: payload => {
    savedItem = payload.application;
    processCustomFields(payload.application);
    return ApplicationService.createApplication(payload.application);
  },
  processData: () => [
      {
        type: CREATE_APPLICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Application Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Application" }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: response => [
    ...FetchErrorHandler(response, "Application Record was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateApplication: Epic<any, any> = EpicUtils.Create(request);

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Application } from "@api/model";
import { initialize } from "redux-form";
import { clearActionsQueue } from "../../../../common/actions";
import { getNoteItems } from "../../../../common/components/form/notes/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_APPLICATION_ITEM, GET_APPLICATION_ITEM_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ApplicationService from "../service/ApplicationService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<Application, number> = {
  type: GET_APPLICATION_ITEM,
  getData: id => ApplicationService.getApplication(id),
  processData: (application, s, id) => [
      {
        type: GET_APPLICATION_ITEM_FULFILLED,
        payload: { application }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: application, name: application.courseName }
      },
      getNoteItems("Application", id, LIST_EDIT_VIEW_FORM_NAME),
      initialize(LIST_EDIT_VIEW_FORM_NAME, application),
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
    ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetApplication: Epic<any, any> = EpicUtils.Create(request);

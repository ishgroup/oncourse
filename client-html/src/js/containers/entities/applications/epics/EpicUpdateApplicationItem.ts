/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Application } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { processCustomFields } from "../../customFieldTypes/utils";
import { GET_APPLICATION_ITEM, UPDATE_APPLICATION_ITEM, UPDATE_APPLICATION_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import ApplicationService from "../service/ApplicationService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; application: Application & { notes: any } }> = {
  type: UPDATE_APPLICATION_ITEM,
  getData: ({ id, application }) => {
    delete application.notes;
    processCustomFields(application);
    return ApplicationService.updateApplication(id, application);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_APPLICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Application Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Application", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView ? [{
        type: GET_APPLICATION_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { application }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, application)]
};

export const EpicUpdateApplicationItem: Epic<any, any> = EpicUtils.Create(request);

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Enrolment } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { processCustomFields } from "../../customFieldTypes/utils";
import { GET_ENROLMENT_ITEM, UPDATE_ENROLMENT_ITEM, UPDATE_ENROLMENT_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import EnrolmentService from "../services/EnrolmentService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; enrolment: Enrolment & { notes: any } }> = {
  type: UPDATE_ENROLMENT_ITEM,
  getData: ({ id, enrolment }) => {
    delete enrolment.notes;
    processCustomFields(enrolment);
    return EnrolmentService.updateEnrolment(id, enrolment);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_ENROLMENT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Enrolment Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Enrolment", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView ? [{
        type: GET_ENROLMENT_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { enrolment }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, enrolment)]
};

export const EpicUpdateEnrolmentItem: Epic<any, any> = EpicUtils.Create(request);

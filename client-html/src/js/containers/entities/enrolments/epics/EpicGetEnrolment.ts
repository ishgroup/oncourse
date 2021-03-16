/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Enrolment } from "@api/model";
import { initialize } from "redux-form";
import { clearActionsQueue } from "../../../../common/actions";
import { getNoteItems } from "../../../../common/components/form/notes/actions";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ENROLMENT_ITEM, GET_ENROLMENT_ITEM_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EnrolmentService from "../services/EnrolmentService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_ENROLMENT_ITEM,
  getData: (id: number) => EnrolmentService.getEnrolment(id),
  processData: (enrolment: Enrolment, s, id) => {
    if (enrolment.relatedFundingSourceId === null) {
      enrolment.relatedFundingSourceId = -1;
    }
    return [
      {
        type: GET_ENROLMENT_ITEM_FULFILLED,
        payload: { enrolment }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: enrolment, name: enrolment.studentName }
      },
      getNoteItems("Enrolment", id, LIST_EDIT_VIEW_FORM_NAME),
      initialize(LIST_EDIT_VIEW_FORM_NAME, enrolment),
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetEnrolment: Epic<any, any> = EpicUtils.Create(request);

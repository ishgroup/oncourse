/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Course } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { updateEntityItemById } from "../../common/entityItemsService";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { processCustomFields } from "../../customFieldTypes/utils";
import { GET_COURSE, UPDATE_COURSE } from "../actions";
import { ENTITY_NAME as CoursesEntity } from "../Courses";

const request: EpicUtils.Request<any, { id: number; course: Course }> = {
  type: UPDATE_COURSE,
  getData: ({ id, course }) => {
    processCustomFields(course);
    return updateEntityItemById(CoursesEntity, id, course);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${CoursesEntity} updated` }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: CoursesEntity, listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView ? [{
        type: GET_COURSE,
        payload: id
      }] : []
    ],
  processError: (response, { course }) => [
      ...FetchErrorHandler(response, `${CoursesEntity} was not updated`),
      initialize(LIST_EDIT_VIEW_FORM_NAME, course)
    ]
};

export const EpicUpdateCourse: Epic<any, any> = EpicUtils.Create(request);

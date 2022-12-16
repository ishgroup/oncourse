/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { reset } from "redux-form";
import { CourseClass } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { clearActionsQueue, FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { processCustomFields } from "../../customFieldTypes/utils";
import { GET_COURSE_CLASS, UPDATE_COURSE_CLASS } from "../actions";
import CourseClassService from "../services/CourseClassService";
import { processCourseClassApiActions } from "../utils";
import { getModifiedData } from "../../../../common/utils/common";
import { updateEntityDocuments } from "../../../../common/components/form/documents/actions";

const request: EpicUtils.Request<any, { id: number; courseClass: CourseClass & { documents } }> = {
  type: UPDATE_COURSE_CLASS,
  getData: async ({ id, courseClass }, s) => {
    processCustomFields(courseClass);
    const documents = [...courseClass.documents];
    delete courseClass.documents;
    await CourseClassService.updateCourseClass(id, courseClass);
    const syncActions = await processCourseClassApiActions(s);
    const actions = syncActions.filter(a => ![ "Note", "Document"].includes(a.entity));
    await processNotesAsyncQueue(actions);
    return { documents, actions };
  },
  processData: ({ documents, actions }, s, { id }) => {
    const modifiedDocs = getModifiedData(s.form[LIST_EDIT_VIEW_FORM_NAME]?.initial.documents, documents);

    return [
      ...actions.map(a => a.actionBody),
      ...modifiedDocs ? [updateEntityDocuments("CourseClass", id, modifiedDocs.map(d => d.id))] : [],
      {
        type: FETCH_SUCCESS,
        payload: { message: "Class updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CourseClass", listUpdate: true, ignoreSelection: true }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_COURSE_CLASS,
        payload: id
      }] : [],
      clearActionsQueue()
    ];
  },
  processError: response => [...FetchErrorHandler(response, "Class was not updated"), reset(LIST_EDIT_VIEW_FORM_NAME)]
};

export const EpicUpdateCourseClass: Epic<any, any> = EpicUtils.Create(request);

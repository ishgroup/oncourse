/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { CourseClass } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  GET_RECORDS_REQUEST,
  setListCreatingNew,
  setListFullScreenEditView,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { processCustomFields } from "../../customFieldTypes/utils";
import { CREATE_COURSE_CLASS } from "../actions";
import CourseClassService from "../services/CourseClassService";
import { processCourseClassApiActions } from "../utils";
import { QueuedAction } from "../../../../model/common/ActionsQueue";
import history from "../../../../constants/History";

let createdClassId = null;

const request: EpicUtils.Request<any, { courseClass: CourseClass }> = {
  type: CREATE_COURSE_CLASS,
  getData: ({ courseClass }) => {
    processCustomFields(courseClass);
    return CourseClassService.createCourseClass(courseClass).then(id => (createdClassId = id));
  },
  retrieveData: (p, s) => processCourseClassApiActions(s, createdClassId),
  processData: (actions: QueuedAction[]) => {
    const { pathname, search } = window.location;

    history.push({
      pathname: pathname.replace("new", createdClassId),
      search
    });

    return [
      ...actions.map(a => a.actionBody),
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CourseClass", ignoreSelection: true }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New class created" }
      },
      setListCreatingNew(false),
      setListSelection([createdClassId.toString()]),
      setListFullScreenEditView(true),
    ];
  },
  processError: response => FetchErrorHandler(response, "Class was not created")
};

export const EpicCreateCourseClass: Epic<any, any> = EpicUtils.Create(request);

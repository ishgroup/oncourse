import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CANCEL_COURSE_CLASS, CANCEL_COURSE_CLASS_FULFILLED } from "../actions";
import CourseClassService from "../services/CourseClassService";

const request: EpicUtils.Request = {
  type: CANCEL_COURSE_CLASS,
  getData: payload => CourseClassService.cancelClass(payload),
  processData: () => [
    {
      type: CANCEL_COURSE_CLASS_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Course class cancelled successfully" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "CourseClass", listUpdate: true }
    },
    setListSelection([]),
    initialize(LIST_EDIT_VIEW_FORM_NAME, null)
  ],
  processError: response => [...FetchErrorHandler(response, "Failed to cancel Course class")]
};

export const EpicCancelCourseClass: Epic<any, any> = EpicUtils.Create(request);

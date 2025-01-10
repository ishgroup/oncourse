import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { duplicateCourseClass, DUPLICATE_COURSE_CLASS_FULFILLED } from "../../../js/containers/entities/courseClasses/actions";
import { EpicDuplicateCourseClass } from "../../../js/containers/entities/courseClasses/epics/EpicDuplicateCourseClass";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";

describe("Duplicate course class epic tests", () => {
  it("EpicDuplicateCourseClass should returns correct values", () => DefaultEpic({
    action: mockedApi => duplicateCourseClass(mockedApi.db.getCourseClass(1)),
    epic: EpicDuplicateCourseClass,
    processData: () => [
      {
        type: DUPLICATE_COURSE_CLASS_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Course class duplicated successfully" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CourseClass" }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});

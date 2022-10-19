import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { cancelCourseClass, CANCEL_COURSE_CLASS_FULFILLED } from "../../../js/containers/entities/courseClasses/actions";
import { EpicCancelCourseClass } from "../../../js/containers/entities/courseClasses/epics/EpicCancelCourseClass";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";

describe("Cancel course class epic tests", () => {
  it("EpicCancelCourseClass should returns correct values", () => DefaultEpic({
    action: mockedApi => cancelCourseClass(mockedApi.db.cancelCourseClassPayload()),
    epic: EpicCancelCourseClass,
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
    ]
  }));
});

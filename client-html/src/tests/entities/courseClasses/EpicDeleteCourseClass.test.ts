import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { deleteCourseClass } from "../../../js/containers/entities/courseClasses/actions";
import { EpicDeleteCourseClass } from "../../../js/containers/entities/courseClasses/epics/EpicDeleteCourseClass";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";

describe("Delete course class epic tests", () => {
  it("EpicDeleteCourseClass should returns correct values", () => DefaultEpic({
    action: () => deleteCourseClass(1),
    epic: EpicDeleteCourseClass,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Class was deleted" }
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

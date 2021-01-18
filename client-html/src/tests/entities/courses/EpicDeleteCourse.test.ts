import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteCourse } from "../../../js/containers/entities/courses/epics/EpicDeleteCourse";
import { ENTITY_NAME as CoursesEntity } from "../../../js/containers/entities/courses/Courses";
import { deleteCourse } from "../../../js/containers/entities/courses/actions";

describe("Delete course epic tests", () => {
  it("EpicDeleteCourse should returns correct values", () => DefaultEpic({
    action: deleteCourse("1"),
    epic: EpicDeleteCourse,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${CoursesEntity} deleted` }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: CoursesEntity, listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});

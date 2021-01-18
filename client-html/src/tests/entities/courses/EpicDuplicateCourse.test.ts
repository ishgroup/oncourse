import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDuplicateCourse } from "../../../js/containers/entities/courses/epics/EpicDuplicateCourse";
import { duplicateCourses } from "../../../js/containers/entities/courses/actions";
import { ENTITY_NAME as CoursesEntity } from "../../../js/containers/entities/courses/Courses";

describe("Duplicate course epic tests", () => {
  it("EpicDuplicateCourse should returns correct values", () => DefaultEpic({
    action: duplicateCourses([1]),
    epic: EpicDuplicateCourse,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${CoursesEntity} duplicated successfully` }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: CoursesEntity }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});

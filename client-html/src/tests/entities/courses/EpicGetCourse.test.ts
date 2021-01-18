import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetCourse } from "../../../js/containers/entities/courses/epics/EpicGetCourse";
import { GET_COURSE_FULFILLED, getCourse } from "../../../js/containers/entities/courses/actions";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

describe("Get course epic tests", () => {
  it("EpicGetCourse should returns correct values", () => DefaultEpic({
    action: getCourse("1"),
    epic: EpicGetCourse,
    processData: mockedApi => {
      const course = mockedApi.db.getCourse(1);
      return [
        {
          type: GET_COURSE_FULFILLED,
          payload: { course }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: course, name: `${course.name} ${course.code}` }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, course),
        getNoteItems("Course", "1" as any, LIST_EDIT_VIEW_FORM_NAME)
      ];
    }
  }));
});

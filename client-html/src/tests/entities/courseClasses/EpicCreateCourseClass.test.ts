import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { createCourseClass } from "../../../js/containers/entities/courseClasses/actions";
import { EpicCreateCourseClass } from "../../../js/containers/entities/courseClasses/epics/EpicCreateCourseClass";
import {
  GET_RECORDS_REQUEST, setListCreatingNew, setListFullScreenEditView, setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";

describe("Create course class epic tests", () => {
  it("EpicCreateCourseClass should returns correct values", () => DefaultEpic({
    action: mockedApi => createCourseClass(mockedApi.db.getCourseClass(1)),
    epic: EpicCreateCourseClass,
    store: () => ({
      form: { [LIST_EDIT_VIEW_FORM_NAME]: { values: { tutors: [] } } }
    }),
    processData: mockedApi => [
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CourseClass" }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Class created" }
      },
    ]
  }));
});

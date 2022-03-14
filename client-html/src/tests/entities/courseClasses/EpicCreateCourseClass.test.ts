import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { createCourseClass } from "../../../js/containers/entities/courseClasses/actions";
import { EpicCreateCourseClass } from "../../../js/containers/entities/courseClasses/epics/EpicCreateCourseClass";
import {
  GET_RECORDS_REQUEST, setListCreatingNew, setListFullScreenEditView, setListSelection
} from "../../../js/common/components/list-view/actions";

describe("Create course class epic tests", () => {
  it("EpicCreateCourseClass should returns correct values", () => DefaultEpic({
    action: mockedApi => createCourseClass(mockedApi.db.getCourseClass(1)),
    epic: EpicCreateCourseClass,
    processData: mockedApi => [
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CourseClass", ignoreSelection: true }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New class created" }
      },
      setListCreatingNew(false),
      setListSelection([mockedApi.db.getCourseClassesTotalRows()]),
      setListFullScreenEditView(true),
    ]
  }));
});

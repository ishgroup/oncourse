import { DefaultEpic } from "../../common/Default.Epic";
import { updateCourseClass } from "../../../js/containers/entities/courseClasses/actions";
import { EpicUpdateCourseClass } from "../../../js/containers/entities/courseClasses/epics/EpicUpdateCourseClass";
import { FETCH_SUCCESS, clearActionsQueue } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";

describe("Update course class epic tests", () => {
  it("EpicUpdateCourseClass should returns correct values", () => DefaultEpic({
    action: mockedApi => updateCourseClass(1, mockedApi.db.getCourseClass(1)),
    epic: EpicUpdateCourseClass,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Class updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CourseClass", listUpdate: true, ignoreSelection: true }
      },
      clearActionsQueue()
    ]
  }));
});

import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateCourse } from "../../../js/containers/entities/courses/epics/EpicCreateCourse";
import { createCourse } from "../../../js/containers/entities/courses/actions";
import { ENTITY_NAME as CoursesEntity } from "../../../js/containers/entities/courses/Courses";

describe("Create course epic tests", () => {
  it("EpicCreateCourse should returns correct values", () => DefaultEpic({
    action: mockedApi => createCourse(mockedApi.db.createNewCourse()),
    epic: EpicCreateCourse,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: `New ${CoursesEntity} created` }
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

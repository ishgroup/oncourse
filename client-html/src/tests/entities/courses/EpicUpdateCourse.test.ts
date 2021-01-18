import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateCourse } from "../../../js/containers/entities/courses/epics/EpicUpdateCourse";
import { GET_COURSE, updateCourse } from "../../../js/containers/entities/courses/actions";
import { ENTITY_NAME as CoursesEntity } from "../../../js/containers/entities/courses/Courses";

describe("Update course epic tests", () => {
  it("EpicUpdateCourse should returns correct values", () => {
    const id = "2";
    return DefaultEpic({
      action: mockedApi => updateCourse(id, mockedApi.db.createNewCourse(id)),
      epic: EpicUpdateCourse,
      processData: () => [
        {
          type: FETCH_SUCCESS,
          payload: { message: `${CoursesEntity} updated` }
        },
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: CoursesEntity, listUpdate: true, savedID: id }
        },
        {
          type: GET_COURSE,
          payload: id
        }
      ]
    });
  });
});

import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";
import { ENTITY_NAME as CoursesEntity } from "../../../js/containers/entities/courses/Courses";

describe("Delete course epic tests", () => {
  it("EpicDeleteCourse should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, CoursesEntity),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions(CoursesEntity)
  }));
});

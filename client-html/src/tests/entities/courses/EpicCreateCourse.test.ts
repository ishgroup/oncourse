import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";
import { ENTITY_NAME as CoursesEntity } from "../../../js/containers/entities/courses/Courses";

describe("Create course epic tests", () => {
  it("EpicCreateCourse should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(CoursesEntity, mockedApi.db.createNewCourse()),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions(CoursesEntity)
  }));
});

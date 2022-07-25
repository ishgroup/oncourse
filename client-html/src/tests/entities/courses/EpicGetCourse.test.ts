import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get course epic tests", () => {
  it("EpicGetCourse should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getCourse(1), "Course"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getCourse(1), "Course", state)
  }));
});
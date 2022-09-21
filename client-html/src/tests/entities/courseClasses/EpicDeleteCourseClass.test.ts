import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete course class epic tests", () => {
  it("EpicDeleteCourseClass should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "CourseClass"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("CourseClass")
  }));
});
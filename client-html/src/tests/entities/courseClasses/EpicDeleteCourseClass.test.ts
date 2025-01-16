import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";
import { getListRecordAfterDeleteActions } from "../../../js/containers/entities/common/utils";

describe("Delete course class epic tests", () => {
  it("EpicDeleteCourseClass should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "CourseClass"),
    epic: EpicDeleteEntityRecord,
    processData: () => getListRecordAfterDeleteActions("CourseClass")
  }));
});
import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";
import { getListRecordAfterDeleteActions } from "../../../js/containers/entities/common/utils";

describe("Delete Entity epic tests", () => {
  it("EpicDeleteEntity should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Account"),
    epic: EpicDeleteEntityRecord,
    processData: () => getListRecordAfterDeleteActions("Account")
  }));
});
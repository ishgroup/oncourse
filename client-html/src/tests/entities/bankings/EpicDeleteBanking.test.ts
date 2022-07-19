import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete banking epic tests", () => {
  it("EpicDeleteBanking should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Banking"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Banking")
  }));
});
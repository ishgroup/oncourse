import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete discount epic tests", () => {
  it("EpicDeleteDiscount should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Discount"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Discount")
  }));
});
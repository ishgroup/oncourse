import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete corporate pass epic tests", () => {
  it("EpicDeleteCorporatePass should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "CorporatePass"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("CorporatePass")
  }));
});
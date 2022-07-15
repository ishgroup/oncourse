import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create transaction epic tests", () => {
  it("EpicCreateTransaction should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("AccountTransaction", mockedApi.db.getAccountTransaction(1)),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("AccountTransaction")
  }));
});

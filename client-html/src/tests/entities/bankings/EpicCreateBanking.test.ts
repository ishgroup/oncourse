import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create banking epic tests", () => {
  it("EpicCreateBanking should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.createBankingMock(), "Banking"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Banking")
  }));
});

import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get banking epic tests", () => {
  it("EpicGetBanking should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getBanking(1), "Banking"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getBanking(1), "Banking", state)
  }));
});
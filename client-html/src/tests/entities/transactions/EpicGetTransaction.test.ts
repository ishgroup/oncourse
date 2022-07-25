import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get transaction epic tests", () => {
  it("EpicGetTransaction should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getAccountTransaction(1), "AccountTransaction"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getAccountTransaction(1), "AccountTransaction", state)
  }));
});
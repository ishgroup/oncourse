import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get account epic tests", () => {
  it("EpicGetAccount should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getAccount(), "Account"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getAccount(), "Account", state)
  }));
});
import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get Entity epic tests", () => {
  it("EpicGetEntity should returns correct values", () => DefaultEpic({
    action: () => getEntityRecord(1, "Account"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getAccount(1), "Account", state)
  }));
});
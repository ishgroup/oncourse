import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get module epic tests", () => {
  it("EpicGetModule should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getModule(1), "Module"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getModule(1), "Module", state)
  }));
});
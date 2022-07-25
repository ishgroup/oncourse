import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get application epic tests", () => {
  it("EpicGetApplication should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getApplication(), "Application"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getApplication(), "Application", state)
  }));
});

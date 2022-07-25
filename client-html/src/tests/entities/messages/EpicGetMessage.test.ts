import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get message epic tests", () => {
  it("EpicGetMessage should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getMessage(1), "Message"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getMessage(1), "Message", state)
  }));
});
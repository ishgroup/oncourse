import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get waitingList epic tests", () => {
  it("EpicGetWaitingList should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getWaitingList(), "WaitingList"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getWaitingList(), "WaitingList", state)
  }));
});

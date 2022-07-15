import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create account epic tests", () => {
  it("EpicCreateAccount should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("Account", mockedApi.db.createNewAccount()),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Account")
  }));
});

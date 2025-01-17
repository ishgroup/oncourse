import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";
import { getListRecordAfterCreateActions } from "../../../js/containers/entities/common/utils";

describe("Create Entity epic tests", () => {
  it("EpicCreateEntity should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.createNewAccount(), "Account"),
    epic: EpicCreateEntityRecord,
    processData: () => getListRecordAfterCreateActions("Account")
  }));
});
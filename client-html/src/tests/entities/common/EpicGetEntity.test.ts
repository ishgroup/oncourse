import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";
import { getListRecordAfterGetActions } from "../../../js/containers/entities/common/utils";

describe("Get Entity epic tests", () => {
  it("EpicGetEntity should returns correct values", () => DefaultEpic({
    action: () => getEntityRecord(1, "Account"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getListRecordAfterGetActions(mockedApi.db.getAccount(1), "Account", state)
  }));
});
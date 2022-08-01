import { DefaultEpic } from "../../common/Default.Epic";
import { updateEntityRecord } from "../../../js/containers/entities/common/actions";
import { EpicUpdateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicUpdateEntityRecord";

describe("Update Entity epic tests", () => {
  it("EpicUpdateEntity should returns correct values", () => DefaultEpic({
    action: mockedApi => {
      const account = mockedApi.db.getAccount(1);
      return updateEntityRecord(account.id, "Account", account);
    },
    epic: EpicUpdateEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions("Account", state, mockedApi.db.getAccount(1).id)
  }));
});
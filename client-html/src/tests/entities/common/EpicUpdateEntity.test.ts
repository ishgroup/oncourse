import { EpicUpdateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicUpdateEntityRecord";
import { updateEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Update Entity epic tests", () => {
  it("EpicUpdateEntity should returns correct values", () => ({
    action: mockedApi => {
      const account = mockedApi.db.getAccount();
      return updateEntityRecord(account.id, "Account", account)
    },
    epic: EpicUpdateEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions("Account", state, mockedApi.db.getAccount().id)
  }));
});
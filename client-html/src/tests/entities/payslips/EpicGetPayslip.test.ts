import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get payslip epic tests", () => {
  it("EpicGetPayslip should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getPayslip(1), "Payslip"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getPayslip(1), "Payslip", state)
  }));
});
import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get paymentOut epic tests", () => {
  it("EpicGetPaymentOut should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getPaymentOut(1), "PaymentOut"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getPaymentOut(1), "PaymentOut", state)
  }));
});
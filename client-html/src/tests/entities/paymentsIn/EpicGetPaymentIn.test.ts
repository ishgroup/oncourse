import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get paymentIn epic tests", () => {
  it("EpicGetPaymentIn should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getPaymentIn(1), "PaymentIn"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getPaymentIn(1), "PaymentIn", state)
  }));
});
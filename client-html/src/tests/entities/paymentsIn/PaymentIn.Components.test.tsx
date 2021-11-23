import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import PaymentInsEditView from "../../../js/containers/entities/paymentsIn/components/PaymentInEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PaymentInsEditView", () => {
  mockedEditView({
    entity: "PaymentIn",
    EditView: PaymentInsEditView,
    record: mockecApi => mockecApi.db.getPaymentIn(1),
    render: (wrapper, initialValues) => {
      const inputs = wrapper.find("input");
      expect(inputs[0].attribs.value).toContain(initialValues.payerName);
      expect(inputs[1].attribs.value).toContain(initialValues.administrationCenterId);
      expect(inputs[2].attribs.value).toContain(initialValues.paymentInType);
      expect(inputs[3].attribs.value).toContain(initialValues.status);
      expect(inputs[4].attribs.value).toContain(initialValues.amount);
      expect(inputs[5].attribs.value).toContain(initialValues.accountInName);
      expect(inputs[6].attribs.value).toContain(initialValues.source);
      expect(inputs[7].attribs.value).toContain(initialValues.ccTransaction);
      expect(inputs[9].attribs.value).toContain(
        format(new Date(initialValues.datePayed), III_DD_MMM_YYYY).toString()
      );
      expect(inputs[11].attribs.value).toContain(initialValues.createdBy);
    }
  });
});

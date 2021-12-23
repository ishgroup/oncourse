import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import PaymentsOutEditView from "../../../js/containers/entities/paymentsOut/components/PaymentOutEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PaymentsOutEditView", () => {
  mockedEditView({
    entity: "PaymentOut",
    EditView: PaymentsOutEditView,
    record: mockecApi => mockecApi.db.getPaymentOut(1),
    render: (wrapper, initialValues) => {
      const inputs = wrapper.find("input");
      expect(inputs[0].attribs.value).toContain(initialValues.payeeName);
      expect(inputs[1].attribs.value).toContain(initialValues.administrationCenterName);
      expect(inputs[3].attribs.value).toContain(initialValues.status);
      expect(inputs[4].attribs.value).toContain("Account is disabled");
      expect(inputs[5].attribs.value).toContain(initialValues.amount);
      expect(inputs[6].attribs.value).toContain(
        format(new Date(initialValues.datePayed), III_DD_MMM_YYYY).toString()
      );
      expect(inputs[7].attribs.value).toContain(
        format(new Date(initialValues.dateBanked), III_DD_MMM_YYYY).toString()
      );
      expect(inputs[8].attribs.value).toContain(initialValues.createdBy);
    }
  });
});

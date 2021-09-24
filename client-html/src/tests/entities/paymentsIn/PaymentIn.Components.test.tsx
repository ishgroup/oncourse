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
      expect(wrapper.find(".textField").text()).toContain(initialValues.payerName);
      expect(wrapper.find("#administrationCenterId input").val()).toContain(initialValues.administrationCenterId);
      expect(wrapper.find(".textField").text()).toContain(initialValues.paymentInType);
      expect(wrapper.find(".textField").text()).toContain(initialValues.status);
      expect(wrapper.find(".textField").text()).toContain(initialValues.amount);
      expect(wrapper.find(".textField").text()).toContain(initialValues.source);
      expect(wrapper.find(".textField").text()).toContain(initialValues.ccTransaction);
      expect(wrapper.find(".textField").text()).toContain(
        format(new Date(initialValues.datePayed), III_DD_MMM_YYYY).toString()
      );

      expect(wrapper.find(".textField").text()).toContain(initialValues.createdBy);
    }
  });
});

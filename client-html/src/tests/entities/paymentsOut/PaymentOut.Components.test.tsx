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
      expect(wrapper.find(".textField").at(0).text()).toContain(initialValues.payeeName);
      expect(wrapper.find("#administrationCenterId").text()).toContain(initialValues.administrationCenterName);
      expect(wrapper.find(".textField").at(2).text()).toContain("No Value");
      expect(wrapper.find(".textField").at(3).text()).toContain(initialValues.status);
      expect(wrapper.find(".textField").at(4).text()).toContain("Account is disabled");
      expect(wrapper.find(".textField").at(5).text()).toContain(initialValues.amount);

      expect(wrapper.find(".textField").at(6).text()).toContain(
        format(new Date(initialValues.datePayed), III_DD_MMM_YYYY).toString()
      );

      expect(wrapper.find(".textField").at(7).text()).toContain(
        format(new Date(initialValues.dateBanked), III_DD_MMM_YYYY).toString()
      );

      expect(wrapper.find("#privateNotes").text()).toContain("No value");
      expect(wrapper.find(".textField").at(8).text()).toContain(initialValues.createdBy);
    }
  });
});

import VoucherProductEditView from "../../../js/containers/entities/voucherProducts/components/VoucherProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered VoucherProductEditView", () => {
  mockedEditView({
    entity: "VoucherProduct",
    EditView: VoucherProductEditView,
    record: mockecApi => mockecApi.db.getVoucherProduct(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name").text()).toContain(initialValues.name);
      expect(wrapper.find("#code").text()).toContain(initialValues.code);
      expect(wrapper.find("#liabilityAccountId").text()).toContain("No value");
      expect(wrapper.find("#expiryDays").text()).toContain(initialValues.expiryDays);
      expect(wrapper.find("#maxCoursesRedemption").text()).toContain(initialValues.maxCoursesRedemption);
      expect(wrapper.find("#feeExTax").text()).toContain(initialValues.feeExTax);
      expect(wrapper.find("#status").text()).toContain(initialValues.status);
      expect(wrapper.find("#description").text()).toContain(initialValues.description);
    }
  });
});

import VoucherProductEditView from "../../../js/containers/entities/voucherProducts/components/VoucherProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered VoucherProductEditView", () => {
  mockedEditView({
    entity: "VoucherProduct",
    EditView: VoucherProductEditView,
    record: mockecApi => mockecApi.db.getVoucherProduct(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name input").val()).toContain(initialValues.name);
      expect(wrapper.find("#code input").val()).toContain(initialValues.code);
      expect(wrapper.find("#liabilityAccountId input").val()).toContain("No value");
      expect(wrapper.find("#expiryDays input").val()).toContain(initialValues.expiryDays);
      expect(wrapper.find("#maxCoursesRedemption input").val()).toContain(initialValues.maxCoursesRedemption);
      expect(wrapper.find("#feeExTax input").val()).toContain(initialValues.feeExTax);
      expect(wrapper.find("#status input").val()).toContain(initialValues.status);
      expect(wrapper.find("#description input").val()).toContain(initialValues.description);
    }
  });
});

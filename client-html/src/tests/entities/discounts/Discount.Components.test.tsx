import DiscountEditView from "../../../js/containers/entities/discounts/components/DiscountEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered DiscountEditView", () => {
  mockedEditView({
    entity: "Discount",
    EditView: DiscountEditView,
    record: mockecApi => mockecApi.db.getDiscount(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name input").val()).toContain(initialValues.name);
      expect(wrapper.find("#discountType input").val()).toContain("Percent");
      expect(wrapper.find("#discountPercent input").val()).toContain(initialValues.discountPercent * 100);
      expect(wrapper.find("#rounding input").val()).toContain(initialValues.rounding);

      expect(wrapper.find("#predictedStudentsPercentage input").val()).toContain(initialValues.predictedStudentsPercentage);
      expect(wrapper.find("#code input").val()).toContain(initialValues.code);

      expect(wrapper.find("#minEnrolments input").val()).toContain(initialValues.minEnrolments);
      expect(wrapper.find("#minValue input").val()).toContain(initialValues.minValue);
    }
  });
});

import DiscountEditView from "../../../js/containers/entities/discounts/components/DiscountEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered DiscountEditView", () => {
  mockedEditView({
    entity: "Discount",
    EditView: DiscountEditView,
    record: mockecApi => mockecApi.db.getDiscount(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name input").val()).toContain(initialValues.name);
      expect(wrapper.find("#discountType input").val()).toContain("Discount percent");
      expect(wrapper.find("#discountPercent input").val()).toContain(initialValues.discountPercent * 100);
      expect(wrapper.find("#rounding input").val()).toContain(initialValues.rounding);
      expect(wrapper.find("#discountMin input").val()).toContain("No value");
      expect(wrapper.find("#discountMax input").val()).toContain("No value");

      expect(wrapper.find("#cosAccount input").val()).toContain("No value");
      expect(wrapper.find("#predictedStudentsPercentage input").val()).toContain(initialValues.predictedStudentsPercentage);
      expect(wrapper.find('#General input[type="checkbox"]').at(0).props().checked).toEqual(true);
      expect(wrapper.find("#code input").val()).toContain(initialValues.code);
      expect(wrapper.find('#General input[type="checkbox"]').at(1).props().checked).toEqual(true);
      expect(wrapper.find('#General input[type="checkbox"]').at(2).props().checked).toEqual(false);
      expect(wrapper.find("#description input").val()).toContain("No value");

      expect(wrapper.find('#Students input[type="checkbox"]').at(0).props().checked).toEqual(true);
      expect(wrapper.find("#studentEnrolledWithinDays input").val()).toContain("No value");
      expect(wrapper.find("#studentAgeUnder input").val()).toContain("No value");
      expect(wrapper.find("#studentPostcode input").val()).toContain("No value");
      expect(wrapper.find('#Students input[type="checkbox"]').at(1).props().checked).toEqual(false);
      expect(wrapper.find('#Students input[type="checkbox"]').at(2).props().checked).toEqual(true);
      expect(wrapper.find('#Students input[type="checkbox"]').at(3).props().checked).toEqual(true);

      expect(wrapper.find('#Classes input[type="checkbox"]').at(0).props().checked).toEqual(false);
      expect(wrapper.find("#minEnrolments input").val()).toContain(initialValues.minEnrolments);
      expect(wrapper.find("#minValue input").val()).toContain(initialValues.minValue);
    }
  });
});

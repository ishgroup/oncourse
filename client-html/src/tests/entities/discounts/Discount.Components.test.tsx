import DiscountEditView from "../../../js/containers/entities/discounts/components/DiscountEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered DiscountEditView", () => {
  mockedEditView({
    entity: "Discount",
    EditView: DiscountEditView,
    record: mockecApi => mockecApi.db.getDiscount(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name").text()).toContain(initialValues.name);
      expect(wrapper.find("#discountType").text()).toContain("Discount percent");
      expect(wrapper.find("#discountPercent").text()).toContain(initialValues.discountPercent * 100);
      expect(wrapper.find("#rounding").text()).toContain(initialValues.rounding);
      expect(wrapper.find("#discountMin").text()).toContain("No value");
      expect(wrapper.find("#discountMax").text()).toContain("No value");

      expect(wrapper.find("#cosAccount").text()).toContain("No value");
      expect(wrapper.find("#predictedStudentsPercentage").text()).toContain(initialValues.predictedStudentsPercentage);
      expect(wrapper.find('#General input[type="checkbox"]').at(0).props().checked).toEqual(true);
      expect(wrapper.find("#code").text()).toContain(initialValues.code);
      expect(wrapper.find('#General input[type="checkbox"]').at(1).props().checked).toEqual(true);
      expect(wrapper.find('#General input[type="checkbox"]').at(2).props().checked).toEqual(false);
      expect(wrapper.find("#description").text()).toContain("No value");

      expect(wrapper.find('#Students input[type="checkbox"]').at(0).props().checked).toEqual(true);
      expect(wrapper.find("#studentEnrolledWithinDays").text()).toContain("No value");
      expect(wrapper.find("#studentAgeUnder").text()).toContain("No value");
      expect(wrapper.find("#studentPostcode").text()).toContain("No value");
      expect(wrapper.find('#Students input[type="checkbox"]').at(1).props().checked).toEqual(false);
      expect(wrapper.find('#Students input[type="checkbox"]').at(2).props().checked).toEqual(true);
      expect(wrapper.find('#Students input[type="checkbox"]').at(3).props().checked).toEqual(true);

      expect(wrapper.find('#Classes input[type="checkbox"]').at(0).props().checked).toEqual(false);
      expect(wrapper.find("#minEnrolments").text()).toContain(initialValues.minEnrolments);
      expect(wrapper.find("#minValue").text()).toContain(initialValues.minValue);
    }
  });
});

import SiteEditView from "../../../js/containers/entities/sites/components/SiteEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered SiteEditView", () => {
  mockedEditView({
    entity: "Site",
    EditView: SiteEditView,
    record: mockecApi => mockecApi.db.getSite(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name").text()).toContain(initialValues.name);
      expect(wrapper.find("#tags").text()).toContain("#name 1 ");

      expect(wrapper.find('input[type="checkbox"]').at(0).props().checked).toEqual(true);
      expect(wrapper.find('input[type="checkbox"]').at(1).props().checked).toEqual(false);
      expect(wrapper.find('input[type="checkbox"]').at(2).props().checked).toEqual(true);

      expect(wrapper.find("#street").text()).toContain(initialValues.street);
      expect(wrapper.find("#suburb").text()).toContain(initialValues.suburb);
      expect(wrapper.find("#state").text()).toContain("No value");
      expect(wrapper.find("#postcode").text()).toContain(initialValues.postcode);
      expect(wrapper.find("#country").text()).toContain(initialValues.country.name);
      expect(wrapper.find("#timezone").text()).toContain(initialValues.timezone);
      expect(wrapper.find("#drivingDirections").text()).toContain("No value");
      expect(wrapper.find("#publicTransportDirections").text()).toContain("No value");
      expect(wrapper.find("#specialInstructions").text()).toContain("No value");
    }
  });
});

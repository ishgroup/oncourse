import SiteEditView from "../../../js/containers/entities/sites/components/SiteEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered SiteEditView", () => {
  mockedEditView({
    entity: "Site",
    EditView: SiteEditView,
    record: mockecApi => mockecApi.db.getSite(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name input").val()).toContain(initialValues.name);
      expect(wrapper.find("#street input").val()).toContain(initialValues.street);
      expect(wrapper.find("#suburb input").val()).toContain(initialValues.suburb);
      expect(wrapper.find("#postcode input").val()).toContain(initialValues.postcode);
      expect(wrapper.find("#country input").val()).toContain(initialValues.country.name);
      expect(wrapper.find("#timezone input").val()).toContain(initialValues.timezone);
    }
  });
});
